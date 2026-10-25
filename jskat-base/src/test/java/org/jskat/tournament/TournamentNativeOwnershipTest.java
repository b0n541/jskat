package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import ai.onnxruntime.*;

import org.jskat.ai.ml.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.util.*;

/** Native session API is the external resource boundary; real player construction owns it. */
class TournamentNativeOwnershipTest {
    @TempDir Path directory;

    ResolvedModelSet models() throws Exception {
        var paths = new HashMap<String, Path>();
        for (var name : ModelManager.REQUIRED_MODEL_FILES) {
            Path p = directory.resolve(name);
            Files.write(p, new byte[0]);
            paths.put(name, p);
        }
        return ResolvedModelSet.of("fixture", paths);
    }

    @Test
    void closesEarlierSessionsWhenLaterModelConstructionFails() throws Exception {
        var models = models();
        var env = mock(OrtEnvironment.class);
        var first = mock(OrtSession.class);
        var second = mock(OrtSession.class);
        try (var environment = mockStatic(OrtEnvironment.class)) {
            environment.when(OrtEnvironment::getEnvironment).thenReturn(env);
            when(env.createSession(anyString(), any(OrtSession.SessionOptions.class)))
                    .thenReturn(first, second)
                    .thenThrow(new OrtException("native failure"));
            assertThatThrownBy(() -> new MLPlayer(models, ONNXConstructionPolicy.TOURNAMENT))
                    .hasRootCauseMessage("native failure");
            verify(first).close();
            verify(second).close();
        }
    }

    @Test
    void closingPlayerAttemptsEverySessionEvenIfAnEarlierCloseFails() throws Exception {
        var models = models();
        var env = mock(OrtEnvironment.class);
        var first = mock(OrtSession.class);
        var second = mock(OrtSession.class);
        var third = mock(OrtSession.class);
        try (var environment = mockStatic(OrtEnvironment.class)) {
            environment.when(OrtEnvironment::getEnvironment).thenReturn(env);
            when(env.createSession(anyString(), any(OrtSession.SessionOptions.class)))
                    .thenReturn(first, second, third);
            var player = new MLPlayer(models, ONNXConstructionPolicy.TOURNAMENT);
            doThrow(new OrtException("close failed")).when(first).close();
            assertThatThrownBy(player::close).hasRootCauseMessage("close failed");
            verify(second).close();
            verify(third).close();
        }
    }

    @Test
    void proCannotSilentlyOmitFourthSessionInTournamentPolicy() throws Exception {
        var models = models();
        var env = mock(OrtEnvironment.class);
        var first = mock(OrtSession.class);
        var second = mock(OrtSession.class);
        var third = mock(OrtSession.class);
        try (var environment = mockStatic(OrtEnvironment.class)) {
            environment.when(OrtEnvironment::getEnvironment).thenReturn(env);
            when(env.createSession(anyString(), any(OrtSession.SessionOptions.class)))
                    .thenReturn(first, second, third)
                    .thenThrow(new OrtException("optional model rejected"));
            assertThatThrownBy(() -> new MLPlayerPro(models, ONNXConstructionPolicy.TOURNAMENT))
                    .hasRootCauseMessage("optional model rejected");
            verify(first).close();
            verify(second).close();
            verify(third).close();
        }
    }

