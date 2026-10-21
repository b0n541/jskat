package org.jskat;

import org.junit.jupiter.api.*;
import org.jskat.tournament.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;

class TournamentEntryTest {
    @TempDir Path temporary;
    record Result(int exit, String output) {}
    private Process start(Path cwd, Path log, String... args) throws Exception {
        var command = new ArrayList<>(List.of(Path.of(System.getProperty("java.home"), "bin/java").toString(),
                "-Djava.awt.headless=true", "-Duser.home=" + temporary, "-cp", System.getProperty("jskat.application.classpath"), "org.jskat.JSkatKt"));
        command.addAll(List.of(args));
        var builder = new ProcessBuilder(command).directory(cwd.toFile()).redirectErrorStream(true).redirectOutput(log.toFile());
        builder.environment().remove("DISPLAY"); builder.environment().remove("WAYLAND_DISPLAY");
        return builder.start();
    }
    private Result run(String... args) throws Exception {
        var log = temporary.resolve(UUID.randomUUID() + ".log");
        var process = start(temporary, log, args);
        try { assertThat(process.waitFor(90, TimeUnit.SECONDS)).isTrue(); return new Result(process.exitValue(), Files.readString(log)); }
        finally { process.destroyForcibly(); }
    }
    private Path root() throws Exception {
        var configured = System.getenv("JSKAT_TOURNAMENT_EXT4_TEST_ROOT");
        Assumptions.assumeTrue(configured != null, "Durable store requires explicitly selected ext4 test root");
        return Files.createTempDirectory(Path.of(configured), "cli-");
    }
    private Path config(Path root, int epochs) throws Exception {
        return Files.writeString(root.resolve("config.json"), """
            {"schemaVersion":1,"ruleset":"standard-skat-v1","epochs":%d,"seed":"42","output":"run","competitors":[
            {"id":"550e8400-e29b-41d4-a716-446655440000","name":"Random","implementation":"random"},
            {"id":"ca207ce9-3ca4-4ce1-8443-81e081b8344e","name":"Algorithmic","implementation":"algorithmic"}]}
            """.formatted(epochs));
    }
    @Test void malformedTournamentArgumentsNeverInitializeGraphics() throws Exception {
        var result = run("--tournament=missing.json");
        assertThat(result.exit).isEqualTo(2);
        assertThat(result.output).contains("CONFIGURATION_ERROR", "Usage:").doesNotContain("DISPLAY", "JavaFX", "Downloading");
    }
    @Test void runsAndExplicitlyResumesCompletedRunWithoutModelStoreOrDisplay() throws Exception {
        var directory = root(); var config = config(directory, 1);
        var first = run("--tournament", config.toString(), "--workers", "2");
        assertThat(first.exit).withFailMessage(first.output).isZero();
        assertThat(first.output).contains("Status: COMPLETED", "committed=6/6", "Output: " + directory.resolve("run"))
                .doesNotContain("JavaFX", "Downloading", "Player moves card");
        var before = new TreeMap<String, byte[]>();
        try (var paths = Files.walk(directory.resolve("run"))) { paths.filter(Files::isRegularFile).forEach(p -> { try { before.put(p.toString(), Files.readAllBytes(p)); } catch (Exception e) { throw new RuntimeException(e); } }); }
        var resume = run("--tournament", config.toString(), "--resume", "--verbose");
        assertThat(resume.exit).withFailMessage(resume.output).isZero();
        assertThat(resume.output).contains("Status: COMPLETED", "Summary: committed=6/6");
        for (var entry : before.entrySet()) assertThat(Files.readAllBytes(Path.of(entry.getKey()))).isEqualTo(entry.getValue());
        assertThat(Files.exists(temporary.resolve(".jskat/models"))).isFalse();
        assertThat(run("--tournament", config.toString()).exit).isEqualTo(2);
        Files.writeString(config, Files.readString(config).replace("\"seed\":\"42\"", "\"seed\":\"43\""));
        var incompatible = run("--tournament", config.toString(), "--resume");
        assertThat(incompatible.exit).withFailMessage(incompatible.output).isEqualTo(1);
        assertThat(incompatible.output).contains("Status: FAILED");
    }
    @Test void modelBackedRunUsesRelativeDevelopmentSourceAndCompletedResumeUsesSnapshot() throws Exception {
        var source = Path.of(System.getProperty("user.home"), ".jskat/models/releases/v1.4.0");
        Assumptions.assumeTrue(Files.exists(source.resolve("models.json")), "Installed ONNX integration fixture required");
        var directory = root(); var development = Files.createDirectory(directory.resolve("development"));
        try (var files = Files.list(source)) { for (var file : files.toList()) if (Files.isRegularFile(file)) Files.copy(file, development.resolve(file.getFileName())); }
        var config = config(directory, 1);
        Files.writeString(config, Files.readString(config).replace("\"implementation\":\"algorithmic\"", "\"implementation\":\"ml-pro\",\"model\":{\"source\":\"development\",\"path\":\"development\"}"));
        var result = run("--tournament", config.toString());
        assertThat(result.exit).withFailMessage(result.output).isZero();
        assertThat(result.output).contains("Status: COMPLETED", "committed=6/6").doesNotContain("JavaFX", "Downloading");
        try (var files = Files.list(development)) { for (var file : files.toList()) Files.delete(file); }
        Files.delete(development);
        var resume = run("--tournament", config.toString(), "--resume");
        assertThat(resume.exit).withFailMessage(resume.output).isZero();
        assertThat(resume.output).contains("Status: COMPLETED");
        assertThat(Files.exists(temporary.resolve(".jskat/models"))).isFalse();
    }

