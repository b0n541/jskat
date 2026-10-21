package org.jskat.tournament;

import org.junit.jupiter.api.Test;
import java.io.*;
import static org.assertj.core.api.Assertions.assertThat;

class TournamentCommandTest {
    @org.junit.jupiter.api.io.TempDir java.nio.file.Path directory;
    @Test void rejectsMalformedControlsWithoutReadingConfiguration() {
        for (String[] args : new String[][] {
                {"--resume"}, {"--tournament", "missing.json", "--recover-stale-lock"},
                {"--tournament", "missing.json", "--workers", "0"},
                {"--tournament", "missing.json", "--workers", "715827883"},
                {"--tournament", "missing.json", "--verbose", "--verbose"},
                {"--tournament", "missing.json", "--developer-mode"},
                {"--tournament=missing.json"}, {"--tournament-help", "--resume"},
                {"--tournament", "missing.json", "--stall-warning-seconds", "1.5"}}) {
            var err = new ByteArrayOutputStream();
            assertThat(new TournamentCommand().execute(args, new PrintStream(OutputStream.nullOutputStream()), new PrintStream(err))).isEqualTo(2);
            assertThat(err.toString()).contains("Usage:").doesNotContain("FILE_READ");
        }
    }
    @Test void printsEverySharedConfigurationIssueInStablePathOrder() throws Exception {
        var config = java.nio.file.Files.writeString(directory.resolve("invalid.json"), "{\"schemaVersion\":2,\"epochs\":1.5,\"unknown\":true}");
        var errors = new ByteArrayOutputStream();
        var output = new ByteArrayOutputStream();
        int exit = new TournamentCommand().execute(new String[]{"--tournament", config.toString()}, new PrintStream(output), new PrintStream(errors));
        assertThat(exit).isEqualTo(2);
        var expected = new TournamentService().read(config).issues().stream()
                .map(i -> i.severity() + " " + i.code() + " " + i.path() + ": " + i.message()).toList();
        assertThat(errors.toString().lines().toList()).containsExactlyElementsOf(expected);
        assertThat(output.toString()).contains("Status: CONFIGURATION_ERROR");
    }

    @Test void coalescesFastPublicationUpdatesButShowsStallsAndFailuresImmediately() {
        var bytes = new ByteArrayOutputStream();
        var display = new TournamentCommand.ProgressOutput(new PrintStream(bytes), false);
        for (int i = 0; i < 10; i++) {
            var metrics = new TournamentMetrics(i, java.time.Duration.ofMillis(i * 10), 10,
                    java.util.Optional.empty(), java.util.List.of(), java.util.Optional.of(java.util.UUID.randomUUID()), java.util.Optional.empty());
            display.onProgress(new TournamentProgress(TournamentProgress.State.RUNNING, 60, i, i, 0, i / 6, false, true, "Epoch reports published", metrics));
        }
        assertThat(bytes.toString().lines().count()).isEqualTo(1);
        display.onProgress(new TournamentProgress(TournamentProgress.State.RUNNING, 60, 10, 10, 0, 1, false, true, "Stall warning: game a", TournamentMetrics.empty()));
        display.onProgress(new TournamentProgress(TournamentProgress.State.FAILED, 60, 10, 10, 1, 1, false, true, "Callback failed", TournamentMetrics.empty()));
        assertThat(bytes.toString()).contains("Stall warning: game a", "FAILED");
    }

    @Test void standaloneHelpIsDisplayFreeAndSuccessful() {
        var out = new ByteArrayOutputStream();
        assertThat(new TournamentCommand().execute(new String[]{"--tournament-help"}, new PrintStream(out), System.err)).isZero();
        assertThat(out.toString()).contains("--workers", "--stall-warning-seconds", "--recover-stale-lock");
        assertThat(TournamentCommand.handles(new String[]{"--developer-mode"})).isFalse();
        assertThat(TournamentCommand.handles(new String[]{"--workers=2"})).isTrue();
    }
}
