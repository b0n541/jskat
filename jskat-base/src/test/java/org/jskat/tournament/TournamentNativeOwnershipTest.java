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
        try (var environment = mockStatic(OrtEnvironment.class)) {
            environment.when(OrtEnvironment::getEnvironment).thenReturn(env);
            doAnswer(
                            invocation -> {
                                var session = mock(OrtSession.class);
                                when(session.run(anyMap()))
                                        .thenThrow(new OrtException("inference unavailable"));
                                return session;
                            })
                    .when(env)
                    .createSession(anyString(), any(OrtSession.SessionOptions.class));
            try (var pool = new TournamentPlayerPool(List.of(entry), 1)) {
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

    private ModelSource nullModel() {
        return new ModelSource.Development(new ConfiguredPath("fixture", directory));
    }
}
