package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.jskat.player.JSkatPlayer;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

class TournamentRunnerTest {
    private static final TournamentBuildIdentity BUILD =
            new TournamentBuildIdentity("a".repeat(40), Optional.empty());
    private Path parent;

    @BeforeEach
    void ext4() throws Exception {
        String root = System.getenv("JSKAT_TOURNAMENT_EXT4_TEST_ROOT");
        Assumptions.assumeTrue(root != null, "Select ext4 root");
        parent = Path.of(root).resolve("runner-" + UUID.randomUUID());
        Files.createDirectories(parent);
        Assumptions.assumeTrue(Files.getFileStore(parent).type().equals("ext4"));
    }

    private TournamentConfiguration configuration(String name) {
        return new TournamentConfigurationReader()
                .read(
                        TournamentConfigurationTest.VALID
                                .replace("runs/test", parent.resolve(name).toString())
                                .replace("\"epochs\":2", "\"epochs\":2,\"seed\":\"7\""),
                        parent.resolve("config.json"))
                .value();
    }

    private TournamentService service(
            TournamentService.PlayerFactory factory, TournamentService.ReportCalculator reports) {
        return new TournamentService(
                new DefaultTournamentInputResolver(parent.resolve("unused")),
                () -> BUILD,
                factory,
                reports,
                System::nanoTime,
                TournamentPublicationIO.LOCAL);
    }

    private TournamentService.Players players(
            ResolvedTournamentConfiguration config,
            int workers,
            java.util.function.Consumer<String> callback,
            boolean releaseFailure,
            java.util.function.Consumer<Void> cleanup) {
        var pool = new TournamentPlayerPool(config.competitors(), workers);
        return new TournamentService.Players() {
            public TournamentService.Lease acquire(
                    ScheduledTournamentGame game, CancellationToken cancel) {
                var lease = pool.acquire(game, cancel);
                var proxies =
                        lease.players().stream()
                                .map(
                                        p ->
                                                (JSkatPlayer)
                                                        Proxy.newProxyInstance(
                                                                JSkatPlayer.class.getClassLoader(),
                                                                new Class<?>[] {JSkatPlayer.class},
                                                                (proxy, method, args) -> {
                                                                    callback.accept(
                                                                            method.getName());
                                                                    try {
                                                                        return method.invoke(
                                                                                p, args);
                                                                    } catch (
                                                                            InvocationTargetException
                                                                                    e) {
                                                                        throw e.getCause();
                                                                    }
                                                                }))
                                .toList();
                return new TournamentService.Lease() {
                    public List<JSkatPlayer> players() {
                        return proxies;
                    }

                    public List<TournamentInferenceFallback> fallbacks() {
                        return lease.fallbacks();
                    }

                    public void discard() {
                        lease.discard();
                    }

                    public void close() {
                        lease.close();
                        if (releaseFailure) throw new IllegalStateException("lease return failed");
                    }
                };
            }

            public void close() {
                pool.close();
                cleanup.accept(null);
            }
        };
    }

    private TournamentService normal() {
        return service(
                (c, w) -> players(c, w, m -> {}, false, v -> {}), TournamentReports::calculate);
    }

