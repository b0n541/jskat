package org.jskat.tournament;

import org.jskat.player.JSkatPlayer;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;

/** Shared, presentation independent tournament entry point. */
public final class TournamentService {
    public enum Disposition {
        CONFIGURATION_ERROR,
        COMPLETED,
        CANCELLED,
        FAILED
    }

    public enum Recovery { NOT_REQUIRED, EXPLICIT_AUTHORIZATION_REQUIRED }

    public record RunResult(
            Disposition disposition,
            Path output,
            Optional<UUID> runId,
            Optional<UUID> sessionId,
            Optional<DurableTournamentRunStore.ReportGeneration> reports,
            List<ValidationIssue> configurationIssues,
            List<String> diagnostics,
            Recovery recovery) {
        public RunResult(Disposition disposition, Path output, Optional<UUID> runId,
                Optional<UUID> sessionId, Optional<DurableTournamentRunStore.ReportGeneration> reports,
                List<ValidationIssue> configurationIssues, List<String> diagnostics) {
            this(disposition, output, runId, sessionId, reports, configurationIssues, diagnostics,
                    Recovery.NOT_REQUIRED);
        }

        public RunResult {
            Objects.requireNonNull(recovery);
            configurationIssues = List.copyOf(configurationIssues);
            diagnostics = List.copyOf(diagnostics);
        }
    }

    public record OpenedRun(
            DurableTournamentRunStore.Inspection inspection,
            Optional<DurableTournamentRunStore.ReportGeneration> reports,
            List<String> diagnostics) {
        public OpenedRun {
            diagnostics = List.copyOf(diagnostics);
        }
    }

    /** Current validated source identities; Start repeats preflight under invocation ownership. */
    public record Preflight(
            TournamentConfiguration configuration,
            Optional<TournamentBuildIdentity> build,
            List<TournamentInputResolver.InputIdentity> competitors,
            List<ValidationIssue> issues) {
        public Preflight {
            competitors = List.copyOf(competitors);
            issues = issues.stream().sorted(ValidationIssue.ORDER).toList();
        }

        public boolean valid() {
            return issues.stream().noneMatch(i -> i.severity() == ValidationIssue.Severity.ERROR);
        }
    }

    /** Boundary for native player ownership; every successful acquire owns all three seats. */
    public interface Players extends AutoCloseable {
        Lease acquire(ScheduledTournamentGame game, CancellationToken cancellation);

        void close();
    }

    public interface Lease extends AutoCloseable {
        List<JSkatPlayer> players();

        List<TournamentInferenceFallback> fallbacks();

        void discard();

        void close();
    }

    @FunctionalInterface
    public interface PlayerFactory {
        Players create(ResolvedTournamentConfiguration configuration, int workers);
    }

    @FunctionalInterface
    public interface ReportCalculator {
        TournamentReports.Report calculate(
                ResolvedTournamentConfiguration configuration,
                List<ScheduledTournamentGame> schedule,
                DurableTournamentRunStore.CommittedSet set,
                String history,
                com.fasterxml.jackson.databind.JsonNode metadata);
    }

    private static final AtomicBoolean ACTIVE = new AtomicBoolean();

    public static boolean isActive() {
        return ACTIVE.get();
    }

    private final TournamentInputResolver resolver;
    private final Supplier<TournamentBuildIdentity> build;
    private final PlayerFactory playerFactory;
    private final ReportCalculator reportCalculator;
    private final LongSupplier nanoTime;
    private final TournamentPublicationIO publicationIO;

    public TournamentService() {
        this(new DefaultTournamentInputResolver());
    }

    public TournamentService(TournamentInputResolver resolver) {
        this(
                resolver,
                TournamentBuildIdentity::current,
                TournamentService::defaultPlayers,
                TournamentReports::calculate,
                System::nanoTime,
                TournamentPublicationIO.LOCAL);
    }

