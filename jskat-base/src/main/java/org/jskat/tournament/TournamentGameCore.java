package org.jskat.tournament;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jskat.control.SkatGameEngine;
import org.jskat.control.event.skatgame.*;
import org.jskat.data.*;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.*;

import java.lang.reflect.*;
import java.util.*;

/** One synchronous standard game. Players must be isolated, seeded and reset by their leases. */
public final class TournamentGameCore {
    /** A pre-freeze failure, with the original callback cause and an unscored diagnostic. */
    public static final class GameFailure extends RuntimeException {
        private final TournamentFailedAttempt attempt;
        GameFailure(TournamentFailedAttempt attempt, Throwable cause) {
            super(attempt.error(), cause);
            this.attempt = attempt;
        }
        public TournamentFailedAttempt attempt() { return attempt; }
    }

    public TournamentGameResult play(ScheduledTournamentGame scheduled, List<JSkatPlayer> players,
                                     String inputDigest, UUID attemptId, UUID sessionId,
                                     List<TournamentInferenceFallback> fallbacks) {
        Objects.requireNonNull(scheduled);
        if (players.size() != 3)
            throw new IllegalArgumentException("Three isolated players required");
        Set<JSkatPlayer> identities = Collections.newSetFromMap(new IdentityHashMap<>());
        if (!players.stream().allMatch(p -> p != null && identities.add(p)))
            throw new IllegalArgumentException("Three distinct player identities required");
        Audit audit = new Audit();
        SkatGameEngine[] running = new SkatGameEngine[1];
        String[] phase = {"GAME_START"};
        try {
            List<JSkatPlayer> guarded = new ArrayList<>();
            for (Player seat : Player.getOrderedList()) {
                JSkatPlayer delegate = players.get(seat.getOrder());
                guarded.add((JSkatPlayer) Proxy.newProxyInstance(JSkatPlayer.class.getClassLoader(),
                        new Class<?>[]{JSkatPlayer.class}, (proxy, method, args) -> {
                            try { return method.invoke(delegate, args); }
                            catch (InvocationTargetException e) {
                                throw new PlayerCallbackFailure(seat + "." + method.getName(), e.getCause());
                            }
                        }));
            }
            var engine = new SkatGameEngine(SkatGameEngine.RulesPolicy.standard(), guarded,
                    new SkatGameEngine.Presentation() {}, true, false,
                    move -> {
                        audit.move(move);
                        if (move instanceof TrickCardPlayedEvent && ++audit.cardsPlayed % 3 == 0) {
                            Trick trick = running[0].gameData().getLastCompletedTrick();
                            ObjectNode payload = CanonicalJson.object();
                            payload.put("winner", trick.getTrickWinner().name());
                            payload.put("points", trick.getValue());
                            payload.put("index", audit.cardsPlayed / 3 - 1);
                            audit.add("trick", payload);
                        }
                    }, (type, value) -> {
                        if (type.equals("phase")) phase[0] = value;
                        audit.add(type, CanonicalJson.object().put("value", value));
                    });
            running[0] = engine;
            engine.setCardDeck(CardDeck.fromRetainedOrder(scheduled.deck()));
            engine.run();
            TournamentGameResult.Outcome outcome = outcome(engine.gameData());
            Map<Player,Long> scores = scores(outcome);
            ObjectNode score = CanonicalJson.object();
            for (Player seat : Player.getOrderedList()) score.put(seat.name(), scores.get(seat));
            audit.add("score", score);
            audit.add("complete", CanonicalJson.object().put("ruleset", "standard-skat-v1"));
            return new TournamentGameResult(scheduled.gameId(), attemptId, sessionId, inputDigest,
                    outcome, scores, audit.events, fallbacks);
        } catch (RuntimeException failure) {
            throw new GameFailure(new TournamentFailedAttempt(scheduled.gameId(), attemptId, sessionId,
                    phase[0], failure.toString(), audit.events),
                    failure instanceof PlayerCallbackFailure ? failure.getCause() : failure);
        }
    }

