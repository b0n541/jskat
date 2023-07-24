package org.jskat.ai.alex;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.player.AbstractJSkatPlayer;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jskat.player.ImmutablePlayerKnowledge;

import java.util.Random;

/**
 * Random player for testing purposes and driving the other players nuts.
 */
public class AIPlayerAlex extends AbstractAIPlayer {

    private static final Logger log = LoggerFactory.getLogger(AIPlayerAlex.class);

    /**
     * Random generator for decision making.
     */
    private final Random random = new Random();
    private MyState state = new MyState();

    /**
     * Creates a new instance of AIPlayerRND.
     */
    public AIPlayerAlex() {
        this("Alex");
    }

    /**
     * Creates a new instance of AIPlayerRND.
     *
     * @param newPlayerName Player's name
     */
    public AIPlayerAlex(final String newPlayerName) {
        log.debug("Constructing new AIPlayerAlex");
        setPlayerName(newPlayerName);
    }

    @Override
    public boolean pickUpSkat() {
        return state.pickUpSkat;
    }

    @Override
    public boolean playGrandHand() {
        // only for ramschen
        return false;
    }

    @Override
    public GameAnnouncement announceGame() {
        log.debug("position: " + knowledge.getPlayerPosition());
        log.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) +
                " " + knowledge.getHighestBid(Player.MIDDLEHAND) +
                " " + knowledge.getHighestBid(Player.REARHAND));

        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();

        // select a random game type (without RAMSCH and PASSED_IN)
        factory.setGameType(state.game);
        factory.setHand(state.hand);
        factory.setSchneider(state.schneider);
        factory.setSchwarz(state.schwartz);

        return factory.getAnnouncement();
    }

    @Override
    public int bidMore(final int nextBidValue) {
        // create state because there's no setup func
        state = Util.CreateState(knowledge);

        int result = 0;
        if (state.maxBid >= nextBidValue) {
            result = nextBidValue;
        }
        return result;
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        // create state because there's no setup func
        state = Util.CreateState(knowledge);

        if (state.maxBid >= currBidValue) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void startGame() {
        // do nothing
    }

    @Override
    public Card playCard() {
        int index = -1;
        log.debug('\n' + knowledge.toString());

        // first find all possible cards
        final CardList possibleCards = getPlayableCards(knowledge.getTrickCards());
        log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards);

        // then choose a random one
        index = random.nextInt(possibleCards.size());
        log.debug("choosing card " + index);
        log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(index));

        return possibleCards.get(index);
    }

    @Override
    public CardList getCardsToDiscard() {
        final CardList result = new CardList();
        CardList discardableCards = new CardList(knowledge.getOwnCards());

        // TODO 
        // 1. discard cards where we have only one in this color and it's NOT an ace
        // 2. discard cards where we have only two in this color
        for (int i = 0; i< 2; i++) {
            Suit leastSuit = Util.getLeastFrequentSuit(discardableCards);
            discardableCards.getSuitCount(leastSuit, false);
            discardableCards.getFirstIndexOfSuit(leastSuit);
            result.add(discardableCards.remove(discardableCards.getFirstIndexOfSuit(leastSuit)));
        }

        return result;
    }

    @Override
    public void prepareForNewGame() {
        state = new MyState();
        // nothing to do for AIPlayerRND
    }

    @Override
    public void finalizeGame() {
        state = new MyState();
        // nothing to do for AIPlayerRND
    }

    @Override
    public boolean callContra() {
        return false;
    }

    @Override
    public boolean callRe() {
        return false;
    }
}

class MyState {
    public GameType game = null;
    public boolean schneider = false;
    public boolean hand = false;
    public boolean schwartz = false;
    public boolean pickUpSkat = false;
    public int maxBid = 0;
}

class Util {
    public static MyState CreateState(ImmutablePlayerKnowledge knowledge) {
        MyState newState = new MyState();

        CardList myCards = new CardList(knowledge.getOwnCards());

        // For the case of perfect cards
        if (myCards.equals(CardList.getPerfectGrandSuitHand())) {
            newState.game = GameType.GRAND;
            newState.maxBid = SkatConstants.getGameBaseValue(newState.game, true, true);;
            newState.pickUpSkat = false;
            newState.schneider = true;
            newState.hand = true;
            newState.schwartz = true;
        }

        // If we have 6 cards from one color plus jacks
        if (myCards.getSuitCount(myCards.getMostFrequentSuit(), true) == 6) {
            newState.game = GameType.valueOf(myCards.getMostFrequentSuit().toString());
            newState.maxBid = SkatConstants.getGameBaseValue(newState.game, false, false);
            newState.pickUpSkat = true;
        }

        return newState;
    }

    public static Suit getLeastFrequentSuit(CardList cards) {
        int count = 10;
        Suit suitResult = null;
        for (Suit suit : Suit.values()) {
            int suitCount = cards.getSuitCount(suit, false);
            if (count < suitCount) {
                count = suitCount;
                suitResult = suit;
            }
        }
        return suitResult;
    }
}

// TODO 