    public TournamentService(
            TournamentInputResolver resolver,
            Supplier<TournamentBuildIdentity> build,
            PlayerFactory playerFactory,
            ReportCalculator reportCalculator,
            LongSupplier nanoTime,
            TournamentPublicationIO publicationIO) {
        this.resolver = Objects.requireNonNull(resolver);
        this.build = Objects.requireNonNull(build);
        this.playerFactory = Objects.requireNonNull(playerFactory);
        this.reportCalculator = Objects.requireNonNull(reportCalculator);
        this.nanoTime = Objects.requireNonNull(nanoTime);
        this.publicationIO = Objects.requireNonNull(publicationIO);
    }

    private static Players defaultPlayers(
            ResolvedTournamentConfiguration configuration, int workers) {
        var pool = new TournamentPlayerPool(configuration.competitors(), workers);
        return new Players() {
            public Lease acquire(ScheduledTournamentGame game, CancellationToken cancellation) {
                var lease = pool.acquire(game, cancellation);
                return new Lease() {
                    public List<JSkatPlayer> players() {
                        return lease.players();
                    }

                    public List<TournamentInferenceFallback> fallbacks() {
                        return lease.fallbacks();
                    }

                    public void discard() {
                        lease.discard();
                    }

                    public void close() {
                        lease.close();
                    }
                };
            }

            public void close() {
                pool.close();
            }
        };
    }

    public OpenedRun open(Path output) {
        var inspection = DurableTournamentRunStore.inspect(output);
        var diagnostics = new ArrayList<>(inspection.diagnostics());
        Optional<DurableTournamentRunStore.ReportGeneration> reports = Optional.empty();
        if (inspection.runId().isPresent())
            try {
                reports =
                        Optional.of(
                                DurableTournamentRunStore.inspectReports(
                                        output, inspection.runId().orElseThrow()));
            } catch (Exception e) {
                diagnostics.add(e.toString());
            }
        return new OpenedRun(inspection, reports, diagnostics);
    }

    public DurableTournamentRunStore.Verification verify(
            Path output, CancellationToken cancellation) throws IOException {
        return DurableTournamentRunStore.verify(output, build.get(), resolver, cancellation);
    }

    public DurableTournamentRunStore.Verification verify(
            TournamentConfiguration configuration, CancellationToken cancellation)
            throws IOException {
        return DurableTournamentRunStore.verify(configuration, build.get(), resolver, cancellation);
    }