    /**
     * Re-executes retained decisions through the same rules engine. This is a pure boundary:
     * no competitor construction, inference, options, files, buses or graphical state.
     * Every event and observation must match; incomplete, impossible or reordered audits fail.
     */
    public void verifyResult(ScheduledTournamentGame scheduled, TournamentGameResult retained) {
        if (!scheduled.gameId().equals(retained.gameId()))
            throw new IllegalArgumentException("Result does not belong to scheduled game");
        try {
            List<JSkatPlayer> players = new ArrayList<>();
            for (Player seat : Player.getOrderedList()) {
                Deque<TournamentGameEvent> decisions = new ArrayDeque<>();
                for (TournamentGameEvent event : retained.events()) {
                    if (Set.of("BidEvent", "HoldBidEvent", "PassBidEvent", "PickUpSkatEvent",
                            "DiscardSkatEvent", "GameAnnouncementEvent", "TrickCardPlayedEvent").contains(event.type())
                            && seat.name().equals(CanonicalJson.parse(event.payloadJson()).path("seat").asText()))
                        decisions.add(event);
                }
                players.add((JSkatPlayer) Proxy.newProxyInstance(JSkatPlayer.class.getClassLoader(),
                        new Class<?>[]{JSkatPlayer.class}, (proxy, method, args) -> {
                            String name = method.getName();
                            if (name.equals("getPlayerName")) return "audit-" + seat;
                            if (name.equals("isAIPlayer")) return true;
                            if (name.equals("isHumanPlayer")) return false;
                            if (name.equals("pickUpSkat")) return !decisions.isEmpty()
                                    && decisions.peek().type().equals("PickUpSkatEvent");
                            if (name.equals("takeSkat")) { next(decisions, "PickUpSkatEvent"); return null; }
                            if (name.equals("bidMore")) {
                                if (!decisions.isEmpty() && decisions.peek().type().equals("PassBidEvent")) {
                                    next(decisions, "PassBidEvent"); return 0;
                                }
                                return next(decisions, "BidEvent").path("bid").asInt();
                            }
                            if (name.equals("holdBid")) {
                                if (!decisions.isEmpty() && decisions.peek().type().equals("PassBidEvent")) {
                                    next(decisions, "PassBidEvent"); return false;
                                }
                                JsonNode held = next(decisions, "HoldBidEvent");
                                if (held.path("bid").asInt() != (Integer) args[0])
                                    throw new IllegalArgumentException("Held bid differs from offered bid");
                                return true;
                            }
                            if (name.equals("discardSkat")) return readCards(next(decisions, "DiscardSkatEvent").path("cards"));
                            if (name.equals("announceGame")) {
                                JsonNode contract = next(decisions, "GameAnnouncementEvent");
                                return new GameContract(GameType.valueOf(contract.path("gameType").asText()),
                                        contract.path("hand").asBoolean(), contract.path("schneider").asBoolean(),
                                        contract.path("schwarz").asBoolean(), contract.path("ouvert").asBoolean(),
                                        readCards(contract.path("ouvertCards")));
                            }
                            if (name.equals("playCard")) return Card.valueOf(next(decisions, "TrickCardPlayedEvent").path("card").asText());
                            if (name.equals("finalizeGame") && !decisions.isEmpty())
                                throw new IllegalArgumentException("Unused game decisions");
                            return null;
                        }));
            }
            TournamentGameResult replayed = play(scheduled, players, retained.inputDigest(),
                    retained.attemptId(), retained.sessionId(), retained.fallbacks());
            if (!replayed.outcome().equals(retained.outcome())
                    || !replayed.seatScores().equals(retained.seatScores())
                    || !replayed.events().equals(retained.events()))
                throw new IllegalArgumentException("Retained audit or outcome differs from legal execution");
        } catch (RuntimeException failure) {
            throw new IllegalArgumentException("Invalid retained result for " + scheduled.gameId(), failure);
        }
    }

