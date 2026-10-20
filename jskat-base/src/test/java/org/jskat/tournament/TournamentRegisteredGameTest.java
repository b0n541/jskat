package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.jskat.util.*;
import org.junit.jupiter.api.Test;

import java.util.*;

class TournamentRegisteredGameTest {
    @Test
    void registeredModelFreeLeasesPlayLegalRepeatableRetainedDeals() {
        UUID random = UUID.fromString("00000000-0000-4000-8000-000000000001"),
                algorithmic = UUID.fromString("00000000-0000-4000-8000-000000000002");
        var roster =
                List.of(
                                new TournamentCompetitor(
                                        random, "Random", PlayerImplementation.RANDOM, null),
                                new TournamentCompetitor(
                                        algorithmic,
                                        "Algorithmic",
                                        PlayerImplementation.ALGORITHMIC,
                                        null))
                        .stream()
                        .map(
                                c ->
                                        new ResolvedTournamentCompetitor(
                                                c,
                                                c.implementation().className(),
                                                null,
                                                null,
                                                null,
                                                List.of()))
                        .toList();
        var core = new TournamentGameCore();
        try (var pool = new TournamentPlayerPool(roster, 2)) {
            for (int deal = 0; deal < 24; deal++) {
                var deck = new ArrayList<>(Arrays.asList(Card.values()));
                Collections.shuffle(deck, new Random(deal));
                var seats =
                        List.of(
                                new TournamentSeat(Player.FOREHAND, random, deal * 3L),
                                new TournamentSeat(Player.MIDDLEHAND, algorithmic, deal * 3L + 1),
                                new TournamentSeat(
                                        Player.REARHAND,
                                        deal % 2 == 0 ? random : algorithmic,
                                        deal * 3L + 2));
                var scheduled =
                        new ScheduledTournamentGame(
                                "game-" + deal, deal, "block-" + deal, seats, deck, deal);
                TournamentGameResult first;
                try (var lease = pool.acquire(scheduled, CancellationToken.NONE)) {
                    first =
                            core.play(
                                    scheduled,
                                    lease.players(),
                                    "0".repeat(64),
                                    UUID.randomUUID(),
                                    UUID.randomUUID(),
                                    lease::fallbacks);
                    core.verifyResult(scheduled, first);
                }
                try (var lease = pool.acquire(scheduled, CancellationToken.NONE)) {
                    var repeated =
                            core.play(
                                    scheduled,
                                    lease.players(),
                                    "0".repeat(64),
                                    UUID.randomUUID(),
                                    UUID.randomUUID(),
                                    lease::fallbacks);
                    assertThat(repeated.outcome()).isEqualTo(first.outcome());
                    assertThat(repeated.seatScores()).isEqualTo(first.seatScores());
                    assertThat(repeated.events()).isEqualTo(first.events());
                }
            }
        }
    }

    @Test
    void fallbacksAreObservedAfterCallbacksBeforeResultFreeze() {
        var diagnostics = new ArrayList<TournamentInferenceFallback>();
        UUID competitor = UUID.fromString("00000000-0000-4000-8000-000000000001");
        var expected =
                new TournamentInferenceFallback(
                        competitor, "supplier-game", "TRICK_PLAYING", "inference failed");
        class Passing extends org.jskat.ai.rnd.AIPlayerRND {
            @Override
            public int bidMore(int next) {
                return 0;
            }
        }
        List<org.jskat.player.JSkatPlayer> players =
                List.of(
                        new Passing() {
                            @Override
                            public void finalizeGame() {
                                diagnostics.add(expected);
                            }
                        },
                        new Passing(),
                        new Passing());
        for (var seat : Player.getOrderedList()) players.get(seat.getOrder()).newGame(seat);
        var scheduled =
                new ScheduledTournamentGame(
                        "supplier-game",
                        0,
                        "block",
                        Player.getOrderedList().stream()
                                .map(seat -> new TournamentSeat(seat, competitor, 1))
                                .toList(),
                        Arrays.asList(Card.values()),
                        1);
        var result =
                new TournamentGameCore()
                        .play(
                                scheduled,
                                players,
                                "0".repeat(64),
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                () -> List.copyOf(diagnostics));
        assertThat(result.fallbacks()).containsExactly(expected);
    }
}
