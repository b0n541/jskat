package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.jskat.util.*;
import org.junit.jupiter.api.Test;

import java.util.*;

class TournamentPlayerPoolTest {
    static final UUID ID = UUID.fromString("00000000-0000-4000-8000-000000000001");

    ScheduledTournamentGame game(String id) {
        return new ScheduledTournamentGame(
                id,
                0,
                "block",
                Player.getOrderedList().stream().map(p -> new TournamentSeat(p, ID, 42)).toList(),
                Arrays.asList(Card.values()),
                17);
    }

    @Test
    void leasesThreeIndependentSeatsAndResetsReusedRandomStream() {
        var c = new TournamentCompetitor(ID, "Random", PlayerImplementation.RANDOM, null);
        var entry =
                new ResolvedTournamentCompetitor(
                        c, PlayerImplementation.RANDOM.className(), null, null, null, List.of());
        try (var pool = new TournamentPlayerPool(List.of(entry), 1)) {
            List<Integer> decisions;
            try (var lease = pool.acquire(game("first"), CancellationToken.NONE)) {
                assertThat(new HashSet<>(lease.players())).hasSize(3);
                decisions = lease.players().stream().map(p -> p.bidMore(18)).toList();
            }
            try (var lease = pool.acquire(game("second"), CancellationToken.NONE)) {
                assertThat(lease.players().stream().map(p -> p.bidMore(18)).toList())
                        .isEqualTo(decisions);
            }
            assertThat(pool.instanceCount()).isEqualTo(3);
        }
    }

    @Test
    void registeredModelFreeDeclarationsHonorTheirSkatDecision() {
        var players =
                List.of(
                        new org.jskat.ai.rnd.AIPlayerRND(),
                        new org.jskat.ai.algorithmic.AlgorithmicAIPlayer());
        ((org.jskat.ai.rnd.AIPlayerRND) players.getFirst()).reseed(42);
        for (var player : players)
            for (int game = 0; game < 20; game++) {
                player.newGame(Player.FOREHAND);
                player.takeCards(new CardList(Arrays.asList(Card.values()).subList(0, 10)));
                boolean pickup = player.pickUpSkat();
                assertThat(player.announceGame().hand())
                        .as(player.getClass().getSimpleName() + " pickup=" + pickup)
                        .isEqualTo(!pickup);
            }
    }

    @Test
    void seededDecisionsUseCanonicalCardOrder() {
        var player = new org.jskat.ai.rnd.AIPlayerRND();
        player.newGame(Player.FOREHAND);
        player.reseed(42);
        player.takeCards(
                new CardList(
                        List.of(
                                Card.ST, Card.SA, Card.C7, Card.C8, Card.C9, Card.CJ, Card.CQ,
                                Card.CK, Card.CT, Card.CA)));
        assertThat(player.getCardsToDiscard()).containsExactly(Card.CA, Card.CJ);
    }

    @Test
    void concurrentGamesStayWithinCapacityAndCancelledWaitersDoNotAcquire() throws Exception {
        var c = new TournamentCompetitor(ID, "Random", PlayerImplementation.RANDOM, null);
        var entry =
                new ResolvedTournamentCompetitor(
                        c, PlayerImplementation.RANDOM.className(), null, null, null, List.of());
        try (var pool = new TournamentPlayerPool(List.of(entry), 2);
                var executor = java.util.concurrent.Executors.newSingleThreadExecutor()) {
            try (var first = pool.acquire(game("first"), CancellationToken.NONE);
                    var second = pool.acquire(game("second"), CancellationToken.NONE)) {
                assertThat(first.players()).doesNotContainAnyElementsOf(second.players());
                var cancellation = new java.util.concurrent.atomic.AtomicBoolean();
                var waiting =
                        executor.submit(() -> pool.acquire(game("waiting"), cancellation::get));
                assertThat(pool.instanceCount()).isEqualTo(6);
                cancellation.set(true);
                assertThatThrownBy(() -> waiting.get(2, java.util.concurrent.TimeUnit.SECONDS))
                        .hasCauseInstanceOf(java.util.concurrent.CancellationException.class);
                first.discard();
                assertThat(pool.instanceCount()).isEqualTo(3);
            }
            try (var replacement = pool.acquire(game("replacement"), CancellationToken.NONE)) {
                assertThat(pool.instanceCount()).isLessThanOrEqualTo(6);
                assertThat(replacement.players()).hasSize(3);
            }
        }
    }
}