    private static JsonNode next(Deque<TournamentGameEvent> decisions, String type) {
        TournamentGameEvent decision = decisions.poll();
        if (decision == null || !decision.type().equals(type))
            throw new IllegalArgumentException("Missing or out-of-order decision " + type);
        return CanonicalJson.parse(decision.payloadJson());
    }

    private static CardList readCards(JsonNode json) {
        CardList cards = new CardList();
        if (!json.isArray()) throw new IllegalArgumentException("Expected card array");
        for (JsonNode card : json) cards.add(Card.valueOf(card.asText()));
        return cards;
    }

    private static final class PlayerCallbackFailure extends RuntimeException {
        PlayerCallbackFailure(String callback, Throwable cause) { super(callback, cause); }
    }

    static TournamentGameResult.Outcome outcome(SkatGameData data) {
        boolean passed = data.getGameType() == GameType.PASSED_IN;
        Map<Player,Integer> points = new EnumMap<>(Player.class);
        Map<Player,Integer> tricks = new EnumMap<>(Player.class);
        for (Player seat : Player.getOrderedList()) {
            points.put(seat, data.getPlayerPoints(seat));
            tricks.put(seat, (int) data.getTricks().stream().filter(t -> seat == t.getTrickWinner()).count());
        }
        var contract = data.getAnnouncement().contract();
        return new TournamentGameResult.Outcome(passed, data.getDeclarer(), !passed && data.isGameWon(),
                data.getResult().getGameValue(), passed ? null : new TournamentContract(contract.gameType(),
                contract.hand(), contract.ouvert(), contract.schneider(), contract.schwarz()), points, tricks);
    }

    static Map<Player,Long> scores(TournamentGameResult.Outcome outcome) {
        Map<Player,Long> result = new EnumMap<>(Player.class);
        for (Player seat : Player.getOrderedList()) {
            long score = 0;
            if (!outcome.allPassed()) {
                if (seat == outcome.declarer()) score = outcome.signedGameValue() + (outcome.declarerWon() ? 50 : -50);
                else if (!outcome.declarerWon()) score = 40;
            }
            result.put(seat, score);
        }
        return result;
    }

    /** Complete versioned audit projection. Payloads contain copied values only. */
    private static final class Audit {
        final List<TournamentGameEvent> events = new ArrayList<>();
        int cardsPlayed;
        void add(String type, JsonNode payload) {
            events.add(new TournamentGameEvent(events.size(), type, CanonicalJson.encode(payload)));
        }
        void move(SkatGameEvent event) {
            ObjectNode json = CanonicalJson.object();
            if (event instanceof AbstractPlayerMoveEvent player && player.player != null)
                json.put("seat", player.player.name());
            if (event instanceof AbstractBidEvent bid) json.put("bid", bid.bid);
            if (event instanceof PassBidEvent pass) json.put("bid", pass.nextBidValue);
            if (event instanceof CardDealEvent deal) {
                for (Player seat : Player.getOrderedList()) cards(json, seat.name(), deal.playerCards.get(seat));
                cards(json, "skat", deal.skat);
            }
            if (event instanceof PickUpSkatEvent pickup) cards(json, "cards", pickup.pickedUpSkat);
            if (event instanceof DiscardSkatEvent discard) cards(json, "cards", discard.discardedSkat);
            if (event instanceof TrickCardPlayedEvent card) json.put("card", card.card.name());
            if (event instanceof GameAnnouncementEvent announced) {
                GameContract contract = announced.announcement.contract();
                json.put("gameType", contract.gameType().name());
                json.put("hand", contract.hand());
                json.put("ouvert", contract.ouvert());
                json.put("schneider", contract.schneider());
                json.put("schwarz", contract.schwarz());
                cards(json, "ouvertCards", contract.ouvertCards());
            }
            add(event.getClass().getSimpleName(), json);
        }
        void cards(ObjectNode object, String field, CardList cards) {
            var array = object.putArray(field);
            for (Card card : cards) array.add(card.name());
        }
    }
}
