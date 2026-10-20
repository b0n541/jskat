package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.file.*;
import java.util.*;

class DurableTournamentRunStoreTest {
    @Test
    void unsupportedTmpfsFailsBeforeResolverAndNeverPublishesReady() throws Exception {
        Path parent = Files.createTempDirectory("tournament-unsupported-");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                !Files.getFileStore(parent).type().equals("ext4"),
                "This fixture requires an unsupported temporary filesystem");
        Path output = parent.resolve("run");
        var requested =
                new TournamentConfigurationReader()
                        .read(
                                TournamentConfigurationTest.VALID.replace(
                                        "\"output\":\"runs/test\"",
                                        "\"output\":\"" + output + "\""),
                                parent.resolve("config.json"))
                        .value();
        // Keep the path replacement independent of the shared fixture's relative output spelling.
        requested =
                new TournamentConfiguration(
                        requested.schemaVersion(),
                        requested.ruleset(),
                        requested.seed(),
                        requested.epochs(),
                        new ConfiguredPath(output.toString(), output),
                        requested.competitors(),
                        requested.configurationFile(),
                        requested.originalJson());
        var configuration = requested;
        assertThatThrownBy(
                        () ->
                                DurableTournamentRunStore.create(
                                        configuration,
                                        0,
                                        new TournamentBuildIdentity(
                                                "a".repeat(40), Optional.empty()),
                                        null,
                                        () -> false))
                .isInstanceOf(java.io.IOException.class)
                .hasMessageContaining("Unsupported filesystem");
        assertThat(Files.exists(output.resolve("ready.json"))).isFalse();
    }

    @Test
    void readyRunResumesOnlyRetainedInputsAndRejectsLiveLockAndTampering() throws Exception {
        Path parent = TournamentStoreDurabilityTest.RUNS;
        Files.createDirectories(parent);
        org.junit.jupiter.api.Assumptions.assumeTrue(
                Files.getFileStore(parent).type().equals("ext4"),
                "Set JSKAT_TOURNAMENT_EXT4_TEST_ROOT for ext4 acceptance");
        Path output = parent.resolve("store-" + UUID.randomUUID());
        var initial =
                new TournamentConfigurationReader()
                        .read(
                                TournamentConfigurationTest.VALID.replace(
                                        "runs/test", output.toString()),
                                output.resolveSibling("config.json"))
                        .value();
        var requested =
                new TournamentConfiguration(
                        initial.schemaVersion(),
                        initial.ruleset(),
                        initial.seed(),
                        initial.epochs(),
                        new ConfiguredPath(output.toString(), output),
                        initial.competitors(),
                        initial.configurationFile(),
                        initial.originalJson());
        var build = new TournamentBuildIdentity("a".repeat(40), Optional.empty());
        var resolver = new DefaultTournamentInputResolver(parent.resolve("unused-model-store"));
        UUID run;
        try (var store =
                DurableTournamentRunStore.create(requested, 7, build, resolver, () -> false)) {
            run = store.runId();
            assertThat(store.schedule()).hasSize(12);
            var fixed = store.captureCommittedSet();
            store.publishReports(
                    fixed,
                    Map.of(
                            "summary.json",
                            "{}".getBytes(),
                            "leaderboard.csv",
                            "name\nRandom\n".getBytes(),
                            "game-scores.csv",
                            "game\n".getBytes()),
                    CanonicalJson.parse("{\"outcome\":\"CANCELLED\"}"));
            assertThat(new String(store.readCurrentReports().exports().get("leaderboard.csv")))
                    .isEqualTo("name\nRandom\n");
            assertThatThrownBy(
                            () ->
                                    DurableTournamentRunStore.resume(
                                            requested, build, resolver, false, () -> false))
                    .hasMessageContaining("busy");
        }
        try (var store =
                DurableTournamentRunStore.resume(requested, build, resolver, false, () -> false)) {
            assertThat(store.runId()).isEqualTo(run);
            assertThat(store.committedGames()).isEmpty();
        }
        Files.writeString(output.resolve("inputs/original-config.json"), "corrupt");
        assertThatThrownBy(
                        () ->
                                DurableTournamentRunStore.resume(
                                        requested, build, resolver, false, () -> false))
                .hasMessageContaining("inventory");
    }
}