    /** Separate app JVMs exercise registry construction, pool reuse, durable reports and signal hooks. */
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(booleans = {false, true})
    void freshJvmWorkersAndVerboseResumeReproduceEveryRetainedSemanticResult(boolean modelBacked) throws Exception {
        var sequentialRoot = root(); var parallelRoot = root(); var resumedRoot = root();
        var sequential = config(sequentialRoot, 3); var parallel = config(parallelRoot, 3); var resumed = config(resumedRoot, 3);
        if (modelBacked) {
            var source = Path.of(System.getProperty("user.home"), ".jskat/models/releases/v1.4.0");
            Assumptions.assumeTrue(Files.exists(source.resolve("models.json")), "Installed ONNX reproducibility fixture required");
            for (var file : List.of(sequential, parallel, resumed)) Files.writeString(file, Files.readString(file).replace("\"implementation\":\"algorithmic\"", "\"implementation\":\"ml-pro\",\"model\":{\"source\":\"development\",\"path\":\"" + source + "\"}"));
        }
        var first = run("--tournament", sequential.toString());
        assertThat(first.exit).withFailMessage(first.output).isZero();
        var second = run("--tournament", parallel.toString(), "--workers", "3", "--verbose");
        assertThat(second.exit).withFailMessage(second.output).isZero();
        var log = temporary.resolve("reproducibility-interrupted.log");
        var child = start(temporary, log, "--tournament", resumed.toString(), "--workers", "1");
        try {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
            while (System.nanoTime() < deadline && child.isAlive() && !hasCommittedGame(resumedRoot.resolve("run"))) Thread.sleep(2);
            assertThat(Files.readString(log)).contains("RUNNING");
            assertThat(new ProcessBuilder("kill", "-INT", Long.toString(child.pid())).start().waitFor()).isZero();
            assertThat(child.waitFor(60, TimeUnit.SECONDS)).isTrue();
            assertThat(child.exitValue()).withFailMessage(Files.readString(log)).isEqualTo(130);
            assertThat(Files.readString(log)).contains("Status: CANCELLED");
        } finally { child.destroyForcibly(); }
        var partial = new TournamentService().verify(resumedRoot.resolve("run"), CancellationToken.NONE);
        assertThat(partial.committedSet().games()).isNotEmpty().hasSizeLessThan(18);
        var finish = run("--tournament", resumed.toString(), "--resume", "--workers", "4", "--verbose");
        assertThat(finish.exit).withFailMessage(finish.output).isZero();
        var expected = semantics(sequentialRoot.resolve("run"));
        assertThat(semantics(parallelRoot.resolve("run"))).isEqualTo(expected);
        assertThat(semantics(resumedRoot.resolve("run"))).isEqualTo(expected);
    }

    private boolean hasCommittedGame(Path run) throws Exception {
        var games = run.resolve("games");
        if (!Files.isDirectory(games)) return false;
        try (var files = Files.list(games)) { return files.anyMatch(p -> p.getFileName().toString().endsWith(".json")); }
    }

