package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.jskat.util.Player;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.*;

class TournamentReportsTest {
    final UUID a = UUID.fromString("11111111-1111-4111-8111-111111111111"),
            b = UUID.fromString("ffffffff-ffff-4fff-8fff-ffffffffffff");

    ResolvedTournamentConfiguration config() {
        var cs =
                List.of(
                        new TournamentCompetitor(
                                a, "A,\"quoted\"", PlayerImplementation.RANDOM, null),
                        new TournamentCompetitor(b, "B", PlayerImplementation.ALGORITHMIC, null));
        var path = Path.of("/tmp/ratings-run");
        var requested =
                new TournamentConfiguration(
                        1,
                        "standard-skat-v1",
                        Optional.of(7L),
                        2,
                        new ConfiguredPath(path.toString(), path),
                        cs,
                        path.resolveSibling("config.json"),
                        "{}");
        return new ResolvedTournamentConfiguration(
                requested,
                7,
                new TournamentBuildIdentity("a".repeat(40), Optional.empty()),
                cs.stream()
                        .map(
                                c ->
                                        new ResolvedTournamentCompetitor(
                                                c,
                                                c.implementation().className(),
                                                null,
                                                null,
                                                null,
                                                List.of()))
                        .toList());
    }

    List<ScheduledTournamentGame> schedule() {
        return TournamentSchedules.generate(List.of(a, b), 2, 7);
    }

