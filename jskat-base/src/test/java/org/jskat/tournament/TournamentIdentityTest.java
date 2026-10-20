package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

class TournamentIdentityTest {
    @Test
    void canonicalRequestIgnoresFormattingAndRosterOrderingButRetainsSeedPresenceAndOutput() {
        var reader = new TournamentConfigurationReader();
        var configuration =
                reader.read(TournamentConfigurationTest.VALID, Path.of("/tmp/config.json")).value();
        var reversed =
                new TournamentConfiguration(
                        configuration.schemaVersion(),
                        configuration.ruleset(),
                        configuration.seed(),
                        configuration.epochs(),
                        configuration.output(),
                        configuration.competitors().reversed(),
                        configuration.configurationFile(),
                        "different formatting");
        assertThat(TournamentIdentities.requestedDigest(configuration))
                .isEqualTo(TournamentIdentities.requestedDigest(reversed));
        var explicit =
                reader.read(
                                TournamentConfigurationTest.VALID.replace(
                                        "\"epochs\":2", "\"seed\":\"0\",\"epochs\":2"),
                                Path.of("/tmp/config.json"))
                        .value();
        assertThat(TournamentIdentities.requestedDigest(configuration))
                .isNotEqualTo(TournamentIdentities.requestedDigest(explicit));
        var moved =
                reader.read(
                                TournamentConfigurationTest.VALID,
                                Path.of("/tmp/elsewhere/config.json"))
                        .value();
        assertThat(TournamentIdentities.requestedDigest(configuration))
                .isNotEqualTo(TournamentIdentities.requestedDigest(moved));
    }

    @Test
    void versionOneSeedAndArtifactGoldenVectors() {
        assertThat(Long.toUnsignedString(TournamentIdentities.deriveSeed(0L, "deck", "epoch:0")))
                .isEqualTo("1537317221572004781");
        assertThat(
                        Long.toUnsignedString(
                                TournamentIdentities.deriveSeed(
                                        -1L, "player-instance", "e\u0301", "seat:2")))
                .isEqualTo("9947724391513880013");
        var artifacts =
                List.of(
                        new TournamentArtifact("é.onnx", 3, "a".repeat(64)),
                        new TournamentArtifact("z.onnx", 0, "0".repeat(64)));
        assertThat(TournamentIdentities.artifactFingerprint(artifacts))
                .isEqualTo("0c0158ba33f7b5848ed55d146138e44fe14b7fab088e0332f86449d9bcc0147d");
    }

    @Test
    void canonicalNumbersRetainFiniteDecimalData() throws Exception {
        var json =
                new com.fasterxml.jackson.databind.ObjectMapper()
                        .enable(
                                com.fasterxml.jackson.databind.DeserializationFeature
                                        .USE_BIG_DECIMAL_FOR_FLOATS);
        assertThat(
                        CanonicalJson.encode(
                                json.readTree(
                                        "{\"tiny\":0.0000010,\"rating\":1500.00,\"negativeZero\":-0.0}")))
                .isEqualTo("{\"negativeZero\":0,\"rating\":1500,\"tiny\":0.000001}");
        assertThatThrownBy(
                        () ->
                                CanonicalJson.encode(
                                        com.fasterxml.jackson.databind.node.DoubleNode.valueOf(
                                                Double.NaN)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requestedCanonicalBytesMatchIndependentVersionedFixture() throws Exception {
        var configuration =
                new TournamentConfigurationReader()
                        .read(TournamentConfigurationTest.VALID, Path.of("/tmp/config.json"))
                        .value();
        try (var fixture = getClass().getResourceAsStream("requested-v1.json")) {
            assertThat(TournamentIdentities.requestedCanonicalJson(configuration))
                    .isEqualTo(
                            new String(
                                    fixture.readAllBytes(),
                                    java.nio.charset.StandardCharsets.UTF_8));
        }
        assertThat(TournamentIdentities.requestedDigest(configuration))
                .isEqualTo("cae24f30ccc084c15cbcf4b2e9dd06a5f9009246614c17b84ca67c5416a47815");
    }

    @Test
    void resolvedIdentityIncludesEffectiveSeedAndCompleteBuildIdentity() {
        var requested =
                new TournamentConfigurationReader()
                        .read(TournamentConfigurationTest.VALID, Path.of("/tmp/config.json"))
                        .value();
        var roster =
                requested.competitors().stream()
                        .map(
                                c ->
                                        new ResolvedTournamentCompetitor(
                                                c,
                                                c.implementation().className(),
                                                null,
                                                null,
                                                null,
                                                List.of()))
                        .toList();
        var clean = new TournamentBuildIdentity("a".repeat(40), java.util.Optional.empty());
        var a = new ResolvedTournamentConfiguration(requested, 0, clean, roster);
        var b = new ResolvedTournamentConfiguration(requested, 1, clean, roster);
        assertThat(TournamentIdentities.resolvedDigest(a))
                .isNotEqualTo(TournamentIdentities.resolvedDigest(b));
        var dirty =
                new ResolvedTournamentConfiguration(
                        requested,
                        0,
                        new TournamentBuildIdentity(
                                clean.commit(), java.util.Optional.of("b".repeat(64))),
                        roster);
        assertThat(TournamentIdentities.resolvedDigest(a))
                .isNotEqualTo(TournamentIdentities.resolvedDigest(dirty));
    }

    @Test
    void canonicalObjectOrdersKeysAfterUnicodeNormalization() throws Exception {
        var root = CanonicalJson.object();
        root.put("e\u0301", 1);
        root.put("f", 2);
        assertThat(CanonicalJson.encode(root)).isEqualTo("{\"f\":2,\"é\":1}");
    }
}