    private TournamentService.RunResult run(
            TournamentService service,
            TournamentConfiguration config,
            int workers,
            CancellationToken cancel,
            TournamentProgress.Observer observer) {
        return service.run(
                config,
                new TournamentInvocation(TournamentInvocation.Action.NEW, false),
                new TournamentSessionOptions(workers, false, Duration.ofSeconds(60)),
                cancel,
                observer);
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(30, TimeUnit.SECONDS)) throw new AssertionError("Latch timed out");
        } catch (InterruptedException e) {
            throw new AssertionError("Worker interrupted", e);
        }
    }

    @Test
    void unreleasedOwnershipHasTypedRecoveryButUnrelatedStaleErrorsDoNot() throws Exception {
        var config = configuration("recovery-status");
        UUID runId;
        try (var store = DurableTournamentRunStore.create(config, 7, BUILD,
                new DefaultTournamentInputResolver(parent.resolve("unused")), CancellationToken.NONE)) {
            runId = store.runId();
        }
        var ownerPath = config.output().absolute().resolve("lock-owner.json");
        var owner = TournamentRecords.decode(Files.readAllBytes(ownerPath), runId, "lock-owner");
        ((com.fasterxml.jackson.databind.node.ObjectNode) owner).put("released", false).put("releasedAt", "");
        Files.write(ownerPath, TournamentRecords.encode(runId, "lock-owner", owner));
        var denied = normal().run(config, new TournamentInvocation(TournamentInvocation.Action.RESUME, false),
                TournamentSessionOptions.defaults(), CancellationToken.NONE, p -> {});
        assertThat(denied.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        assertThat(denied.recovery()).isEqualTo(TournamentService.Recovery.EXPLICIT_AUTHORIZATION_REQUIRED);
        var recovered = normal().run(config, new TournamentInvocation(TournamentInvocation.Action.RESUME, true),
                TournamentSessionOptions.defaults(), CancellationToken.NONE, p -> {});
        assertThat(recovered.disposition()).describedAs(recovered.diagnostics().toString())
                .isEqualTo(TournamentService.Disposition.COMPLETED);
        assertThat(recovered.recovery()).isEqualTo(TournamentService.Recovery.NOT_REQUIRED);
        var unrelated = service((c, w) -> { throw new IllegalStateException("stale model path"); },
                TournamentReports::calculate);
        var failed = run(unrelated, configuration("unrelated"), 1, CancellationToken.NONE, p -> {});
        assertThat(failed.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        assertThat(failed.recovery()).isEqualTo(TournamentService.Recovery.NOT_REQUIRED);
    }

    @Test
    void ownerReleaseErrorStillReleasesOsLockAndReturnsFailed() throws Exception {
        var owners = new AtomicInteger();
        var io =
                new TournamentPublicationIO() {
                    public void forceFile(java.nio.channels.FileChannel file)
                            throws java.io.IOException {
                        LOCAL.forceFile(file);
                    }

                    public void forceDirectory(Path directory) throws java.io.IOException {
                        LOCAL.forceDirectory(directory);
                    }

                    public void atomicMove(Path temporary, Path target, boolean replace)
                            throws java.io.IOException {
                        if (target.getFileName().toString().equals("lock-owner.json")
                                && owners.incrementAndGet() == 2)
                            throw new AssertionError("owner release failed");
                        LOCAL.atomicMove(temporary, target, replace);
                    }
                };
        var service =
                new TournamentService(
                        new DefaultTournamentInputResolver(parent.resolve("unused")),
                        () -> BUILD,
                        (c, w) -> players(c, w, m -> {}, false, v -> {}),
                        TournamentReports::calculate,
                        System::nanoTime,
                        io);
        var config = configuration("owner-error");
        var result = run(service, config, 3, CancellationToken.NONE, p -> {});
        assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        assertThat(result.diagnostics()).anyMatch(s -> s.contains("owner release failed"));
        assertThat(TournamentService.isActive()).isFalse();
        assertThat(normal().verify(config, CancellationToken.NONE).successfullyCompleted())
                .isFalse();
    }

    @Test
    void diagnosticErrorsCannotSkipFinalReportsOrStoreReleaseAfterCleanupFailure()
            throws Exception {
        var io =
                new TournamentPublicationIO() {
                    public void forceFile(java.nio.channels.FileChannel file)
                            throws java.io.IOException {
                        LOCAL.forceFile(file);
                    }

                    public void forceDirectory(Path directory) throws java.io.IOException {
                        LOCAL.forceDirectory(directory);
                    }

                    public void atomicMove(Path temporary, Path target, boolean replace)
                            throws java.io.IOException {
                        if (target.getParent().getFileName().toString().equals("diagnostics"))
                            throw new AssertionError("diagnostic publication failed");
                        LOCAL.atomicMove(temporary, target, replace);
                    }
                };
        var service =
                new TournamentService(
                        new DefaultTournamentInputResolver(parent.resolve("unused")),
                        () -> BUILD,
                        (c, w) ->
                                players(
                                        c,
                                        w,
                                        m -> {},
                                        false,
                                        v -> {
                                            throw new IllegalStateException("pool cleanup failed");
                                        }),
                        TournamentReports::calculate,
                        System::nanoTime,
                        io);
        var config = configuration("cleanup-errors");
        var observed = new ArrayList<TournamentProgress>();
        var result = run(service, config, 3, CancellationToken.NONE, observed::add);
        assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        assertThat(result.diagnostics())
                .anyMatch(s -> s.contains("pool cleanup failed"))
                .anyMatch(s -> s.contains("diagnostic publication failed"));
        assertThat(observed.getLast().state()).isEqualTo(TournamentProgress.State.FAILED);
        assertThat(TournamentService.isActive()).isFalse();
        var verification = normal().verify(config, CancellationToken.NONE);
        assertThat(verification.committedSet().games()).hasSize(12);
        assertThat(verification.successfullyCompleted()).isFalse();
        assertThat(verification.reports()).isPresent();
        assertThat(
                        new String(
                                verification.reports().orElseThrow().exports().get("summary.json"),
                                java.nio.charset.StandardCharsets.UTF_8))
                .contains("FAILED");
    }

    @Test
    void processGateIsHeldDuringPreparationAndReleasedAfterPreparationFailure() throws Exception {
        var preparing = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var service =
                new TournamentService(
                        new DefaultTournamentInputResolver(parent.resolve("unused")),
                        () -> {
                            preparing.countDown();
                            await(release);
                            throw new IllegalStateException("build identity failed");
                        },
                        (c, w) -> {
                            throw new AssertionError("Preparation failure constructed players");
                        },
                        TournamentReports::calculate,
                        System::nanoTime,
                        TournamentPublicationIO.LOCAL);
        var config = configuration("prepare-gate");
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future =
                    executor.submit(() -> run(service, config, 1, CancellationToken.NONE, p -> {}));
            await(preparing);
            assertThat(TournamentService.isActive()).isTrue();
            assertThat(
                            run(
                                            normal(),
                                            configuration("prepare-other"),
                                            1,
                                            CancellationToken.NONE,
                                            p -> {})
                                    .disposition())
                    .isEqualTo(TournamentService.Disposition.FAILED);
            assertThat(Files.exists(config.output().absolute())).isFalse();
            assertThat(Files.exists(parent.resolve("prepare-other"))).isFalse();
            release.countDown();
            assertThat(future.get(30, TimeUnit.SECONDS).disposition())
                    .isEqualTo(TournamentService.Disposition.CONFIGURATION_ERROR);
            assertThat(TournamentService.isActive()).isFalse();
            assertThat(
                            run(
                                            normal(),
                                            configuration("after-prepare-failure"),
                                            1,
                                            CancellationToken.NONE,
                                            p -> {})
                                    .disposition())
                    .isEqualTo(TournamentService.Disposition.COMPLETED);
        } finally {
            release.countDown();
            executor.shutdown();
        }
    }

    @Test
    void laterEpochStartsWhileAnEarlierGameIsStillRunning() throws Exception {
        var release = new CountDownLatch(1);
        var laterEpoch = new CountDownLatch(1);
        var laterEpochCompleted = new CountDownLatch(1);
        var first = new AtomicBoolean();
        var service = service((c, w) -> {
            var delegate = players(c, w, m -> {}, false, v -> {});
            return new TournamentService.Players() {
                public TournamentService.Lease acquire(ScheduledTournamentGame game, CancellationToken cancel) {
                    var lease = delegate.acquire(game, cancel);
                    if (game.epoch() > 0) laterEpoch.countDown();
                    if (!first.compareAndSet(false, true)) return lease;
                    return new TournamentService.Lease() {
                        public List<JSkatPlayer> players() { await(release); return lease.players(); }
                        public List<TournamentInferenceFallback> fallbacks() { return lease.fallbacks(); }
                        public void discard() { lease.discard(); }
                        public void close() { lease.close(); }
                    };
                }
                public void close() { delegate.close(); }
            };
        }, TournamentReports::calculate);
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future = executor.submit(() -> run(service, configuration("cross-epoch"), 8,
                    CancellationToken.NONE, p -> {
                        if (p.committedGames() == 11 && p.completeEpochs() == 1) laterEpochCompleted.countDown();
                    }));
            assertThat(laterEpoch.await(5, TimeUnit.SECONDS))
                    .describedAs("Next epoch must start before the slow earlier game finishes")
                    .isTrue();
            assertThat(laterEpochCompleted.await(5, TimeUnit.SECONDS))
                    .describedAs("A later epoch can complete while the earlier epoch is incomplete")
                    .isTrue();
            release.countDown();
            assertThat(future.get(30, TimeUnit.SECONDS).disposition())
                    .isEqualTo(TournamentService.Disposition.COMPLETED);
        } finally {
            release.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    void slowReportsDoNotStopGamesAndPendingEpochReportsAreCoalesced() throws Exception {
        var reporting = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var allCommitted = new CountDownLatch(1);
        var calculations = new AtomicInteger();
        var service = service((c, w) -> players(c, w, m -> {}, false, v -> {}), (c, s, fixed, h, m) -> {
            if (calculations.incrementAndGet() == 1) {
                assertThat(fixed.games()).hasSize(6);
                reporting.countDown();
                await(release);
                assertThat(fixed.games()).hasSize(6);
            }
            return TournamentReports.calculate(c, s, fixed, h, m);
        });
        var config = new TournamentConfigurationReader().read(
                configuration("background-reports").originalJson().replace("\"epochs\":2", "\"epochs\":6"),
                parent.resolve("config.json")).value();
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future = executor.submit(() -> run(service, config, 1, CancellationToken.NONE, p -> {
                if (p.committedGames() == 36) allCommitted.countDown();
            }));
            await(reporting);
            assertThat(allCommitted.await(5, TimeUnit.SECONDS))
                    .describedAs("Games must keep committing while the report calculator is blocked")
                    .isTrue();
            assertThat(future.isDone()).isFalse();
            release.countDown();
            var result = future.get(30, TimeUnit.SECONDS);
            assertThat(result.disposition()).describedAs(result.diagnostics().toString())
                    .isEqualTo(TournamentService.Disposition.COMPLETED);
            assertThat(calculations).hasValue(2);
            var verified = service.verify(config, CancellationToken.NONE);
            assertThat(verified.successfullyCompleted()).isTrue();
            assertThat(verified.committedSet().completeEpochs()).hasSize(6);
        } finally {
            release.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    void reportFailureStopsFurtherAdmissionAndDrainsWaitingWorkers() throws Exception {
        var release = new CountDownLatch(1);
        var failed = new CountDownLatch(1);
        var first = new AtomicBoolean();
        var service = service((c, w) -> {
            var delegate = players(c, w, m -> {}, false, v -> {});
            return new TournamentService.Players() {
                public TournamentService.Lease acquire(ScheduledTournamentGame game, CancellationToken cancel) {
                    if (game.epoch() > 0) await(release);
                    return delegate.acquire(game, cancel);
                }
                public void close() { delegate.close(); }
            };
        }, (c, s, fixed, h, m) -> {
            if (first.compareAndSet(false, true)) throw new IllegalStateException("background ratings failed");
            return TournamentReports.calculate(c, s, fixed, h, m);
        });
        var config = configuration("report-failure");
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future = executor.submit(() -> run(service, config, 1, CancellationToken.NONE, p -> {
                if (p.kind() == TournamentProgress.Kind.FAILURE) failed.countDown();
            }));
            await(failed);
            release.countDown();
            var result = future.get(30, TimeUnit.SECONDS);
            assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
            assertThat(result.diagnostics()).anyMatch(s -> s.contains("background ratings failed"));
            assertThat(service.verify(config, CancellationToken.NONE).committedSet().games()).hasSize(6);
        } finally {
            release.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    void cancellationWaitsForBackgroundReportingBeforeReleasingTheRun() throws Exception {
        var reporting = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var cancelling = new CountDownLatch(1);
        var cancellation = new TournamentCancellation();
        var first = new AtomicBoolean();
        var service = service((c, w) -> {
            var delegate = players(c, w, m -> {}, false, v -> {});
            return new TournamentService.Players() {
                public TournamentService.Lease acquire(ScheduledTournamentGame game, CancellationToken cancel) {
                    if (game.epoch() > 0) {
                        await(reporting);
                        cancellation.requestCancellation();
                    }
                    return delegate.acquire(game, cancel);
                }
                public void close() { delegate.close(); }
            };
        }, (c, s, fixed, h, m) -> {
            if (first.compareAndSet(false, true)) {
                reporting.countDown();
                await(release);
            }
            return TournamentReports.calculate(c, s, fixed, h, m);
        });
        var config = configuration("cancel-report");
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future = executor.submit(() -> run(service, config, 1, cancellation, p -> {
                if (p.cancellationRequested()) cancelling.countDown();
            }));
            await(cancelling);
            assertThat(future.isDone()).isFalse();
            assertThat(TournamentService.isActive()).isTrue();
            release.countDown();
            var result = future.get(30, TimeUnit.SECONDS);
            assertThat(result.disposition()).describedAs(result.diagnostics().toString())
                    .isEqualTo(TournamentService.Disposition.CANCELLED);
            assertThat(service.verify(config, CancellationToken.NONE).committedSet().games()).hasSize(6);
            assertThat(TournamentService.isActive()).isFalse();
        } finally {
            release.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    void reportCacheInitializationFailureReleasesTheProcessGate() {
        var calculator = new TournamentService.ReportCalculator() {
            public TournamentService.ReportCalculator forSession() {
                throw new IllegalStateException("report cache initialization failed");
            }
            public TournamentReports.Report calculate(ResolvedTournamentConfiguration c,
                    List<ScheduledTournamentGame> s, DurableTournamentRunStore.CommittedSet set,
                    String history, com.fasterxml.jackson.databind.JsonNode metadata) {
                throw new AssertionError("No reports should be calculated");
            }
        };
        var config = configuration("cache-initialization");
        var result = run(service((c, w) -> { throw new AssertionError("No players should be constructed"); }, calculator),
                config, 1, CancellationToken.NONE, p -> {});
        assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        assertThat(result.diagnostics()).anyMatch(s -> s.contains("report cache initialization failed"));
        assertThat(TournamentService.isActive()).isFalse();
        assertThat(Files.exists(config.output().absolute())).isFalse();
    }

    @Test
    void actualParallelRunCompletesAndCompletedResumeConstructsNoPlayers() throws Exception {
        var config = configuration("parallel");
        var service = normal();
        var progress = new ArrayList<TournamentProgress>();
        var result = run(service, config, 3, CancellationToken.NONE, progress::add);
        assertThat(result.disposition())
                .describedAs(result.diagnostics().toString())
                .isEqualTo(TournamentService.Disposition.COMPLETED);
        assertThat(service.verify(config, CancellationToken.NONE).successfullyCompleted()).isTrue();
        assertThat(progress.getLast().committedGames()).isEqualTo(12);
        var forbidden =
                service(
                        (c, w) -> {
                            throw new AssertionError("Completed resume constructed players");
                        },
                        TournamentReports::calculate);
        var resumed =
                forbidden.run(
                        config,
                        new TournamentInvocation(TournamentInvocation.Action.RESUME, false),
                        TournamentSessionOptions.defaults(),
                        CancellationToken.NONE,
                        p -> {});
        assertThat(resumed.disposition()).isEqualTo(TournamentService.Disposition.COMPLETED);
    }

    @Test
    void cancellationDrainsAdmittedGamesAndGateRemainsOwnedThroughCleanup() throws Exception {
        var started = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var cleanup = new CountDownLatch(1);
        var cleanRelease = new CountDownLatch(1);
        var cancellation = new TournamentCancellation();
        var firstCallback = new AtomicBoolean();
        var service =
                service(
                        (c, w) ->
                                players(
                                        c,
                                        w,
                                        m -> {
                                            if (firstCallback.compareAndSet(false, true)) {
                                                started.countDown();
                                                await(release);
                                            }
                                        },
                                        false,
                                        v -> {
                                            cleanup.countDown();
                                            await(cleanRelease);
                                        }),
                        TournamentReports::calculate);
        var config = configuration("cancel");
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future = executor.submit(() -> run(service, config, 1, cancellation, p -> {}));
            await(started);
            cancellation.requestCancellation();
            release.countDown();
            await(cleanup);
            assertThat(TournamentService.isActive()).isTrue();
            assertThat(future.isDone()).isFalse();
            var competing =
                    run(normal(), configuration("other"), 1, CancellationToken.NONE, p -> {});
            assertThat(competing.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
            assertThat(Files.exists(parent.resolve("other"))).isFalse();
            cleanRelease.countDown();
            var result = future.get(30, TimeUnit.SECONDS);
            assertThat(result.disposition())
                    .describedAs(result.diagnostics().toString())
                    .isEqualTo(TournamentService.Disposition.CANCELLED);
            var retained = service.verify(config, CancellationToken.NONE);
            assertThat(retained.committedSet().games()).hasSize(1);
            assertThat(TournamentService.isActive()).isFalse();
        } finally {
            release.countDown();
            cleanRelease.countDown();
            executor.shutdown();
        }
    }

    @Test
    void observerFailureDoesNotChangeDurableCompletion() {
        var service = normal();
        var result =
                run(
                        service,
                        configuration("observer"),
                        3,
                        CancellationToken.NONE,
                        p -> {
                            throw new IllegalArgumentException("observer broken");
                        });
        assertThat(result.disposition())
                .describedAs(result.diagnostics().toString())
                .isEqualTo(TournamentService.Disposition.COMPLETED);
        assertThat(result.diagnostics()).anyMatch(s -> s.contains("observer broken"));
    }

    @Test
    void preFreezeFailureIsUnscoredAndStopsAdmissionWhilePostFreezeCloseRetainsScore()
            throws Exception {
        var broken =
                service(
                        (c, w) ->
                                players(
                                        c,
                                        w,
                                        m -> {
                                            if (m.equals("finalizeGame"))
                                                throw new IllegalStateException("finalize failed");
                                        },
                                        false,
                                        v -> {}),
                        TournamentReports::calculate);
        var config = configuration("prefreeze");
        var result = run(broken, config, 1, CancellationToken.NONE, p -> {});
        assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        var verification = broken.verify(config, CancellationToken.NONE);
        assertThat(verification.committedSet().games()).isEmpty();
        try (var attempts = Files.list(config.output().absolute().resolve("failed-attempts"))) {
            assertThat(attempts.toList()).hasSize(1);
        }
        var closeBroken =
                service(
                        (c, w) -> players(c, w, m -> {}, true, v -> {}),
                        TournamentReports::calculate);
        var second = configuration("postfreeze");
        var closed = run(closeBroken, second, 1, CancellationToken.NONE, p -> {});
        assertThat(closed.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        assertThat(closeBroken.verify(second, CancellationToken.NONE).committedSet().games())
                .hasSize(1);
    }

    @Test
    void backgroundReportFailureStillFailsRunAfterGamesComplete() throws Exception {
        var calculating = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var acquired = new CopyOnWriteArrayList<Long>();
        var allCommitted = new CountDownLatch(1);
        var first = new AtomicBoolean();
        var service =
                service(
                        (c, w) -> {
                            var delegate = players(c, w, m -> {}, false, v -> {});
                            return new TournamentService.Players() {
                                public TournamentService.Lease acquire(
                                        ScheduledTournamentGame game, CancellationToken cancel) {
                                    acquired.add(game.epoch());
                                    return delegate.acquire(game, cancel);
                                }

                                public void close() {
                                    delegate.close();
                                }
                            };
                        },
                        (c, schedule, set, history, metadata) -> {
                            if (first.compareAndSet(false, true)) {
                                calculating.countDown();
                                await(release);
                                throw new IllegalStateException("ratings failed");
                            }
                            return TournamentReports.calculate(c, schedule, set, history, metadata);
                        });
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future =
                    executor.submit(
                            () ->
                                    run(
                                            service,
                                            configuration("barrier"),
                                            3,
                                            CancellationToken.NONE,
                                            p -> { if (p.committedGames() == 12) allCommitted.countDown(); }));
            await(calculating);
            await(allCommitted);
            assertThat(acquired).hasSize(12).contains(1L);
            assertThat(future.isDone()).isFalse();
            release.countDown();
            assertThat(future.get(30, TimeUnit.SECONDS).disposition())
                    .isEqualTo(TournamentService.Disposition.FAILED);
            assertThat(acquired).hasSize(12);
        } finally {
            release.countDown();
            executor.shutdown();
        }
    }

    @Test
    void aStallWarnsOnceUsingMonotonicTimeAndNeverInterruptsTheGame() throws Exception {
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var warned = new CountDownLatch(1);
        var clock = new AtomicLong();
        var first = new AtomicBoolean();
        var warnings = new AtomicInteger();
        var cancellation = new TournamentCancellation();
        var service =
                new TournamentService(
                        new DefaultTournamentInputResolver(parent.resolve("unused")),
                        () -> BUILD,
                        (c, w) ->
                                players(
                                        c,
                                        w,
                                        m -> {
                                            if (first.compareAndSet(false, true)) {
                                                entered.countDown();
                                                await(release);
                                            }
                                        },
                                        false,
                                        v -> {}),
                        TournamentReports::calculate,
                        clock::get,
                        TournamentPublicationIO.LOCAL);
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future =
                    executor.submit(
                            () ->
                                    service.run(
                                            configuration("stall"),
                                            new TournamentInvocation(
                                                    TournamentInvocation.Action.NEW, false),
                                            new TournamentSessionOptions(
                                                    1, false, Duration.ofSeconds(1)),
                                            cancellation,
                                            p -> {
                                                if (p.kind() == TournamentProgress.Kind.STALL_WARNING) {
                                                    warnings.incrementAndGet();
                                                    warned.countDown();
                                                }
                                            }));
            await(entered);
            clock.set(1_000_000_000L);
            await(warned);
            clock.set(5_000_000_000L);
            cancellation.requestCancellation();
            release.countDown();
            var result = future.get(30, TimeUnit.SECONDS);
            assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.CANCELLED);
            assertThat(warnings).hasValue(1);
        } finally {
            release.countDown();
            executor.shutdown();
        }
    }

    @Test
    void aWorkerFailureStopsFurtherAdmissionBeforeItsFreedSlotCanBeReused() throws Exception {
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var failed = new CountDownLatch(1);
        var callbacks = new AtomicInteger();
        var acquisitions = new AtomicInteger();
        var service =
                service(
                        (c, w) -> {
                            var delegate =
                                    players(
                                            c,
                                            w,
                                            m -> {
                                                int call = callbacks.incrementAndGet();
                                                if (call == 1) {
                                                    entered.countDown();
                                                    await(release);
                                                } else if (call == 2)
                                                    throw new IllegalStateException(
                                                            "second worker failed");
                                            },
                                            false,
                                            v -> {});
                            return new TournamentService.Players() {
                                public TournamentService.Lease acquire(
                                        ScheduledTournamentGame game, CancellationToken cancel) {
                                    acquisitions.incrementAndGet();
                                    return delegate.acquire(game, cancel);
                                }

                                public void close() {
                                    delegate.close();
                                }
                            };
                        },
                        TournamentReports::calculate);
        var config = configuration("race");
        var executor = Executors.newSingleThreadExecutor();
        try {
            var future =
                    executor.submit(
                            () ->
                                    run(
                                            service,
                                            config,
                                            2,
                                            CancellationToken.NONE,
                                            p -> {
                                                if (p.failedAttempts() > 0) failed.countDown();
                                            }));
            await(entered);
            await(failed);
            release.countDown();
            var result = future.get(30, TimeUnit.SECONDS);
            assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
            assertThat(acquisitions).hasValue(2);
            assertThat(service.verify(config, CancellationToken.NONE).committedSet().games())
                    .hasSize(1);
        } finally {
            release.countDown();
            executor.shutdown();
        }
    }

    @Test
    void explicitResumeRetriesOnlyPendingGamesAndMatchesSequentialOutcome() throws Exception {
        var cancellation = new TournamentCancellation();
        var first = new AtomicBoolean();
        var cancelling =
                service(
                        (c, w) ->
                                players(
                                        c,
                                        w,
                                        m -> {
                                            if (first.compareAndSet(false, true))
                                                cancellation.requestCancellation();
                                        },
                                        false,
                                        v -> {}),
                        TournamentReports::calculate);
        var config = configuration("resume");
        assertThat(run(cancelling, config, 1, cancellation, p -> {}).disposition())
                .isEqualTo(TournamentService.Disposition.CANCELLED);
        var before = cancelling.verify(config, CancellationToken.NONE).committedSet().games();
        assertThat(before).hasSize(1);
        var resumed =
                normal().run(
                                config,
                                new TournamentInvocation(TournamentInvocation.Action.RESUME, false),
                                new TournamentSessionOptions(3, false, Duration.ofSeconds(60)),
                                CancellationToken.NONE,
                                p -> {});
        assertThat(resumed.disposition())
                .describedAs(resumed.diagnostics().toString())
                .isEqualTo(TournamentService.Disposition.COMPLETED);
        var after = normal().verify(config, CancellationToken.NONE).committedSet().games();
        assertThat(after).hasSize(12);
        assertThat(after)
                .anySatisfy(
                        game -> {
                            assertThat(game.gameId()).isEqualTo(before.getFirst().gameId());
                            assertThat(game.attemptId()).isEqualTo(before.getFirst().attemptId());
                        });
        var baseline = configuration("sequential");
        var sequential = run(normal(), baseline, 1, CancellationToken.NONE, p -> {});
        assertThat(sequential.disposition()).isEqualTo(TournamentService.Disposition.COMPLETED);
        var expected = normal().verify(baseline, CancellationToken.NONE).committedSet().games();
        assertThat(
                        after.stream()
                                .map(
                                        g ->
                                                List.of(
                                                        g.gameId(),
                                                        g.outcome(),
                                                        g.seatScores(),
                                                        g.events()))
                                .toList())
                .isEqualTo(
                        expected.stream()
                                .map(
                                        g ->
                                                List.of(
                                                        g.gameId(),
                                                        g.outcome(),
                                                        g.seatScores(),
                                                        g.events()))
                                .toList());
    }

    @Test
    void publicationOnlyRecoveryAndCancellationAfterLastCommitNeedNoNewGames() throws Exception {
        var cancellation = new TournamentCancellation();
        var config = configuration("publication");
        var result =
                run(
                        normal(),
                        config,
                        3,
                        cancellation,
                        p -> {
                            if (p.committedGames() == 12) cancellation.requestCancellation();
                        });
        assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.COMPLETED);
        Files.delete(config.output().absolute().resolve("reports/current.json"));
        var recovering =
                service(
                        (c, w) -> {
                            throw new AssertionError("Publication recovery constructed players");
                        },
                        TournamentReports::calculate);
        var recovered =
                recovering.run(
                        config,
                        new TournamentInvocation(TournamentInvocation.Action.RESUME, false),
                        TournamentSessionOptions.defaults(),
                        CancellationToken.NONE,
                        p -> {});
        assertThat(recovered.disposition())
                .describedAs(recovered.diagnostics().toString())
                .isEqualTo(TournamentService.Disposition.COMPLETED);
        assertThat(recovering.verify(config, CancellationToken.NONE).successfullyCompleted())
                .isTrue();
    }

    @Test
    void postRenameCommitUncertaintyDoesNotInflateAcknowledgedCountsAndResumeFindsTheGame()
            throws Exception {
        var once = new AtomicBoolean();
        var io =
                new TournamentPublicationIO() {
                    public void forceFile(java.nio.channels.FileChannel file)
                            throws java.io.IOException {
                        LOCAL.forceFile(file);
                    }

                    public void forceDirectory(Path directory) throws java.io.IOException {
                        LOCAL.forceDirectory(directory);
                    }

                    public void atomicMove(Path temporary, Path target, boolean replace)
                            throws java.io.IOException {
                        LOCAL.atomicMove(temporary, target, replace);
                        if (target.getParent().getFileName().toString().equals("games")
                                && once.compareAndSet(false, true))
                            throw new java.io.IOException("uncertain game commit");
                    }
                };
        var service =
                new TournamentService(
                        new DefaultTournamentInputResolver(parent.resolve("unused")),
                        () -> BUILD,
                        (c, w) -> players(c, w, m -> {}, false, v -> {}),
                        TournamentReports::calculate,
                        System::nanoTime,
                        io);
        var config = configuration("uncertain");
        var observations = new ArrayList<TournamentProgress>();
        var result = run(service, config, 1, CancellationToken.NONE, observations::add);
        assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        assertThat(observations.getLast().committedGames()).isZero();
        assertThat(normal().verify(config, CancellationToken.NONE).committedSet().games())
                .hasSize(1);
        var resumed =
                normal().run(
                                config,
                                new TournamentInvocation(TournamentInvocation.Action.RESUME, false),
                                TournamentSessionOptions.defaults(),
                                CancellationToken.NONE,
                                p -> {});
        assertThat(resumed.disposition())
                .describedAs(resumed.diagnostics().toString())
                .isEqualTo(TournamentService.Disposition.COMPLETED);
    }

    @Test
    void callbackCancellationExceptionIsAnUnscoredFailureEvenDuringRequestedCancellation()
            throws Exception {
        var cancellation = new TournamentCancellation();
        var broken =
                service(
                        (c, w) ->
                                players(
                                        c,
                                        w,
                                        m -> {
                                            cancellation.requestCancellation();
                                            throw new CancellationException("callback failed");
                                        },
                                        false,
                                        v -> {}),
                        TournamentReports::calculate);
        var config = configuration("callback-cancel");
        var result = run(broken, config, 1, cancellation, p -> {});
        assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.FAILED);
        assertThat(broken.verify(config, CancellationToken.NONE).committedSet().games()).isEmpty();
    }

    @Test
    void failedAttemptFallbackEvidenceSurvivesResumeAndRemainsInReports() throws Exception {
        var delegate = new DefaultTournamentInputResolver(parent.resolve("unused"));
        var config = configuration("fallback-history");
        try (var store =
                DurableTournamentRunStore.create(
                        config, 7, BUILD, delegate, CancellationToken.NONE)) {
            store.diagnostic(
                    TournamentRecords.tree(
                            Map.of(
                                    "attemptId",
                                    UUID.randomUUID().toString(),
                                    "fallbacks",
                                    List.of(
                                            new TournamentInferenceFallback(
                                                    config.competitors().getFirst().id(),
                                                    store.schedule().getFirst().gameId(),
                                                    "BIDDING",
                                                    "unseeded fallback")))));
        }
        var observed = new ArrayList<TournamentProgress>();
        var result =
                normal().run(
                                config,
                                new TournamentInvocation(TournamentInvocation.Action.RESUME, false),
                                TournamentSessionOptions.defaults(),
                                CancellationToken.NONE,
                                observed::add);
        assertThat(result.disposition())
                .describedAs(result.diagnostics().toString())
                .isEqualTo(TournamentService.Disposition.COMPLETED);
        var summary =
                CanonicalJson.parse(
                        new String(
                                result.reports().orElseThrow().exports().get("summary.json"),
                                java.nio.charset.StandardCharsets.UTF_8));
        assertThat(summary.path("reproducible").asBoolean()).isFalse();
        assertThat(summary.path("inferenceFallbacks")).hasSize(1);
        assertThat(
                        observed.stream()
                                .filter(p -> p.state() == TournamentProgress.State.RUNNING)
                                .map(TournamentProgress::reproducible))
                .containsOnly(false);
        var completed = new ArrayList<TournamentProgress>();
        normal().run(
                        config,
                        new TournamentInvocation(TournamentInvocation.Action.RESUME, false),
                        TournamentSessionOptions.defaults(),
                        CancellationToken.NONE,
                        completed::add);
        assertThat(completed.getLast().reproducible()).isFalse();
    }
}