    TournamentGameResult allPass(ScheduledTournamentGame g) {
        return new TournamentGameResult(
                g.gameId(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                TournamentIdentities.resolvedDigest(config()),
                new TournamentGameResult.Outcome(true, null, false, 0, null, Map.of(), Map.of()),
                Map.of(Player.FOREHAND, 0L, Player.MIDDLEHAND, 0L, Player.REARHAND, 0L),
                List.of(),
                List.of());
    }

    TournamentReports.Report report(List<TournamentGameResult> games, List<Long> epochs) {
        return TournamentReports.calculate(
                config(),
                schedule(),
                new DurableTournamentRunStore.CommittedSet(
                        UUID.fromString("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa"),
                        "a".repeat(64),
                        games,
                        Map.of(),
                        epochs),
                "{}",
                CanonicalJson.object());
    }

    @Test
    void partialEpochIsProgressOnlyAndNoEvidenceDoesNotInventRating() {
        var r = report(List.of(allPass(schedule().getFirst())), List.of());
        var s = CanonicalJson.parse(r.summaryJson());
        assertThat(s.path("provisional").asBoolean()).isTrue();
        assertThat(s.path("hasRatedEvidence").asBoolean()).isFalse();
        assertThat(s.path("leaderboard").get(0).path("rating").isNull()).isTrue();
        assertThat(s.path("partialEpochGames").asInt()).isEqualTo(1);
        assertThat(r.leaderboardCsv()).contains("\"A,\"\"quoted\"\"\"");
    }

    @Test
    void allPassCompleteEpochsHaveExactTiedIntervalsAndHeldOutLogLoss() {
        var games = schedule().stream().map(this::allPass).toList();
        var report = report(games, List.of(0L, 1L));
        var s = CanonicalJson.parse(report.summaryJson());
        assertThat(s.path("provisional").asBoolean()).isFalse();
        assertThat(s.path("observations").size()).isEqualTo(2);
        assertThat(s.path("leaderboard").get(0).path("interval").toString())
                .isEqualTo("[1500,1500]");
        assertThat(s.path("predictive").path("logLoss").asDouble())
                .isCloseTo(0.6931471805599453, within(1e-14));
        assertThat(s.path("descriptive").get(0).path("seatAppearances").asInt()).isEqualTo(18);
        var reversed = new ArrayList<>(games);
        Collections.reverse(reversed);
        assertThat(report(reversed, List.of(0L, 1L))).isEqualTo(report);
    }

    @Test
    void duplicatedSeatScoresSumEveryInstanceBeforeBlockOrdering() {
        var games = new ArrayList<TournamentGameResult>();
        for (var g : schedule().subList(0, 6)) {
            Player declarer =
                    g.seats().stream()
                            .filter(seat -> seat.competitorId().equals(a))
                            .findFirst()
                            .orElseThrow()
                            .seat();
            var scores = new EnumMap<Player, Long>(Player.class);
            for (var seat : g.seats()) scores.put(seat.seat(), seat.seat() == declarer ? 68L : 0L);
            var outcome =
                    new TournamentGameResult.Outcome(
                            false,
                            declarer,
                            true,
                            18,
                            new TournamentContract(
                                    org.jskat.util.GameType.DIAMONDS, false, false, false, false),
                            Map.of(Player.FOREHAND, 60, Player.MIDDLEHAND, 40, Player.REARHAND, 20),
                            Map.of(Player.FOREHAND, 5, Player.MIDDLEHAND, 3, Player.REARHAND, 2));
            games.add(
                    new TournamentGameResult(
                            g.gameId(),
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            TournamentIdentities.resolvedDigest(config()),
                            outcome,
                            scores,
                            List.of(),
                            List.of()));
        }
        var s = CanonicalJson.parse(report(games, List.of(0L)).summaryJson());
        assertThat(s.path("blockTotals").get(0).path("totals").path(a.toString()).asInt())
                .isEqualTo(408);
        assertThat(s.path("leaderboard").get(0).path("rating").asDouble())
                .isCloseTo(1645.410817318144, within(1e-6));
        assertThat(s.path("predictive").path("available").asBoolean()).isFalse();
        assertThat(s.path("descriptive").get(0).path("declarerWins").asInt()).isEqualTo(6);
    }

    @Test
    void durableGenerationPublishesAndReadsOneFixedObservation() throws Exception {
        var requested = TournamentStoreDurabilityTest.requested();
        try (var store =
                DurableTournamentRunStore.create(
                        requested,
                        7,
                        TournamentStoreDurabilityTest.BUILD,
                        TournamentStoreDurabilityTest.RESOLVER,
                        CancellationToken.NONE)) {
            var session = UUID.randomUUID();
            store.startSession(session, CanonicalJson.object());
            try (var pool = new TournamentPlayerPool(store.configuration().competitors(), 1)) {
                var core = new TournamentGameCore();
                for (var game : store.schedule())
                    try (var lease = pool.acquire(game, CancellationToken.NONE)) {
                        var result =
                                core.play(
                                        game,
                                        lease.players(),
                                        TournamentIdentities.resolvedDigest(store.configuration()),
                                        UUID.randomUUID(),
                                        session,
                                        lease::fallbacks);
                        store.commit(result);
                    }
            }
            var metadata =
                    CanonicalJson.object()
                            .put("outcome", "COMPLETED")
                            .put("sessionId", session.toString());
            var generation = TournamentReports.publish(store, metadata);
            var report = TournamentReports.read(store);
            assertThat(report.exports().keySet())
                    .containsExactlyInAnyOrder(
                            "summary.json", "leaderboard.csv", "game-scores.csv");
            var summary = CanonicalJson.parse(report.summaryJson());
            assertThat(summary.path("includedEpochs").size()).isEqualTo(2);
            assertThat(summary.path("history").path("sessions").size()).isEqualTo(1);
            assertThat(summary.path("provisional").asBoolean()).isFalse();
            assertThat(generation.exports().get("summary.json"))
                    .isEqualTo(report.exports().get("summary.json"));
        }
    }

    @Test
    void heldOutOpposingEpochsUseTrainingEpochOnlyAndCanonicalCalibrationBins() {
        var games = new ArrayList<TournamentGameResult>();
        for (var g : schedule()) {
            UUID winner = g.epoch() == 0 ? a : b;
            Player declarer =
                    g.seats().stream()
                            .filter(seat -> seat.competitorId().equals(winner))
                            .findFirst()
                            .orElseThrow()
                            .seat();
            var scores = new EnumMap<Player, Long>(Player.class);
            for (var seat : g.seats()) scores.put(seat.seat(), seat.seat() == declarer ? 68L : 0L);
            games.add(
                    new TournamentGameResult(
                            g.gameId(),
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            TournamentIdentities.resolvedDigest(config()),
                            new TournamentGameResult.Outcome(
                                    false,
                                    declarer,
                                    true,
                                    18,
                                    new TournamentContract(
                                            org.jskat.util.GameType.DIAMONDS,
                                            false,
                                            false,
                                            false,
                                            false),
                                    Map.of(
                                            Player.FOREHAND,
                                            60,
                                            Player.MIDDLEHAND,
                                            40,
                                            Player.REARHAND,
                                            20),
                                    Map.of(
                                            Player.FOREHAND,
                                            5,
                                            Player.MIDDLEHAND,
                                            3,
                                            Player.REARHAND,
                                            2)),
                            scores,
                            List.of(),
                            List.of()));
        }
        var s = CanonicalJson.parse(report(games, List.of(0L, 1L)).summaryJson());
        assertThat(s.path("predictive").path("logLoss").asDouble())
                .isCloseTo(1.8459340330627338, within(1e-9));
        var bins = s.path("predictive").path("calibration");
        assertThat(bins.get(1).path("meanPredicted").asDouble())
                .isCloseTo(.15787778892577956, within(1e-9));
        assertThat(bins.get(1).path("meanObserved").asDouble()).isEqualTo(1);
        assertThat(bins.get(8).path("meanObserved").asDouble()).isZero();
        assertThat(bins.get(0).path("meanObserved").isNull()).isTrue();
    }
}