    private JsonNode semantics(Path output) throws Exception {
        var verified = new TournamentService().verify(output, CancellationToken.NONE);
        assertThat(verified.successfullyCompleted()).isTrue();
        assertThat(verified.diagnostics()).isEmpty();
        var canonical = CanonicalJson.object();
        canonical.set("schedule", new ObjectMapper().valueToTree(verified.schedule()));
        var games = canonical.putArray("games");
        for (var result : verified.committedSet().games().stream().sorted(Comparator.comparing(TournamentGameResult::gameId)).toList()) {
            assertThat(result.fallbacks()).isEmpty();
            var game = (ObjectNode) new ObjectMapper().valueToTree(result);
            // These bind provenance, not the game semantics being compared across different run directories.
            game.remove(List.of("attemptId", "sessionId", "inputDigest"));
            games.add(game);
        }
        var summary = (ObjectNode) CanonicalJson.parse(new String(verified.reports().orElseThrow().exports().get("summary.json"), java.nio.charset.StandardCharsets.UTF_8));
        assertThat(summary.path("reproducible").asBoolean()).isTrue();
        assertThat(summary.path("ratingParameters").path("bootstrapReplicates").asInt()).isEqualTo(5000);
        assertThat(summary.path("includedEpochs").size()).isEqualTo(3);
        assertThat(summary.path("predictive").isNull()).isFalse();
        summary.remove(List.of("runId", "committedSetDigest", "gameChecksums", "requestedDigest", "resolvedDigest", "configuration", "requestedConfiguration", "originalConfiguration", "configurationFile", "lifecycle", "history"));
        canonical.set("summary", summary);
        return canonical;
    }

    @Test void actualCliRefusesWrongBuildAndLiveLockAndRequiresAuditedStaleRecovery() throws Exception {
        var service = new TournamentService();
        var wrongRoot = root(); var wrongFile = config(wrongRoot, 1); var wrong = service.read(wrongFile).value();
        try (var store = DurableTournamentRunStore.create(wrong, 42,
                new TournamentBuildIdentity("a".repeat(40), Optional.empty()),
                new DefaultTournamentInputResolver(), CancellationToken.NONE)) { }
        var incompatible = run("--tournament", wrongFile.toString(), "--resume");
        assertThat(incompatible.exit).withFailMessage(incompatible.output).isEqualTo(1);
        assertThat(incompatible.output).contains("Status: FAILED", "build");

        var directory = root(); var file = config(directory, 1); var requested = service.read(file).value();
        UUID runId;
        try (var store = DurableTournamentRunStore.create(requested, 42,
                TournamentBuildIdentity.current(), new DefaultTournamentInputResolver(), CancellationToken.NONE)) {
            runId = store.runId();
            var busy = run("--tournament", file.toString(), "--resume", "--recover-stale-lock");
            assertThat(busy.exit).withFailMessage(busy.output).isEqualTo(1);
            assertThat(busy.output).contains("live OS lock", "Status: FAILED");
        }
        var stale = TournamentRecords.encode(runId, "lock-owner",
                CanonicalJson.parse("{\"released\":false,\"pid\":999999999}"));
        var owner = directory.resolve("run/lock-owner.json");
        Files.write(owner, stale);
        var refused = run("--tournament", file.toString(), "--resume");
        assertThat(refused.exit).withFailMessage(refused.output).isEqualTo(1);
        assertThat(refused.output).contains("explicit recovery", "Status: FAILED");
        assertThat(Files.readAllBytes(owner)).isEqualTo(stale);
        var recovered = run("--tournament", file.toString(), "--resume", "--recover-stale-lock");
        assertThat(recovered.exit).withFailMessage(recovered.output).isZero();
        try (var files = Files.list(directory.resolve("run/lock-history"))) {
            var audit = TournamentRecords.decode(Files.readAllBytes(files.findFirst().orElseThrow()), runId, "lock-recovery");
            assertThat(Base64.getDecoder().decode(audit.path("previousBase64").asText())).isEqualTo(stale);
        }
        var ready = directory.resolve("run/ready.json");
        var unsupported = Files.readString(ready).replace("\"schemaVersion\":1", "\"schemaVersion\":2");
        Files.writeString(ready, unsupported);
        var invalid = run("--tournament", file.toString(), "--resume");
        assertThat(invalid.exit).withFailMessage(invalid.output).isEqualTo(1);
        assertThat(invalid.output).contains("Unsupported schemaVersion", "Status: FAILED");
        assertThat(Files.readString(ready)).isEqualTo(unsupported);
    }