    @Test
    void fallbackDiagnosticsStayWithTheirGameWhenPlayerIsReused() throws Exception {
        models();
        var env = spy(OrtEnvironment.getEnvironment());
        UUID id = UUID.fromString("00000000-0000-4000-8000-000000000001");
        var requested = new TournamentCompetitor(id, "ML", PlayerImplementation.ML, nullModel());
        var artifacts =
                ModelManager.REQUIRED_MODEL_FILES.stream()
                        .map(
                                name ->
                                        new TournamentArtifact(
                                                name,
                                                0,
                                                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"))
                        .toList();
        var entry =
                new ResolvedTournamentCompetitor(
                        requested,
                        PlayerImplementation.ML.className(),
                        "fixture",
                        TournamentIdentities.artifactFingerprint(artifacts),
                        directory,
                        artifacts);
        {
            doAnswer(
                            invocation -> {
                                var session = mock(OrtSession.class);
                                when(session.run(anyMap()))
                                        .thenThrow(new OrtException("inference unavailable"));
                                return session;
                            })
                    .when(env)
                    .createSession(anyString(), any(OrtSession.SessionOptions.class));
            try (var pool = nativePool(List.of(entry), 1, env)) {
                var seats =
                        org.jskat.util.Player.getOrderedList().stream()
                                .map(seat -> new TournamentSeat(seat, id, 42))
                                .toList();
                var firstGame =
                        new ScheduledTournamentGame(
                                "first",
                                0,
                                "block",
                                seats,
                                Arrays.asList(org.jskat.util.Card.values()),
                                1);
                var first = pool.acquire(firstGame, CancellationToken.NONE);
                try {
                    first.players()
                            .getFirst()
                            .takeCards(
                                    new org.jskat.util.CardList(
                                            Arrays.asList(org.jskat.util.Card.values())
                                                    .subList(0, 10)));
                    first.players().getFirst().bidMore(18);
                } finally {
                    first.close();
                }
                var secondGame =
                        new ScheduledTournamentGame(
                                "second",
                                0,
                                "block",
                                seats,
                                Arrays.asList(org.jskat.util.Card.values()),
                                1);
                try (var second = pool.acquire(secondGame, CancellationToken.NONE)) {
                    second.players()
                            .getFirst()
                            .takeCards(
                                    new org.jskat.util.CardList(
                                            Arrays.asList(org.jskat.util.Card.values())
                                                    .subList(0, 10)));
                    second.players().getFirst().bidMore(18);
                    assertThat(second.fallbacks())
                            .containsExactly(
                                    new TournamentInferenceFallback(
                                            id,
                                            "second",
                                            "BIDDING",
                                            "ai.onnxruntime.OrtException: inference unavailable"));
                }
                assertThat(first.fallbacks())
                        .containsExactly(
                                new TournamentInferenceFallback(
                                        id,
                                        "first",
                                        "BIDDING",
                                        "ai.onnxruntime.OrtException: inference unavailable"));
            }
        }
    }

    @Test
    void repeatedNativePlayerEvictionNeverExceedsTwelveSessionsPerWorkerAndClosesEverything() throws Exception {
        models();
        var env = mock(OrtEnvironment.class);
        var live = new java.util.concurrent.atomic.AtomicInteger();
        var peak = new java.util.concurrent.atomic.AtomicInteger();
        var created = new java.util.concurrent.atomic.AtomicInteger();
        var entries = new ArrayList<ResolvedTournamentCompetitor>();
        var artifacts = ModelManager.REQUIRED_MODEL_FILES.stream().map(name -> new TournamentArtifact(name, 0,
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")).toList();
        for (var implementation : List.of(PlayerImplementation.ML_PRO, PlayerImplementation.ML)) {
            int i = entries.size() + 1;
            entries.add(new ResolvedTournamentCompetitor(
                    new TournamentCompetitor(new UUID(0x4000, Long.MIN_VALUE + i), implementation.id(), implementation, nullModel()),
                    implementation.className(), "fixture", TournamentIdentities.artifactFingerprint(artifacts), directory, artifacts));
        }
        {
            when(env.createSession(anyString(), any(OrtSession.SessionOptions.class))).thenAnswer(invocation -> {
                var session = mock(OrtSession.class);
                created.incrementAndGet();
                peak.accumulateAndGet(live.incrementAndGet(), Math::max);
                doAnswer(close -> { assertThat(live.decrementAndGet()).isNotNegative(); return null; }).when(session).close();
                return session;
            });
            try (var pool = nativePool(entries, 3, env)) {
                for (int round = 0; round < 12; round++) {
                    var leases = new ArrayList<TournamentPlayerPool.GameLease>();
                    try {
                        for (int worker = 0; worker < 3; worker++) {
                            var id = entries.get(round % entries.size()).requested().id();
                            var game = new ScheduledTournamentGame("game-" + round + "-" + worker, 0, "block",
                                    org.jskat.util.Player.getOrderedList().stream().map(seat -> new TournamentSeat(seat, id, 42)).toList(),
                                    Arrays.asList(org.jskat.util.Card.values()), 1);
                            leases.add(pool.acquire(game, CancellationToken.NONE));
                            assertThat(live.get()).isLessThanOrEqualTo(36);
                        }
                        assertThat(live.get()).isEqualTo(round % 2 == 0 ? 36 : 27);
                    } finally { for (var lease : leases) lease.close(); }
                }
            }
            assertThat(peak.get()).isEqualTo(36);
            assertThat(created.get()).isGreaterThan(36);
            assertThat(live.get()).isZero();
        }
    }

    private TournamentPlayerPool nativePool(List<ResolvedTournamentCompetitor> entries, int workers, OrtEnvironment env) {
        var registry = new TournamentPlayerRegistry();
        return new TournamentPlayerPool(entries, workers, competitor -> {
            // Mockito static mocks are thread-local; install the fixture on each loader thread.
            try (var environment = mockStatic(OrtEnvironment.class)) {
                environment.when(OrtEnvironment::getEnvironment).thenReturn(env);
                return registry.create(competitor);
            }
        }, registry::close);
    }

    private ModelSource nullModel() {
        return new ModelSource.Development(new ConfiguredPath("fixture", directory));
    }
}
