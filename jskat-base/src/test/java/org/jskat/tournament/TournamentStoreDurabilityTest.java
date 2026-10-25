package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;

class TournamentStoreDurabilityTest {
    static final Path RUNS =
            Path.of(
                            System.getenv()
                                    .getOrDefault(
                                            "JSKAT_TOURNAMENT_EXT4_TEST_ROOT",
                                            System.getProperty("java.io.tmpdir")))
                    .resolve("jskat-tournament-store-tests");
    static final TournamentBuildIdentity BUILD =
            new TournamentBuildIdentity("a".repeat(40), Optional.empty());
    static final TournamentInputResolver RESOLVER =
            new DefaultTournamentInputResolver(RUNS.resolve("unused-model-store"));

    static TournamentConfiguration requested() throws IOException {
        Files.createDirectories(RUNS);
        org.junit.jupiter.api.Assumptions.assumeTrue(
                Files.getFileStore(RUNS).type().equals("ext4"),
                "Set JSKAT_TOURNAMENT_EXT4_TEST_ROOT to a local ext4 directory for durability"
                        + " acceptance");
        Path output = RUNS.resolve("acceptance-" + UUID.randomUUID());
        return new TournamentConfigurationReader()
                .read(
                        TournamentConfigurationTest.VALID.replace("runs/test", output.toString()),
                        RUNS.resolve("configuration.json"))
                .value();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "before-file-force",
                "before-rename",
                "after-rename",
                "after-directory-force"
            })
    void readyCrashWindowsNeverCountTemporaryFilesAsReadiness(String boundary) throws Exception {
        var requested = requested();
        Path root = requested.output().absolute();
        var io =
                new TournamentPublicationIO() {
                    boolean moved, failed;

                    boolean readyTemp() throws IOException {
                        try (var files = Files.list(root)) {
                            return files.anyMatch(
                                    p ->
                                            p.getFileName()
                                                    .toString()
                                                    .matches("\\.tmp-.*-ready\\.json"));
                        }
                    }

                    void fail() throws IOException {
                        failed = true;
                        throw new IOException("Injected " + boundary);
                    }

                    public void forceFile(FileChannel file) throws IOException {
                        if (!failed && boundary.equals("before-file-force") && readyTemp()) fail();
                        LOCAL.forceFile(file);
                    }

                    public void atomicMove(Path temp, Path target, boolean replace)
                            throws IOException {
                        if (target.getFileName().toString().equals("ready.json")) {
                            if (boundary.equals("before-rename")) fail();
                            LOCAL.atomicMove(temp, target, replace);
                            moved = true;
                            if (boundary.equals("after-rename")) fail();
                        } else LOCAL.atomicMove(temp, target, replace);
                    }

                    public void forceDirectory(Path directory) throws IOException {
                        LOCAL.forceDirectory(directory);
                        if (moved && !failed && boundary.equals("after-directory-force")) fail();
                    }
                };
        assertThatThrownBy(
                        () ->
                                DurableTournamentRunStore.create(
                                        requested, 0, BUILD, RESOLVER, () -> false, io))
                .hasMessageContaining("Injected");
        if (boundary.startsWith("before")) {
            assertThatThrownBy(
                            () ->
                                    DurableTournamentRunStore.resume(
                                            requested, BUILD, RESOLVER, false, () -> false))
                    .isInstanceOf(IOException.class);
            assertThat(Files.exists(root.resolve("ready.json"))).isFalse();
        } else
            try (var resumed =
                    DurableTournamentRunStore.resume(
                            requested, BUILD, RESOLVER, false, () -> false)) {
                assertThat(resumed.committedGames()).isEmpty();
            }
    }

    @Test
    void staleMetadataRequiresExplicitRecoveryAndRetainsExactPriorBytes() throws Exception {
        var c = requested();
        UUID run;
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false)) {
            run = store.runId();
        }
        byte[] previous =
                TournamentRecords.encode(
                        run,
                        "lock-owner",
                        CanonicalJson.parse("{\"released\":false,\"pid\":999999999}"));
        Files.write(c.output().absolute().resolve("lock-owner.json"), previous);
        assertThatThrownBy(
                        () ->
                                DurableTournamentRunStore.resume(
                                        c, BUILD, RESOLVER, false, () -> false))
                .hasMessageContaining("explicit recovery");
        try (var store = DurableTournamentRunStore.resume(c, BUILD, RESOLVER, true, () -> false)) {
            try (var records = Files.list(c.output().absolute().resolve("lock-history"))) {
                var audit =
                        TournamentRecords.decode(
                                Files.readAllBytes(records.findFirst().orElseThrow()),
                                run,
                                "lock-recovery");
                assertThat(Base64.getDecoder().decode(audit.path("previousBase64").asText()))
                        .isEqualTo(previous);
            }
            assertThatThrownBy(
                            () ->
                                    DurableTournamentRunStore.resume(
                                            c, BUILD, RESOLVER, true, () -> false))
                    .hasMessageContaining("live OS lock");
        }
    }

    @Test
    void malformedAuthoritativeRecordsRefuseWhileInterruptedTemporaryRecordsRemainEvidence()
            throws Exception {
        var c = requested();
        try (var ignored = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false)) {}
        Path games = c.output().absolute().resolve("games");
        Path temporary = games.resolve(".tmp-" + UUID.randomUUID() + "-game.json");
        Files.writeString(temporary, "interrupted");
        try (var store = DurableTournamentRunStore.resume(c, BUILD, RESOLVER, false, () -> false)) {
            assertThat(store.committedGames()).isEmpty();
        }
        assertThat(Files.readString(temporary)).isEqualTo("interrupted");
        Files.writeString(games.resolve("unknown.json"), "{}");
        assertThatThrownBy(
                        () ->
                                DurableTournamentRunStore.resume(
                                        c, BUILD, RESOLVER, false, () -> false))
                .hasMessageContaining("games/unknown.json");
    }

    @Test
    void gameCommitAcknowledgesOnlyAfterDirectoryFlushAndResumeSkipsUncertainPublication()
            throws Exception {
        var c = requested();
        var io =
                new TournamentPublicationIO() {
                    public void forceFile(FileChannel file) throws IOException {
                        LOCAL.forceFile(file);
                    }

                    public void atomicMove(Path temp, Path target, boolean replace)
                            throws IOException {
                        LOCAL.atomicMove(temp, target, replace);
                    }

                    public void forceDirectory(Path directory) throws IOException {
                        LOCAL.forceDirectory(directory);
                        if (directory.getFileName().toString().equals("games"))
                            throw new IOException("uncertain game acknowledgment");
                    }
                };
        TournamentGameResult result;
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false, io)) {
            UUID session = UUID.randomUUID();
            store.startSession(session, CanonicalJson.object());
            result =
                    new TournamentGameCore()
                            .play(
                                    store.schedule().getFirst(),
                                    TournamentGameCoreTest.passing(),
                                    TournamentIdentities.resolvedDigest(store.configuration()),
                                    UUID.randomUUID(),
                                    session,
                                    List.of());
            assertThatThrownBy(() -> store.commit(result))
                    .hasMessageContaining("uncertain game acknowledgment");
            assertThat(store.committedGames()).isEmpty();
        }
        try (var resumed =
                DurableTournamentRunStore.resume(c, BUILD, RESOLVER, false, () -> false)) {
            assertThat(resumed.committedGames()).containsExactly(result);
            assertThatThrownBy(() -> resumed.commit(result)).isInstanceOf(IOException.class);
        }
    }

    @Test
    void backgroundReportsAcceptAuthenticSnapshotsButRejectForgeryAndRegression() throws Exception {
        var c = requested();
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false)) {
            UUID session = UUID.randomUUID();
            store.startSession(session, CanonicalJson.object());
            DurableTournamentRunStore.CommittedSet earlier = null;
            for (var game : store.schedule().subList(0, 2)) {
                store.commit(new TournamentGameCore().play(game, TournamentGameCoreTest.passing(),
                        TournamentIdentities.resolvedDigest(store.configuration()),
                        UUID.randomUUID(), session, List.of()));
                if (earlier == null) earlier = store.captureCommittedSet();
            }
            var fixed = earlier;
            var exports = Map.of("summary.json", "{}".getBytes(), "leaderboard.csv", new byte[0],
                    "game-scores.csv", new byte[0]);
            var metadata = CanonicalJson.object().put("outcome", "RUNNING");
            var published = store.publishReportSnapshot(fixed, exports, metadata);
            assertThat(DurableTournamentRunStore.inspectReports(c.output().absolute(), store.runId())
                    .setDigest()).isEqualTo(fixed.digest()).isEqualTo(published.setDigest());
            assertThatThrownBy(store::readCurrentReports).hasMessageContaining("current verified committed set");
            assertThatThrownBy(() -> store.publishReports(fixed, exports, metadata))
                    .hasMessageContaining("stale or foreign");
            var foreign = new DurableTournamentRunStore.CommittedSet(UUID.randomUUID(), fixed.digest(),
                    fixed.games(), fixed.gameChecksums(), fixed.completeEpochs());
            assertThatThrownBy(() -> store.publishReportSnapshot(foreign, exports, metadata))
                    .hasMessageContaining("foreign or inconsistent");
            var forged = new DurableTournamentRunStore.CommittedSet(fixed.runId(), fixed.digest(),
                    List.of(), fixed.gameChecksums(), fixed.completeEpochs());
            assertThatThrownBy(() -> store.publishReportSnapshot(forged, exports, metadata))
                    .hasMessageContaining("foreign or inconsistent");
            store.publishReports(store.captureCommittedSet(), exports, metadata);
            assertThatThrownBy(() -> store.publishReportSnapshot(fixed, exports, metadata))
                    .hasMessageContaining("backwards");
            assertThat(store.readCurrentReports().setDigest()).isEqualTo(store.captureCommittedSet().digest());
        }
    }

    @Test
    void completedVerificationIsReadOnlyAndMissingExportsRequireOnlyPublicationRecovery()
            throws Exception {
        var c = requested();
        UUID session = UUID.randomUUID();
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false)) {
            store.startSession(session, CanonicalJson.object());
            for (var game : store.schedule())
                store.commit(
                        new TournamentGameCore()
                                .play(
                                        game,
                                        TournamentGameCoreTest.passing(),
                                        TournamentIdentities.resolvedDigest(store.configuration()),
                                        UUID.randomUUID(),
                                        session,
                                        List.of()));
            store.endSession(session, "COMPLETED", CanonicalJson.object());
            store.publishReports(
                    store.captureCommittedSet(),
                    Map.of(
                            "summary.json",
                            "{}".getBytes(),
                            "leaderboard.csv",
                            "name\n".getBytes(),
                            "game-scores.csv",
                            "game\n".getBytes()),
                    CanonicalJson.object()
                            .put("outcome", "COMPLETED")
                            .put("sessionId", session.toString()));
        }
        var root = c.output().absolute();
        var before = new TreeMap<String, String>();
        try (var files = Files.walk(root)) {
            for (Path p : files.filter(Files::isRegularFile).toList())
                before.put(
                        root.relativize(p).toString(), CanonicalJson.sha256(Files.readAllBytes(p)));
        }
        var verified = DurableTournamentRunStore.verify(c, BUILD, RESOLVER, () -> false);
        assertThat(verified.successfullyCompleted()).isTrue();
        var after = new TreeMap<String, String>();
        try (var files = Files.walk(root)) {
            for (Path p : files.filter(Files::isRegularFile).toList())
                after.put(
                        root.relativize(p).toString(), CanonicalJson.sha256(Files.readAllBytes(p)));
        }
        assertThat(after).isEqualTo(before);
        Files.delete(
                root.resolve("reports")
                        .resolve(verified.reports().orElseThrow().generationId())
                        .resolve("summary.json"));
        var publicationOnly = DurableTournamentRunStore.verify(c, BUILD, RESOLVER, () -> false);
        assertThat(publicationOnly.successfullyCompleted()).isFalse();
        assertThat(publicationOnly.committedSet().games()).hasSize(12);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void reportPointerCrashExposesOneCompleteGeneration(boolean afterRename) throws Exception {
        var c = requested();
        var io =
                new TournamentPublicationIO() {
                    boolean fail;

                    public void forceFile(FileChannel file) throws IOException {
                        LOCAL.forceFile(file);
                    }

                    public void forceDirectory(Path directory) throws IOException {
                        LOCAL.forceDirectory(directory);
                    }

                    public void atomicMove(Path temp, Path target, boolean replace)
                            throws IOException {
                        if (fail && target.getFileName().toString().equals("current.json")) {
                            if (afterRename) LOCAL.atomicMove(temp, target, replace);
                            throw new IOException("uncertain report pointer");
                        }
                        LOCAL.atomicMove(temp, target, replace);
                    }
                };
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false, io)) {
            var fixed = store.captureCommittedSet();
            var old =
                    store.publishReports(
                            fixed,
                            Map.of(
                                    "summary.json",
                                    "{\"version\":1}".getBytes(),
                                    "leaderboard.csv",
                                    "old\n".getBytes(),
                                    "game-scores.csv",
                                    "old\n".getBytes()),
                            CanonicalJson.object());
            io.fail = true;
            assertThatThrownBy(
                            () ->
                                    store.publishReports(
                                            fixed,
                                            Map.of(
                                                    "summary.json",
                                                    "{\"version\":2}".getBytes(),
                                                    "leaderboard.csv",
                                                    "new\n".getBytes(),
                                                    "game-scores.csv",
                                                    "new\n".getBytes()),
                                            CanonicalJson.object()))
                    .hasMessageContaining("uncertain report pointer");
            var read = store.readCurrentReports();
            String expected = afterRename ? "new\n" : "old\n";
            assertThat(new String(read.exports().get("leaderboard.csv"))).isEqualTo(expected);
            assertThat(new String(read.exports().get("game-scores.csv"))).isEqualTo(expected);
            assertThat(
                            Files.exists(
                                    c.output()
                                            .absolute()
                                            .resolve("reports")
                                            .resolve(old.generationId())
                                            .resolve("manifest.json")))
                    .isTrue();
        }
    }

    @Test
    void actualSecondProcessCannotAcquireLiveLock() throws Exception {
        var c = requested();
        Path source = Files.createTempDirectory(RUNS, "lock-probe-").resolve("LockProbe.java");
        Files.writeString(
                source,
                """
                import java.nio.channels.*;import java.nio.file.*;
                class LockProbe {public static void main(String[] args)throws Exception {
                    try(var channel=FileChannel.open(Path.of(args[0]),StandardOpenOption.WRITE);var lock=channel.tryLock()){
                        if(lock!=null)System.exit(23);
                    }
                }}
                """);
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false)) {
            var process =
                    new ProcessBuilder(
                                    Path.of(System.getProperty("java.home"), "bin", "java")
                                            .toString(),
                                    source.toString(),
                                    c.output().absolute().resolve(".lock").toString())
                            .redirectErrorStream(true)
                            .start();
            String output = new String(process.getInputStream().readAllBytes());
            assertThat(process.waitFor()).withFailMessage(output).isZero();
        }
        var process =
                new ProcessBuilder(
                                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                                source.toString(),
                                c.output().absolute().resolve(".lock").toString())
                        .redirectErrorStream(true)
                        .start();
        String output = new String(process.getInputStream().readAllBytes());
        assertThat(process.waitFor()).withFailMessage(output).isEqualTo(23);
        Files.delete(source);
        Files.delete(source.getParent());
    }

    @Test
    void missingModelSourceRefusesBeforeCreatingOutput() throws Exception {
        var c = requested();
        var ml =
                new TournamentConfigurationReader()
                        .read(
                                c.originalJson()
                                        .replace(
                                                "\"implementation\":\"algorithmic\"",
                                                "\"implementation\":\"ml\",\"model\":{\"source\":\"development\",\"path\":\"missing-source\"}"),
                                c.configurationFile())
                        .value();
        assertThatThrownBy(
                        () -> DurableTournamentRunStore.create(ml, 0, BUILD, RESOLVER, () -> false))
                .hasMessageContaining("preflight failed");
        assertThat(Files.exists(c.output().absolute())).isFalse();
    }

    @Test
    void checksumValidForgedGameScoresRefuseWithoutRepair() throws Exception {
        var c = requested();
        String game;
        UUID run;
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false)) {
            run = store.runId();
            var session = UUID.randomUUID();
            store.startSession(session, CanonicalJson.object());
            var scheduled = store.schedule().getFirst();
            game = scheduled.gameId();
            store.commit(
                    new TournamentGameCore()
                            .play(
                                    scheduled,
                                    TournamentGameCoreTest.passing(),
                                    TournamentIdentities.resolvedDigest(store.configuration()),
                                    UUID.randomUUID(),
                                    session,
                                    List.of()));
        }
        Path file = c.output().absolute().resolve("games").resolve(game + ".json");
        var payload =
                (com.fasterxml.jackson.databind.node.ObjectNode)
                        TournamentRecords.decode(Files.readAllBytes(file), run, "game");
        ((com.fasterxml.jackson.databind.node.ObjectNode) payload.path("result").path("seatScores"))
                .put("FOREHAND", 99);
        byte[] forged = TournamentRecords.encode(run, "game", payload);
        Files.write(file, forged);
        assertThatThrownBy(
                        () ->
                                DurableTournamentRunStore.resume(
                                        c, BUILD, RESOLVER, false, () -> false))
                .hasMessageContaining("games/" + game + ".json")
                .hasMessageContaining("Illegal committed game");
        assertThat(Files.readAllBytes(file)).isEqualTo(forged);
    }

    @Test
    void failureHistoriesSurviveRetryAndOnlyCompleteEpochsEnterFixedSet() throws Exception {
        var c = requested();
        UUID first = UUID.randomUUID();
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false)) {
            store.startSession(first, CanonicalJson.object());
            store.recordFailure(
                    new TournamentFailedAttempt(
                            store.schedule().getFirst().gameId(),
                            UUID.randomUUID(),
                            first,
                            "BIDDING",
                            "callback failed",
                            List.of()));
            store.endSession(first, "FAILED", CanonicalJson.object());
        }
        try (var store = DurableTournamentRunStore.resume(c, BUILD, RESOLVER, false, () -> false)) {
            var second = UUID.randomUUID();
            store.startSession(second, CanonicalJson.object());
            var game = store.schedule().getFirst();
            store.commit(
                    new TournamentGameCore()
                            .play(
                                    game,
                                    TournamentGameCoreTest.passing(),
                                    TournamentIdentities.resolvedDigest(store.configuration()),
                                    UUID.randomUUID(),
                                    second,
                                    List.of()));
            assertThat(CanonicalJson.parse(store.historyJson()).path("failed-attempts")).hasSize(1);
            assertThat(store.captureCommittedSet().completeEpochs()).isEmpty();
            assertThat(store.pendingGames()).hasSize(11);
        }
    }

    @Test
    void gameBeforeRenameFailureRemainsPendingAndExplicitResumeUsesNewAttempt() throws Exception {
        var c = requested();
        var io =
                new TournamentPublicationIO() {
                    public void forceFile(FileChannel file) throws IOException {
                        LOCAL.forceFile(file);
                    }

                    public void forceDirectory(Path directory) throws IOException {
                        LOCAL.forceDirectory(directory);
                    }

                    public void atomicMove(Path temp, Path target, boolean replace)
                            throws IOException {
                        if (target.getParent().getFileName().toString().equals("games"))
                            throw new IOException("before game rename");
                        LOCAL.atomicMove(temp, target, replace);
                    }
                };
        TournamentGameResult first;
        try (var store = DurableTournamentRunStore.create(c, 0, BUILD, RESOLVER, () -> false, io)) {
            var session = UUID.randomUUID();
            store.startSession(session, CanonicalJson.object());
            first =
                    new TournamentGameCore()
                            .play(
                                    store.schedule().getFirst(),
                                    TournamentGameCoreTest.passing(),
                                    TournamentIdentities.resolvedDigest(store.configuration()),
                                    UUID.randomUUID(),
                                    session,
                                    List.of());
            assertThatThrownBy(() -> store.commit(first))
                    .hasMessageContaining("before game rename");
        }
        try (var resumed =
                DurableTournamentRunStore.resume(c, BUILD, RESOLVER, false, () -> false)) {
            assertThat(resumed.pendingGames()).hasSize(12);
            var session = UUID.randomUUID();
            resumed.startSession(session, CanonicalJson.object());
            var retry =
                    new TournamentGameCore()
                            .play(
                                    resumed.schedule().getFirst(),
                                    TournamentGameCoreTest.passing(),
                                    TournamentIdentities.resolvedDigest(resumed.configuration()),
                                    UUID.randomUUID(),
                                    session,
                                    List.of());
            resumed.commit(retry);
            assertThat(retry.gameId()).isEqualTo(first.gameId());
            assertThat(retry.attemptId()).isNotEqualTo(first.attemptId());
            assertThat(retry.outcome()).isEqualTo(first.outcome());
        }
    }
}