    public RunResult run(
            TournamentConfiguration configuration,
            TournamentInvocation invocation,
            TournamentSessionOptions options,
            CancellationToken cancellation,
            TournamentProgress.Observer observer) {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(invocation);
        Objects.requireNonNull(options);
        Objects.requireNonNull(cancellation);
        Objects.requireNonNull(observer);
        if (!ACTIVE.compareAndSet(false, true))
            return new RunResult(
                    Disposition.FAILED,
                    configuration.output().absolute(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    List.of(),
                    List.of("A tournament invocation is already active"));
        var session = new Session(configuration, invocation, options, cancellation, observer);
        try (var logging = new TournamentLogging(options.verbose())) {
            session.execute();
        } finally {
            ACTIVE.set(false);
        }
        session.emitTerminal();
        return session.result();
    }

    public ValidationResult<TournamentConfiguration> read(Path file) {
        return new TournamentConfigurationReader().read(file);
    }

    public ValidationResult<TournamentConfiguration> read(String json, Path file) {
        return new TournamentConfigurationReader().read(json, file);
    }

    public ValidationResult<TournamentConfiguration> validateNew(
            String json, Path file, CancellationToken cancellation) {
        var parsed = read(json, file);
        return parsed.valid() ? validateNew(parsed.value(), cancellation) : parsed;
    }

    public ValidationResult<TournamentConfiguration> validateNew(
            TournamentConfiguration configuration, CancellationToken cancellation) {
        var result = preflightNew(configuration, cancellation);
        return new ValidationResult<>(configuration, result.issues());
    }

    public Preflight preflightNew(
            TournamentConfiguration configuration, CancellationToken cancellation) {
        var issues = new ArrayList<ValidationIssue>();
        Optional<TournamentBuildIdentity> identity = Optional.empty();
        try {
            identity = Optional.of(build.get());
        } catch (Exception e) {
            issues.add(
                    new ValidationIssue(
                            ValidationIssue.Severity.ERROR,
                            "BUILD_UNAVAILABLE",
                            "/build",
                            e.toString()));
        }
        Path output = configuration.output().absolute();
        try {
            try {
                Files.readAttributes(output, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                issues.add(
                        new ValidationIssue(
                                ValidationIssue.Severity.ERROR,
                                "OUTPUT_EXISTS",
                                "/output",
                                "Output already exists"));
            } catch (NoSuchFileException absent) {
                DurableTournamentRunStore.validateOutputFilesystem(output);
                Path ancestor = output.getParent();
                while (ancestor != null) {
                    try {
                        var attributes =
                                Files.readAttributes(
                                        ancestor,
                                        BasicFileAttributes.class,
                                        LinkOption.NOFOLLOW_LINKS);
                        if (!attributes.isDirectory()
                                || !Files.isWritable(ancestor)
                                || !Files.isExecutable(ancestor))
                            throw new IOException(
                                    "Output ancestor is not a writable directory: " + ancestor);
                        break;
                    } catch (NoSuchFileException missing) {
                        ancestor = ancestor.getParent();
                    }
                }
            }
        } catch (IOException | SecurityException e) {
            issues.add(
                    new ValidationIssue(
                            ValidationIssue.Severity.ERROR, "OUTPUT_IO", "/output", e.toString()));
        }
        var inputs = resolver.preflight(configuration, cancellation);
        issues.addAll(inputs.issues());
        return new Preflight(
                configuration,
                identity,
                inputs.value() == null ? List.of() : inputs.value(),
                issues);
    }

    private final class Session {
        final TournamentConfiguration requested;
        final TournamentInvocation invocation;
        final TournamentSessionOptions options;
        final CancellationToken external;
        final TournamentProgress.Observer observer;
        final AtomicBoolean stopped = new AtomicBoolean(), cancelled = new AtomicBoolean();
        final Object admission = new Object();
        final Map<UUID, ActiveAttempt> running = new LinkedHashMap<>();
        final List<String> diagnostics = new ArrayList<>();
        final List<ValidationIssue> issues = new ArrayList<>();
        final long started = nanoTime.getAsLong();
        long sequence,
                lastTick = Long.MIN_VALUE,
                admitted,
                committed,
                failures,
                epochs,
                newCommits,
                nextTurn;
        boolean reproducible = true, failed, interrupted, sessionStarted;
        Recovery recovery = Recovery.NOT_REQUIRED;
        Disposition disposition = Disposition.FAILED;
        TournamentProgress.State state = TournamentProgress.State.PREPARING;
        DurableTournamentRunStore store;
        Players players;
        ExecutorService workers, reportsExecutor;
        UUID runId, sessionId;
        DurableTournamentRunStore.ReportGeneration reports;

        Session(
                TournamentConfiguration requested,
                TournamentInvocation invocation,
                TournamentSessionOptions options,
                CancellationToken external,
                TournamentProgress.Observer observer) {
            this.requested = requested;
            this.invocation = invocation;
            this.options = options;
            this.external = external;
            this.observer = observer;
        }

        boolean cancellation() {
            return cancelled.get() || external.isCancellationRequested();
        }

        void requestStop() {
            synchronized (admission) {
                stopped.set(true);
                admission.notifyAll();
            }
        }

        void fault(Throwable error) {
            failed = true;
            requestStop();
            diagnostics.add(error.toString());
            if (error instanceof TournamentRecoveryRequiredException)
                recovery = Recovery.EXPLICIT_AUTHORIZATION_REQUIRED;
            emit("Failure: " + error, TournamentProgress.Kind.FAILURE);
            if (store != null)
                try {
                    store.diagnostic(TournamentRecords.tree(Map.of("error", error.toString())));
                } catch (Throwable secondary) {
                    diagnostics.add(secondary.toString());
                }
        }

        void execute() {
            emit("Preparing tournament", true);
            try {
                if (cancellation())
                    throw new TournamentCancellationException("Preparation cancelled");
                if (invocation.action() == TournamentInvocation.Action.NEW) {
                    var validation = validateNew(requested, this::cancellation);
                    issues.addAll(validation.issues());
                    if (!validation.valid()) {
                        disposition = Disposition.CONFIGURATION_ERROR;
                        return;
                    }
                    var identity = build.get();
                    store =
                            DurableTournamentRunStore.create(
                                    requested,
                                    requested
                                            .seed()
                                            .orElseGet(
                                                    () ->
                                                            new java.security.SecureRandom()
                                                                    .nextLong()),
                                    identity,
                                    resolver,
                                    this::cancellation,
                                    publicationIO);
                } else {
                    var identity = build.get();
                    var verification =
                            DurableTournamentRunStore.verify(
                                    requested, identity, resolver, this::cancellation);
                    runId = verification.runId();
                    committed = verification.committedSet().games().size();
                    epochs = verification.committedSet().completeEpochs().size();
                    reports = verification.reports().orElse(null);
                    reproducible =
                            verification.committedSet().games().stream()
                                            .allMatch(r -> r.fallbacks().isEmpty())
                                    && verification
                                            .reports()
                                            .map(
                                                    r ->
                                                            CanonicalJson.parse(
                                                                            new String(
                                                                                    r.exports()
                                                                                            .get(
                                                                                                    "summary.json"),
                                                                                    java.nio.charset
                                                                                            .StandardCharsets
                                                                                            .UTF_8))
                                                                    .path("reproducible")
                                                                    .asBoolean(true))
                                            .orElse(true);
                    if (invocation.action() == TournamentInvocation.Action.VERIFY
                            || verification.successfullyCompleted()) {
                        disposition =
                                verification.successfullyCompleted()
                                        ? Disposition.COMPLETED
                                        : Disposition.CANCELLED;
                        diagnostics.addAll(verification.diagnostics());
                        return;
                    }
                    store =
                            DurableTournamentRunStore.resume(
                                    requested,
                                    identity,
                                    resolver,
                                    invocation.recoverStaleLock(),
                                    this::cancellation);
                }
                runId = store.runId();
                sessionId = UUID.randomUUID();
                var retained = store.captureCommittedSet();
                committed = retained.games().size();
                epochs = retained.completeEpochs().size();
                reproducible =
                        retained.games().stream().allMatch(r -> r.fallbacks().isEmpty())
                                && TournamentReports.retainedFallbacks(store.historyJson())
                                        .isEmpty();
                store.startSession(
                        sessionId,
                        TournamentRecords.tree(
                                Map.of(
                                        "workers",
                                        options.workers(),
                                        "verbose",
                                        options.verbose(),
                                        "stallWarning",
                                        options.stallWarning().toString(),
                                        "action",
                                        invocation.action().name(),
                                        "recoverStaleLock",
                                        invocation.recoverStaleLock(),
                                        "build",
                                        store.configuration().build().identity())));
                sessionStarted = true;
                reportsExecutor =
                        Executors.newSingleThreadExecutor(r -> new Thread(r, "tournament-ratings"));
                var pending = store.pendingGames();
                if (!pending.isEmpty() && !cancellation()) {
                    players = playerFactory.create(store.configuration(), options.workers());
                    workers =
                            new ThreadPoolExecutor(
                                    options.workers(),
                                    options.workers(),
                                    0,
                                    TimeUnit.MILLISECONDS,
                                    new ArrayBlockingQueue<>(options.workers()),
                                    r -> new Thread(r, "tournament-game"));
                    state = TournamentProgress.State.RUNNING;
                    emit("Running tournament", true);
                    int index = 0;
                    while (index < pending.size() && !stopped.get() && !cancellation()) {
                        long epoch = pending.get(index).epoch();
                        int end = index;
                        while (end < pending.size() && pending.get(end).epoch() == epoch) end++;
                        playEpoch(pending.subList(index, end));
                        index = end;
                        if (!stopped.get() && !cancellation()) publish("RUNNING");
                    }
                }
            } catch (TournamentCancellationException e) {
                cancelled.set(true);
                if (e.getSuppressed().length > 0) fault(e);
            } catch (Throwable e) {
                fault(e);
            } finally {
                finish();
            }
        }

        void playEpoch(List<ScheduledTournamentGame> games) throws Exception {
            var completions = new ExecutorCompletionService<Attempt>(workers);
            int submitted = 0, outstanding = 0;
            while (outstanding > 0 || submitted < games.size()) {
                if (cancellation() && state != TournamentProgress.State.CANCELLING) {
                    state = TournamentProgress.State.CANCELLING;
                    emit("Cancellation requested; draining admitted games", true);
                }
                while (outstanding < options.workers()
                        && submitted < games.size()
                        && !stopped.get()
                        && !cancellation()) {
                    var game = games.get(submitted++);
                    long turn = nextTurn++;
                    completions.submit(() -> attempt(game, turn));
                    outstanding++;
                }
                if (outstanding == 0) break;
                Future<Attempt> ready;
                try {
                    ready = completions.poll(50, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    interrupted = true;
                    cancelled.set(true);
                    continue;
                }
                tick();
                if (ready == null) continue;
                outstanding--;
                Attempt result;
                try {
                    result = ready.get();
                } catch (ExecutionException e) {
                    fault(e.getCause());
                    continue;
                }
                synchronized (admission) {
                    running.remove(result.id);
                }
                if (result.result != null)
                    try {
                        store.commit(result.result);
                        committed++;
                        newCommits++;
                        reproducible &= result.result.fallbacks().isEmpty();
                    } catch (Throwable e) {
                        fault(e);
                    }
                if (result.failure != null) {
                    failures++;
                    try {
                        store.recordFailure(result.failure);
                    } catch (Throwable e) {
                        fault(e);
                    }
                }
                for (Throwable error : result.errors) fault(error);
                if (!result.fallbacks.isEmpty() && result.result == null) {
                    reproducible = false;
                    try {
                        store.diagnostic(
                                TournamentRecords.tree(
                                        Map.of(
                                                "attemptId",
                                                result.id.toString(),
                                                "fallbacks",
                                                result.fallbacks)));
                    } catch (Throwable e) {
                        fault(e);
                    }
                }
                emit(
                        result.errors.isEmpty() ? "Game progress" : "Game attempt failed",
                        result.errors.isEmpty() ? TournamentProgress.Kind.ROUTINE : TournamentProgress.Kind.FAILURE);
            }
        }

        long admissionTurn;

        Attempt attempt(ScheduledTournamentGame game, long turn) {
            UUID id = UUID.randomUUID();
            Lease lease = null;
            TournamentGameResult result = null;
            TournamentFailedAttempt failure = null;
            var errors = new ArrayList<Throwable>();
            List<TournamentInferenceFallback> fallbacks = List.of();
            boolean admittedHere = false;
            try (var logging = new TournamentLogging(options.verbose())) {
                synchronized (admission) {
                    while (turn != admissionTurn && !stopped.get() && !cancellation())
                        admission.wait(50);
                    if (stopped.get() || cancellation())
                        throw new TournamentCancellationException("Admission stopped");
                }
                lease = players.acquire(game, () -> stopped.get() || cancellation());
                synchronized (admission) {
                    if (stopped.get() || cancellation())
                        throw new TournamentCancellationException("Admission stopped");
                    running.put(id, new ActiveAttempt(game, id, nanoTime.getAsLong()));
                    admitted++;
                    admittedHere = true;
                    admissionTurn++;
                    admission.notifyAll();
                }
                result =
                        new TournamentGameCore()
                                .play(
                                        game,
                                        lease.players(),
                                        TournamentIdentities.resolvedDigest(store.configuration()),
                                        id,
                                        sessionId,
                                        lease::fallbacks);
            } catch (TournamentCancellationException e) {
                if (e.getSuppressed().length > 0) {
                    requestStop();
                    errors.add(e);
                }
            } catch (Throwable e) {
                requestStop();
                errors.add(e);
                failure =
                        e instanceof TournamentGameCore.GameFailure core
                                ? core.attempt()
                                : new TournamentFailedAttempt(
                                        game.gameId(),
                                        id,
                                        sessionId,
                                        admittedHere ? "PLAYING" : "PREPARING_PLAYERS",
                                        e.toString(),
                                        List.of());
            } finally {
                if (lease != null) {
                    try {
                        fallbacks = lease.fallbacks();
                    } catch (Throwable e) {
                        requestStop();
                        errors.add(e);
                    }
                    try {
                        if (!errors.isEmpty() && result == null) lease.discard();
                        else lease.close();
                    } catch (Throwable e) {
                        requestStop();
                        errors.add(e);
                    }
                }
            }
            return new Attempt(id, result, failure, errors, fallbacks);
        }

        void publish(String outcome) throws Exception {
            var fixed = store.captureCommittedSet();
            String history = store.historyJson();
            var metadata =
                    TournamentRecords.tree(
                            Map.of("outcome", outcome, "sessionId", sessionId.toString()));
            var future =
                    reportsExecutor.submit(
                            () -> {
                                try (var logging = new TournamentLogging(options.verbose())) {
                                    return reportCalculator.calculate(
                                            store.configuration(),
                                            store.schedule(),
                                            fixed,
                                            history,
                                            metadata);
                                }
                            });
            TournamentReports.Report report;
            while (true)
                try {
                    report = future.get(50, TimeUnit.MILLISECONDS);
                    break;
                } catch (TimeoutException e) {
                    tick();
                } catch (InterruptedException e) {
                    interrupted = true;
                    cancelled.set(true);
                }
            reports = store.publishReports(fixed, report.exports(), metadata);
            epochs = fixed.completeEpochs().size();
            emit("Ratings published", true);
        }

        void finish() {
            if (disposition == Disposition.CONFIGURATION_ERROR) return;
            requestStop();
            state = TournamentProgress.State.FINALIZING;
            emit("Finalizing tournament", true);
            if (workers != null) {
                workers.shutdown();
                await(workers);
            }
            if (players != null)
                try {
                    players.close();
                } catch (Throwable e) {
                    fault(e);
                }
            disposition =
                    failed
                            ? Disposition.FAILED
                            : committed == requested.gameCount()
                                    ? Disposition.COMPLETED
                                    : Disposition.CANCELLED;
            if (store != null && sessionStarted) {
                try {
                    publish(disposition.name());
                } catch (Throwable e) {
                    fault(e);
                    disposition = Disposition.FAILED;
                }
                try {
                    store.endSession(
                            sessionId,
                            disposition.name(),
                            TournamentRecords.tree(
                                    Map.of("diagnostics", List.copyOf(diagnostics))));
                } catch (Throwable e) {
                    fault(e);
                    disposition = Disposition.FAILED;
                }
            }
            if (reportsExecutor != null) {
                reportsExecutor.shutdown();
                await(reportsExecutor);
            }
            if (store != null)
                try {
                    store.close();
                } catch (Throwable e) {
                    failed = true;
                    diagnostics.add(e.toString());
                    disposition = Disposition.FAILED;
                }
            if (interrupted) Thread.currentThread().interrupt();
        }

        void await(ExecutorService executor) {
            while (true)
                try {
                    if (executor.awaitTermination(50, TimeUnit.MILLISECONDS)) break;
                    tick();
                } catch (InterruptedException e) {
                    interrupted = true;
                    cancelled.set(true);
                }
        }

        void tick() {
            if (cancellation() && state == TournamentProgress.State.RUNNING) {
                state = TournamentProgress.State.CANCELLING;
                emit("Cancellation requested; draining admitted games", true);
            }
            var warnings = new ArrayList<String>();
            synchronized (admission) {
                for (var attempt : running.values())
                    if (!attempt.warned
                            && elapsed(attempt.started).compareTo(options.stallWarning()) >= 0) {
                        attempt.warned = true;
                        warnings.add(
                                "Stall warning: game "
                                        + attempt.game.gameId()
                                        + " attempt "
                                        + attempt.id
                                        + " elapsed "
                                        + elapsed(attempt.started)
                                        + " competitors "
                                        + attempt.game.seats().stream()
                                                .map(TournamentSeat::competitorId)
                                                .toList());
                    }
            }
            for (String warning : warnings) {
                diagnostics.add(warning);
                emit(warning, TournamentProgress.Kind.STALL_WARNING);
            }
            emit("Tournament progress", false);
        }

        Duration elapsed(long since) {
            return Duration.ofNanos(Math.max(0, nanoTime.getAsLong() - since));
        }

        void emit(String message, boolean immediate) {
            emit(message, immediate ? TournamentProgress.Kind.NOTICE : TournamentProgress.Kind.ROUTINE);
        }

        void emit(String message, TournamentProgress.Kind kind) {
            boolean immediate = kind != TournamentProgress.Kind.ROUTINE;
            long now = nanoTime.getAsLong();
            if (!immediate && lastTick != Long.MIN_VALUE && now - lastTick < 1_000_000_000L) return;
            lastTick = now;
            var active = new ArrayList<TournamentMetrics.RunningAttempt>();
            long admittedCount;
            synchronized (admission) {
                admittedCount = admitted;
                for (var attempt : running.values())
                    active.add(
                            new TournamentMetrics.RunningAttempt(
                                    attempt.game.gameId(),
                                    attempt.id,
                                    attempt.game.seats().stream()
                                            .map(TournamentSeat::competitorId)
                                            .toList(),
                                    elapsed(attempt.started),
                                    attempt.warned));
            }
            Duration duration = elapsed(started);
            double seconds = duration.toNanos() / 1e9;
            double rate = seconds > 0 ? newCommits / seconds : 0;
            Optional<Duration> eta =
                    rate > 0
                            ? Optional.of(
                                    Duration.ofSeconds(
                                            (long)
                                                    Math.min(
                                                            Long.MAX_VALUE,
                                                            (requested.gameCount() - committed)
                                                                    / rate)))
                            : Optional.empty();
            var metrics =
                    new TournamentMetrics(
                            sequence++,
                            duration,
                            rate,
                            eta,
                            active,
                            Optional.ofNullable(reports)
                                    .map(r -> UUID.fromString(r.generationId())),
                            Optional.ofNullable(reports)
                                    .map(
                                            r ->
                                                    new String(
                                                            r.exports().get("summary.json"),
                                                            java.nio.charset.StandardCharsets
                                                                    .UTF_8)));
            try {
                observer.onProgress(
                        new TournamentProgress(
                                state,
                                requested.gameCount(),
                                admittedCount,
                                committed,
                                failures,
                                epochs,
                                cancellation(),
                                reproducible,
                                message,
                                metrics,
                                kind));
            } catch (Throwable observerFailure) {
                diagnostics.add("Progress observer: " + observerFailure);
            }
        }

        void emitTerminal() {
            if (disposition != Disposition.CONFIGURATION_ERROR) {
                state = TournamentProgress.State.valueOf(disposition.name());
                emit(disposition.name(), true);
            }
        }

        RunResult result() {
            return new RunResult(
                    disposition,
                    requested.output().absolute(),
                    Optional.ofNullable(runId),
                    Optional.ofNullable(sessionId),
                    Optional.ofNullable(reports),
                    issues,
                    diagnostics,
                    recovery);
        }

        final class ActiveAttempt {
            final ScheduledTournamentGame game;
            final UUID id;
            final long started;
            boolean warned;

            ActiveAttempt(ScheduledTournamentGame game, UUID id, long started) {
                this.game = game;
                this.id = id;
                this.started = started;
            }
        }

        record Attempt(
                UUID id,
                TournamentGameResult result,
                TournamentFailedAttempt failure,
                List<Throwable> errors,
                List<TournamentInferenceFallback> fallbacks) {}
    }
}
