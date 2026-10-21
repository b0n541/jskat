package org.jskat.tournament;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/** Blocking, display-independent command adapter. All execution policy belongs to the service. */
public final class TournamentCommand {
    public static final String USAGE = """
            Usage: JSkat --tournament <config.json> [--resume] [--recover-stale-lock]
                         [--workers <positive-integer>] [--verbose]
                         [--stall-warning-seconds <positive-integer>]
                   JSkat --tournament-help
            """;
    private static final Set<String> OPTIONS = Set.of("--tournament", "--resume", "--recover-stale-lock",
            "--workers", "--verbose", "--stall-warning-seconds", "--tournament-help");

    public static boolean handles(String[] args) {
        return Arrays.stream(args).anyMatch(a -> OPTIONS.contains(a.split("=", 2)[0]));
    }

    public int execute(String[] args, PrintStream out, PrintStream err) {
        final Arguments parsed;
        try { parsed = parse(args); }
        catch (IllegalArgumentException e) {
            err.println("CONFIGURATION_ERROR: " + line(e.getMessage()));
            err.print(USAGE);
            return 2;
        }
        if (parsed.help) { out.print(USAGE); return 0; }
        var cancelled = new AtomicBoolean();
        var finished = new CountDownLatch(1);
        var hook = new Thread(() -> {
            cancelled.set(true);
            err.println("Cancellation requested; waiting for admitted games and cleanup");
            err.flush();
            boolean interrupted = false;
            while (true) try { finished.await(); break; }
            catch (InterruptedException e) { interrupted = true; }
            if (interrupted) Thread.currentThread().interrupt();
        }, "tournament-shutdown");
        Runtime.getRuntime().addShutdownHook(hook);
        try {
            var service = new TournamentService();
            var configuration = service.read(parsed.file);
            issues(configuration.issues(), err);
            if (!configuration.valid()) { out.println("Status: CONFIGURATION_ERROR"); return 2; }
            var display = new ProgressOutput(out, parsed.options.verbose());
            var result = service.run(configuration.value(), parsed.invocation, parsed.options, cancelled::get, display);
            issues(result.configurationIssues(), err);
            result.diagnostics().forEach(d -> err.println(line(d)));
            out.println("Status: " + result.disposition());
            result.reports().ifPresent(r -> {
                byte[] summary = r.exports().get("summary.json");
                if (summary != null) printSummary(new String(summary, StandardCharsets.UTF_8), out);
                out.println("Report generation: " + line(r.generationId()));
            });
            out.println("Output: " + result.output());
            out.flush(); err.flush();
            return switch (result.disposition()) {
                case COMPLETED -> 0;
                case CONFIGURATION_ERROR -> 2;
                case CANCELLED -> 130;
                case FAILED -> 1;
            };
        } catch (Exception e) {
            err.println("FAILED: " + line(e.toString()));
            out.println("Status: FAILED");
            return 1;
        } finally {
            finished.countDown();
            try { Runtime.getRuntime().removeShutdownHook(hook); }
            catch (IllegalStateException shutdownStarted) { /* The hook owns cooperative draining. */ }
        }
    }

    private record Arguments(Path file, TournamentInvocation invocation, TournamentSessionOptions options, boolean help) {}

