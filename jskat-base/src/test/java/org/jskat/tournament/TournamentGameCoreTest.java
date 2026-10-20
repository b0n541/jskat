package org.jskat.tournament;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.player.JSkatPlayer;
import org.jskat.data.*;
import java.lang.reflect.*;
import org.jskat.util.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class TournamentGameCoreTest {
    static ScheduledTournamentGame schedule() {
        return new ScheduledTournamentGame("game-0", 0, "block-0",
                Player.getOrderedList().stream().map(p -> new TournamentSeat(p, UUID.randomUUID(), 17)).toList(),
                List.of(Card.values()), 0);
    }
    static class Passing extends AIPlayerRND {
        @Override public int bidMore(int next) { return 0; }
    }
    static List<JSkatPlayer> passing() {
        List<JSkatPlayer> players = List.of(new Passing(), new Passing(), new Passing());
        for (Player p : Player.getOrderedList()) players.get(p.getOrder()).newGame(p);
        return players;
    }
    @Test void allPassCompletesWithoutTableOrViewAndPreservesRetainedDeck() {
        var scheduled = schedule();
        var result = new TournamentGameCore().play(scheduled, passing(), "0".repeat(64), UUID.randomUUID(), UUID.randomUUID(), List.of());
        assertThat(result.outcome().allPassed()).isTrue();
        assertThat(result.seatScores().values()).containsOnly(0L);
        assertThat(scheduled.deck()).containsExactly(Card.values());
        assertThat(result.events().getLast().type()).isEqualTo("complete");
    }

    @Test void durableVerifierRejectsMissingPassAndForgedScores() {
        var scheduled = schedule();
        var core = new TournamentGameCore();
        var result = core.play(scheduled, passing(), "0".repeat(64), UUID.randomUUID(), UUID.randomUUID(), List.of());
        core.verifyResult(scheduled, result);
        var forged = new TournamentGameResult(result.gameId(), result.attemptId(), result.sessionId(),
                result.inputDigest(), result.outcome(), Map.of(Player.FOREHAND, 50L, Player.MIDDLEHAND, 0L,
                Player.REARHAND, 0L), result.events(), List.of());
        assertThatThrownBy(() -> core.verifyResult(scheduled, forged)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void finalizeFailureIsUnscoredAndPreservesCauseBeforeCompletion() {
        RuntimeException original = new IllegalStateException("finalizer exploded");
        JSkatPlayer broken = new Passing() { @Override public void finalizeGame() { throw original; } };
        broken.newGame(Player.FOREHAND);
        var players = new ArrayList<>(passing()); players.set(0, broken);
        try {
            new TournamentGameCore().play(schedule(), players, "0".repeat(64), UUID.randomUUID(), UUID.randomUUID(), List.of());
            fail("Finalization must succeed before a result exists");
        } catch (TournamentGameCore.GameFailure failure) {
            assertThat(failure.getCause()).isSameAs(original);
            assertThat(failure.attempt().error()).contains("FOREHAND.finalizeGame");
            assertThat(failure.attempt().events()).noneMatch(e -> Set.of("score", "complete").contains(e.type()));
        }
    }

    static class Ordered extends Passing {
        final GameType gameType;
        final Deque<Card> preferred = new ArrayDeque<>(List.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ,
                Card.CA, Card.CT, Card.C9, Card.SA, Card.ST, Card.S9));
        Ordered(GameType gameType) { this.gameType = gameType; }
        @Override public int bidMore(int next) { return knowledge.getPlayerPosition() == Player.FOREHAND ? 18 : 0; }
        @Override public boolean pickUpSkat() { return false; }
        @Override public GameContract announceGame() { return new GameContract(gameType, true); }
        @Override public Card playCard() {
            CardList legal = getPlayableCards(knowledge.getTrickCards());
            if (knowledge.getPlayerPosition() == Player.FOREHAND && !preferred.isEmpty()) return preferred.remove();
            return legal.get(0);
        }
    }
    static ScheduledTournamentGame perfect() {
        var base = schedule();
        List<Card> deck = new ArrayList<>(); CardDeck.getPerfectDistribution().forEach(deck::add);
        return new ScheduledTournamentGame(base.gameId(), 0, base.blockId(), base.seats(), deck, 0);
    }
    static List<JSkatPlayer> ordered(GameType type) {
        List<JSkatPlayer> players = List.of(new Ordered(type), new Ordered(type), new Ordered(type));
        for (Player p : Player.getOrderedList()) players.get(p.getOrder()).newGame(p);
        return players;
    }
    @Test void grandHandSchwarzScoresExactlyAndSummaryMutationCannotChangeFrozenOutcome() {
        var players = new ArrayList<>(ordered(GameType.GRAND));
        JSkatPlayer delegate = players.get(0);
        players.set(0, (JSkatPlayer) Proxy.newProxyInstance(JSkatPlayer.class.getClassLoader(),
                new Class<?>[]{JSkatPlayer.class}, (proxy, method, args) -> {
                    if (method.getName().equals("setGameSummary")) {
                        GameSummary summary = (GameSummary) args[0];
                        summary.gameResult.setGameValue(0);
                        summary.tricks.getFirst().setTrickWinner(Player.REARHAND);
                    }
                    try { return method.invoke(delegate, args); }
                    catch (InvocationTargetException ex) { throw ex.getCause(); }
                }));
        var core = new TournamentGameCore();
        var result = core.play(perfect(), players, "0".repeat(64), UUID.randomUUID(), UUID.randomUUID(), List.of());
        assertThat(result.outcome().signedGameValue()).isEqualTo(192);
        assertThat(result.seatScores()).containsEntry(Player.FOREHAND, 242L)
                .containsEntry(Player.MIDDLEHAND, 0L).containsEntry(Player.REARHAND, 0L);
        assertThat(result.outcome().tricks()).containsEntry(Player.FOREHAND, 10);
        assertThat(result.events().stream().filter(e -> e.type().equals("trick"))).hasSize(10);
        core.verifyResult(perfect(), result);
    }

    @Test void nullHandLosesAfterFirstDeclarerTrickWithExactDefenderScores() {
        var players = ordered(GameType.NULL);
        Ordered forehand = (Ordered) players.getFirst();
        forehand.preferred.clear(); forehand.preferred.add(Card.CA);
        var core = new TournamentGameCore();
        var result = core.play(perfect(), players, "0".repeat(64), UUID.randomUUID(), UUID.randomUUID(), List.of());
        assertThat(result.outcome().declarerWon()).isFalse();
        assertThat(result.outcome().signedGameValue()).isEqualTo(-70);
        assertThat(result.seatScores()).containsEntry(Player.FOREHAND, -120L)
                .containsEntry(Player.MIDDLEHAND, 40L).containsEntry(Player.REARHAND, 40L);
        assertThat(result.events().stream().filter(e -> e.type().equals("TrickCardPlayedEvent"))).hasSize(3);
        core.verifyResult(perfect(), result);
    }
    @Test void invalidActionsFailBeforeIllegalMoveOrScoreIsRecorded() {
        for (String action : List.of("bidMore", "discardSkat", "announceGame", "playCard")) {
            var players = new ArrayList<>(ordered(GameType.GRAND));
            JSkatPlayer delegate = players.get(0);
            players.set(0, (JSkatPlayer) Proxy.newProxyInstance(JSkatPlayer.class.getClassLoader(),
                    new Class<?>[]{JSkatPlayer.class}, (proxy, method, args) -> {
                        if (method.getName().equals(action)) return switch (action) {
                            case "bidMore" -> 17;
                            case "discardSkat" -> new CardList(Card.DA, Card.DA);
                            case "announceGame" -> new GameContract(GameType.GRAND, false);
                            default -> Card.DA;
                        };
                        if (action.equals("discardSkat") && method.getName().equals("pickUpSkat")) return true;
                        try { return method.invoke(delegate, args); }
                        catch (InvocationTargetException ex) { throw ex.getCause(); }
                    }));
            assertThatThrownBy(() -> new TournamentGameCore().play(perfect(), players, "0".repeat(64),
                    UUID.randomUUID(), UUID.randomUUID(), List.of()))
                    .isInstanceOfSatisfying(TournamentGameCore.GameFailure.class, failure ->
                            assertThat(failure.attempt().events()).noneMatch(e -> Set.of("score", "complete").contains(e.type())));
        }
    }
    @Test void auditRejectsDeletionReorderingAndWrongRetainedDeck() {
        var core = new TournamentGameCore();
        var schedule = perfect();
        var result = core.play(schedule, ordered(GameType.GRAND), "0".repeat(64), UUID.randomUUID(), UUID.randomUUID(), List.of());
        var removed = new ArrayList<>(result.events());
        removed.removeIf(e -> e.type().equals("trick"));
        List<TournamentGameEvent> renumbered = new ArrayList<>();
        for (var e : removed) renumbered.add(new TournamentGameEvent(renumbered.size(), e.type(), e.payloadJson()));
        var forged = new TournamentGameResult(result.gameId(), result.attemptId(), result.sessionId(), result.inputDigest(),
                result.outcome(), result.seatScores(), renumbered, List.of());
        assertThatThrownBy(() -> core.verifyResult(schedule, forged)).isInstanceOf(IllegalArgumentException.class);
        var changedDeck = new ArrayList<>(schedule.deck()); Collections.swap(changedDeck, 0, 3);
        var changed = new ScheduledTournamentGame(schedule.gameId(), 0, schedule.blockId(), schedule.seats(), changedDeck, 0);
        assertThatThrownBy(() -> core.verifyResult(changed, result)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void playerCannotMutateAuthoritativeDealThroughItsCallbackArgument() {
        var players = new ArrayList<>(ordered(GameType.GRAND));
        JSkatPlayer delegate = players.get(0);
        players.set(0, (JSkatPlayer) Proxy.newProxyInstance(JSkatPlayer.class.getClassLoader(),
                new Class<?>[]{JSkatPlayer.class}, (proxy, method, args) -> {
                    Object value;
                    try { value = method.invoke(delegate, args); }
                    catch (InvocationTargetException ex) { throw ex.getCause(); }
                    if (method.getName().equals("takeCards")) ((CardList) args[0]).clear();
                    return value;
                }));
        var result = new TournamentGameCore().play(perfect(), players, "0".repeat(64), UUID.randomUUID(), UUID.randomUUID(), List.of());
        assertThat(result.outcome().signedGameValue()).isEqualTo(192);
    }
}
