package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.jskat.util.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

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
    void modelConstructionDoesNotBlockReturningAnotherGame() throws Exception {
        var constructing = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var slow = new AtomicBoolean();
        try (var registries = mockConstruction(TournamentPlayerRegistry.class, (registry, context) -> {
            when(registry.create(any())).thenAnswer(call -> {
                if (slow.get()) {
                    constructing.countDown();
                    assertThat(release.await(10, TimeUnit.SECONDS)).isTrue();
                }
                return new org.jskat.ai.rnd.AIPlayerRND();
            });
        }); var executor = Executors.newFixedThreadPool(2)) {
            var c = new TournamentCompetitor(ID, "Random", PlayerImplementation.RANDOM, null);
            var entry = new ResolvedTournamentCompetitor(c, c.implementation().className(), null, null, null, List.of());
            try (var pool = new TournamentPlayerPool(List.of(entry), 2)) {
                var first = pool.acquire(game("first"), CancellationToken.NONE);
                slow.set(true);
                var second = executor.submit(() -> pool.acquire(game("second"), CancellationToken.NONE));
                try {
                    assertThat(constructing.await(5, TimeUnit.SECONDS)).isTrue();
                    executor.submit(first::close).get(2, TimeUnit.SECONDS);
                } finally {
                    release.countDown();
                    first.close();
                    second.get(10, TimeUnit.SECONDS).close();
                }
                assertThat(pool.instanceCount()).isLessThanOrEqualTo(6);
            }
        }
    }

    @Test
    void independentlyReservedModelsLoadTogetherAndAllAreClosed() throws Exception {
        var entered = new CountDownLatch(3);
        var created = new AtomicInteger();
        var closed = new AtomicInteger();
        var c = new TournamentCompetitor(ID, "ML", PlayerImplementation.ML, new ModelSource.Active());
        var artifacts = List.of(new TournamentArtifact("fixture.onnx", 0, "a".repeat(64)));
        var entry = new ResolvedTournamentCompetitor(c, c.implementation().className(), "fixture",
                TournamentIdentities.artifactFingerprint(artifacts), java.nio.file.Path.of("/tmp/fixture"), artifacts);
        try (var pool = new TournamentPlayerPool(List.of(entry), 1, competitor -> {
            entered.countDown();
            try { assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue(); }
            catch (InterruptedException e) { throw new AssertionError(e); }
            created.incrementAndGet();
            return new org.jskat.ai.rnd.AIPlayerRND();
        }, player -> closed.incrementAndGet())) {
            try (var lease = pool.acquire(game("parallel-models"), CancellationToken.NONE)) {
                assertThat(pool.instanceCount()).isEqualTo(3);
                assertThat(lease.players()).hasSize(3);
            }
        }
        assertThat(created).hasValue(3);
        assertThat(closed).hasValue(3);
    }

    @Test
    void failedParallelConstructionJoinsAndClosesSuccessfulSiblings() {
        var entered = new CountDownLatch(3);
        var sequence = new AtomicInteger();
        var closed = new AtomicInteger();
        var c = new TournamentCompetitor(ID, "ML", PlayerImplementation.ML, new ModelSource.Active());
        var artifacts = List.of(new TournamentArtifact("fixture.onnx", 0, "a".repeat(64)));
        var entry = new ResolvedTournamentCompetitor(c, c.implementation().className(), "fixture",
                TournamentIdentities.artifactFingerprint(artifacts), java.nio.file.Path.of("/tmp/fixture"), artifacts);
        try (var pool = new TournamentPlayerPool(List.of(entry), 1, competitor -> {
            int number = sequence.getAndIncrement();
            entered.countDown();
            try { assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue(); }
            catch (InterruptedException e) { throw new AssertionError(e); }
            if (number == 0) throw new IllegalStateException("model load failed");
            return new org.jskat.ai.rnd.AIPlayerRND();
        }, player -> closed.incrementAndGet())) {
            assertThatThrownBy(() -> pool.acquire(game("failed-models"), CancellationToken.NONE))
                    .hasMessage("model load failed");
            assertThat(sequence).hasValue(3);
            assertThat(closed).hasValue(2);
            assertThat(pool.instanceCount()).isZero();
        }
    }

    @Test
    void reservesEveryReusableSeatBeforeEvictingIdlePlayers() {
        UUID other = UUID.fromString("00000000-0000-4000-8000-000000000002"),
                unused = UUID.fromString("00000000-0000-4000-8000-000000000003");
        var entries = java.util.stream.Stream.of(ID, other, unused).map(id -> {
            var c = new TournamentCompetitor(id, id.toString(), PlayerImplementation.RANDOM, null);
            return new ResolvedTournamentCompetitor(c, c.implementation().className(), null, null, null, List.of());
        }).toList();
        try (var registries = mockConstruction(TournamentPlayerRegistry.class, (registry, context) ->
                when(registry.create(any())).thenAnswer(call -> new org.jskat.ai.rnd.AIPlayerRND()));
                var pool = new TournamentPlayerPool(entries, 1)) {
            var first = new ScheduledTournamentGame("first", 0, "block",
                    List.of(new TournamentSeat(Player.FOREHAND, ID, 1),
                            new TournamentSeat(Player.MIDDLEHAND, unused, 2),
                            new TournamentSeat(Player.REARHAND, ID, 3)), Arrays.asList(Card.values()), 1);
            var second = new ScheduledTournamentGame("second", 0, "block",
                    List.of(new TournamentSeat(Player.FOREHAND, other, 1),
                            new TournamentSeat(Player.MIDDLEHAND, ID, 2),
                            new TournamentSeat(Player.REARHAND, ID, 3)), Arrays.asList(Card.values()), 1);
            List<org.jskat.player.JSkatPlayer> reusable;
            try (var lease = pool.acquire(first, CancellationToken.NONE)) {
                reusable = List.of(lease.players().get(0), lease.players().get(2));
            }
            try (var lease = pool.acquire(second, CancellationToken.NONE)) {
                assertThat(lease.players()).containsAll(reusable);
                verify(registries.constructed().getFirst(), times(4)).create(any());
                assertThat(pool.instanceCount()).isEqualTo(3);
            }
        }
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