    private static Arguments parse(String[] args) {
        if (Arrays.equals(args, new String[]{"--tournament-help"})) return new Arguments(null, null, null, true);
        var seen = new HashSet<String>();
        Path file = null;
        int workers = 1;
        long seconds = 60;
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if (!OPTIONS.contains(option) || option.equals("--tournament-help"))
                throw new IllegalArgumentException("Unexpected argument: " + option);
            if (!seen.add(option)) throw new IllegalArgumentException("Duplicate option: " + option);
            if (Set.of("--tournament", "--workers", "--stall-warning-seconds").contains(option)) {
                if (++i == args.length || args[i].startsWith("--")) throw new IllegalArgumentException("Missing value: " + option);
                String value = args[i];
                switch (option) {
                    case "--tournament" -> file = Path.of(value).toAbsolutePath().normalize();
                    case "--workers" -> workers = workerCount(value);
                    case "--stall-warning-seconds" -> seconds = positive(value, option);
                }
            }
        }
        if (file == null) throw new IllegalArgumentException("--tournament is required");
        boolean resume = seen.contains("--resume"), recover = seen.contains("--recover-stale-lock");
        if (recover && !resume) throw new IllegalArgumentException("--recover-stale-lock requires --resume");
        return new Arguments(file, new TournamentInvocation(resume ? TournamentInvocation.Action.RESUME : TournamentInvocation.Action.NEW, recover),
                new TournamentSessionOptions(workers, seen.contains("--verbose"), Duration.ofSeconds(seconds)), false);
    }

    private static int workerCount(String value) {
        long count = positive(value, "--workers");
        if (count > Integer.MAX_VALUE / 3) throw new IllegalArgumentException("--workers exceeds representable seat capacity");
        return (int) count;
    }

    private static long positive(String text, String option) {
        if (!text.matches("[0-9]+")) throw new IllegalArgumentException(option + " requires positive decimal integer");
        try {
            long value = Long.parseLong(text);
            if (value < 1) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException e) { throw new IllegalArgumentException(option + " requires a representable positive integer"); }
    }

    private static void issues(List<ValidationIssue> issues, PrintStream err) {
        issues.forEach(i -> err.println(i.severity() + " " + i.code() + " " + line(i.path()) + ": " + line(i.message())));
        err.flush();
    }
    private static String line(String text) { return text.replace('\r', ' ').replace('\n', ' '); }

    private static void printSummary(String json, PrintStream out) {
        var summary = CanonicalJson.parse(json);
        out.println("Summary: committed=" + summary.path("committedGames").asLong() + "/" + summary.path("scheduledGames").asLong()
                + " epochs=" + summary.path("includedEpochs").size() + " provisional=" + summary.path("provisional").asBoolean());
        if (!summary.path("reproducible").asBoolean(true)) out.println("WARNING: Inference fallback; run is not reproducible");
        for (var row : summary.path("leaderboard")) out.println("Rating: " + line(row.path("name").asText())
                + " " + (row.path("rating").isNumber() ? row.path("rating").asText() : "unavailable (no complete epoch)")
                + " interval=" + (row.path("interval").isNull() ? line(row.path("intervalUnavailableReason").asText()) : row.path("interval")));
    }

    /** Coalesces routine observations using their monotonic elapsed time. */
    public static final class ProgressOutput implements TournamentProgress.Observer {
        private final PrintStream out;
        private final boolean verbose;
        private TournamentProgress previous;
        private Duration last = Duration.ofSeconds(-1);
        private Optional<UUID> shownGeneration = Optional.empty();
        public ProgressOutput(PrintStream out, boolean verbose) { this.out = out; this.verbose = verbose; }
        public synchronized void onProgress(TournamentProgress p) {
            boolean immediate = previous == null || previous.state() != p.state() || p.failedAttempts() > previous.failedAttempts()
                    || p.kind().urgent();
            previous = p;
            if (!immediate && p.metrics().elapsed().minus(last).compareTo(Duration.ofSeconds(1)) < 0) return;
            last = p.metrics().elapsed();
            out.printf(Locale.ROOT, "%s committed=%d/%d admitted=%d epochs=%d elapsed=%.1fs rate=%.2f/s ETA=%s %s%n",
                    p.state(), p.committedGames(), p.totalGames(), p.admittedGames(), p.completeEpochs(),
                    p.metrics().elapsed().toNanos() / 1e9, p.metrics().gamesPerSecond(),
                    p.metrics().estimatedRemaining().map(Duration::toString).orElse("unavailable"), line(p.message()));
            if (!p.reproducible()) out.println("WARNING: Inference fallback; run is not reproducible");
            if (!shownGeneration.equals(p.metrics().reportGeneration())) {
                shownGeneration = p.metrics().reportGeneration();
                p.metrics().summaryJson().ifPresent(s -> printSummary(s, out));
            }
            for (var attempt : p.metrics().runningAttempts()) if (verbose || attempt.stallWarned())
                out.println("Attempt: " + line(attempt.gameId()) + " " + attempt.attemptId() + " elapsed=" + attempt.elapsed() + " competitors=" + attempt.competitors());
            out.flush();
        }
    }
}
