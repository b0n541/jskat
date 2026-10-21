package org.jskat;

import org.junit.jupiter.api.*;
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
            var verified = new org.jskat.tournament.TournamentService().verify(directory.resolve("run"), org.jskat.tournament.CancellationToken.NONE);
            assertThat(verified.committedSet().games()).isNotEmpty();
            assertThat(verified.committedSet().games().size()).isLessThan(verified.schedule().size());
            assertThat(verified.successfullyCompleted()).isFalse();
            assertThat(verified.reports()).isPresent();
        } finally { child.destroyForcibly(); }
    }
}