    @Test void actualCliRecoversMissingDerivedExportWithoutReplacingCommittedGames() throws Exception {
        var directory = root(); var file = config(directory, 1);
        var first = run("--tournament", file.toString());
        assertThat(first.exit).withFailMessage(first.output).isZero();
        var service = new TournamentService();
        var before = service.verify(directory.resolve("run"), CancellationToken.NONE);
        Files.delete(directory.resolve("run/reports").resolve(before.reports().orElseThrow().generationId()).resolve("leaderboard.csv"));
        var broken = service.verify(directory.resolve("run"), CancellationToken.NONE);
        assertThat(broken.successfullyCompleted()).isFalse();
        assertThat(broken.committedSet().games()).hasSize(6);
        var recovered = run("--tournament", file.toString(), "--resume", "--workers", "3");
        assertThat(recovered.exit).withFailMessage(recovered.output).isZero();
        assertThat(recovered.output).contains("Status: COMPLETED", "admitted=0", "committed=6/6");
        var after = service.verify(directory.resolve("run"), CancellationToken.NONE);
        assertThat(after.successfullyCompleted()).isTrue();
        assertThat(after.committedSet()).isEqualTo(before.committedSet());
        assertThat(after.reports().orElseThrow().generationId()).isNotEqualTo(before.reports().orElseThrow().generationId());
    }

    @Test void actualCliPreservesOrderedStructuralAndSourcePreflightIssues() throws Exception {
        var directory = root(); var file = config(directory, 1);
        Files.writeString(file, "{\"schemaVersion\":2,\"epochs\":1.5,\"unknown\":true}");
        assertCliIssues(file, new TournamentService().read(file).issues());
        config(directory, 1);
        Files.writeString(file, Files.readString(file).replace("\"implementation\":\"algorithmic\"", "\"implementation\":\"ml\",\"model\":{\"source\":\"development\",\"path\":\"missing\"}"));
        Files.createDirectory(directory.resolve("run"));
        var service = new TournamentService();
        assertCliIssues(file, service.preflightNew(service.read(file).value(), CancellationToken.NONE).issues());
        try (var files = Files.list(directory.resolve("run"))) { assertThat(files.findAny()).isEmpty(); }
    }

    private void assertCliIssues(Path file, List<ValidationIssue> issues) throws Exception {
        var result = run("--tournament", file.toString());
        assertThat(result.exit).withFailMessage(result.output).isEqualTo(2);
        var actual = result.output.lines().filter(line -> line.startsWith("ERROR ") || line.startsWith("WARNING ")).toList();
        assertThat(actual).containsExactlyElementsOf(issues.stream().map(i -> i.severity() + " " + i.code() + " " + i.path() + ": " + i.message()).toList());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"TERM", "INT"})
    void signalsDrainAdmittedGamesAndPublishTerminalStatus(String signal) throws Exception {
        Assumptions.assumeTrue(System.getProperty("os.name").equals("Linux"));
        var directory = root(); var config = config(directory, 1000);
        var log = temporary.resolve("signal.log");
        var child = start(temporary, log, "--tournament", config.toString(), "--workers", "2");
        try {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
            while (System.nanoTime() < deadline && child.isAlive() && !Files.readString(log).matches("(?s).*admitted=[1-9][0-9]*.*")) Thread.sleep(25);
            assertThat(Files.readString(log)).contains("RUNNING");
            var kill = new ProcessBuilder("kill", "-" + signal, Long.toString(child.pid())).start();
            assertThat(kill.waitFor()).isZero();
            assertThat(child.waitFor(60, TimeUnit.SECONDS)).isTrue();
            var output = Files.readString(log);
            assertThat(child.exitValue()).withFailMessage(output).isEqualTo(signal.equals("TERM") ? 143 : 130);
            assertThat(output).contains("Cancellation requested", "Status: CANCELLED", "Report generation:");
            var verified = new TournamentService().verify(directory.resolve("run"), CancellationToken.NONE);
            assertThat(verified.committedSet().games()).isNotEmpty();
            assertThat(verified.committedSet().games().size()).isLessThan(verified.schedule().size());
            assertThat(verified.successfullyCompleted()).isFalse();
            assertThat(verified.reports()).isPresent();
        } finally { child.destroyForcibly(); }
    }
}
