package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.*;

class TournamentStrictValidationTest {
    @TempDir Path directory;

    @ParameterizedTest
    @ValueSource(strings = {"0", "18446744073709551615", "000000000000000000000000000001"})
    void acceptsUnsignedSeedBoundaries(String seed) {
        var result =
                new TournamentConfigurationReader()
                        .read(
                                TournamentConfigurationTest.VALID.replace(
                                        "\"epochs\":2", "\"seed\":\"" + seed + "\",\"epochs\":2"),
                                directory.resolve("config.json"));
        assertThat(result.valid()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "18446744073709551616", "1.2", "+1", " 1", ""})
    void rejectsInvalidSeed(String seed) {
        var result =
                new TournamentConfigurationReader()
                        .read(
                                TournamentConfigurationTest.VALID.replace(
                                        "\"epochs\":2", "\"seed\":\"" + seed + "\",\"epochs\":2"),
                                directory.resolve("config.json"));
        assertThat(result.issues()).extracting(ValidationIssue::code).contains("INVALID_SEED");
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"0", "-1", "1.0", "9223372036854775808", "9223372036854775807", "357913942"})
    void rejectsInvalidOrUnaddressableEpochCount(String epochs) {
        var result =
                new TournamentConfigurationReader()
                        .read(
                                TournamentConfigurationTest.VALID.replace(
                                        "\"epochs\":2", "\"epochs\":" + epochs),
                                directory.resolve("config.json"));
        assertThat(result.valid()).isFalse();
    }

    @Test
    void rejectsDuplicateKeysTrailingContentAndMalformedUtf8() throws Exception {
        var reader = new TournamentConfigurationReader();
        assertThat(
                        reader.read(
                                        TournamentConfigurationTest.VALID.replace(
                                                "\"epochs\":2", "\"epochs\":2,\"epochs\":3"),
                                        directory.resolve("c.json"))
                                .issues())
                .extracting(ValidationIssue::code)
                .containsExactly("INVALID_JSON");
        assertThat(
                        reader.read(
                                        TournamentConfigurationTest.VALID + " {}",
                                        directory.resolve("c.json"))
                                .issues())
                .extracting(ValidationIssue::code)
                .containsExactly("INVALID_JSON");
        var path = directory.resolve("invalid.json");
        Files.write(path, new byte[] {(byte) 0xc3, 0x28});
        assertThat(reader.read(path).issues())
                .extracting(ValidationIssue::code)
                .containsExactly("INVALID_UTF8");
    }

    @Test
    void normalizesUnicodeAndPreservesUnexpandedPathProvenance() {
        var result =
                new TournamentConfigurationReader()
                        .read(
                                TournamentConfigurationTest.VALID
                                        .replace(" Random ", " e\\u0301 ")
                                        .replace("runs/test", "~/runs/$HOME"),
                                directory.resolve("config.json"));
        assertThat(result.value().competitors().getFirst().name()).isEqualTo("é");
        assertThat(result.value().output().configured()).isEqualTo("~/runs/$HOME");
        assertThat(result.value().output().absolute()).isEqualTo(directory.resolve("~/runs/$HOME"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{\"source\":\"active\"}",
                "{\"source\":\"installed\",\"version\":\"1.4.0\"}",
                "{\"source\":\"development\",\"path\":\"../model\"}"
            })
    void acceptsExactModelSourceVariants(String model) {
        var json =
                TournamentConfigurationTest.VALID.replace(
                        "\"implementation\":\"algorithmic\"",
                        "\"implementation\":\"ml\",\"model\":" + model);
        assertThat(
                        new TournamentConfigurationReader()
                                .read(json, directory.resolve("config.json"))
                                .valid())
                .isTrue();
    }

    @Test
    void rejectsInapplicableSourceFieldsAndInstalledTraversal() {
        var json =
                TournamentConfigurationTest.VALID.replace(
                        "\"implementation\":\"algorithmic\"",
                        "\"implementation\":\"ml\",\"model\":{\"source\":\"installed\",\"version\":\"../../candidate\",\"path\":\"somewhere\"}");
        assertThat(
                        new TournamentConfigurationReader()
                                .read(json, directory.resolve("config.json"))
                                .issues())
                .extracting(ValidationIssue::code)
                .containsExactly("UNKNOWN_FIELD", "INVALID_VERSION");
    }

    @Test
    void rejectsUnpairedUnicodeSurrogateAndAcceptsLargestAddressableTwoPlayerSchedule() {
        var reader = new TournamentConfigurationReader();
        var invalid = TournamentConfigurationTest.VALID.replace(" Random ", "\\uD800");
        assertThat(reader.read(invalid, directory.resolve("c.json")).issues())
                .extracting(ValidationIssue::code)
                .contains("INVALID_UNICODE");
        assertThat(
                        reader.read(
                                        TournamentConfigurationTest.VALID.replace(
                                                "\"epochs\":2", "\"epochs\":357913941"),
                                        directory.resolve("c.json"))
                                .value()
                                .gameCount())
                .isEqualTo(2147483646L);
    }
}
