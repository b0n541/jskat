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
import org.jskat.util.Rank;
import org.jskat.util.SkatConstants;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private MyState state = null;

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
        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();

        log.info("announce game type: " + state.game.toString());
        factory.setGameType(state.game);
        factory.setHand(state.hand);
        factory.setSchneider(state.schneider);
        factory.setSchwarz(state.schwartz);

        return factory.getAnnouncement();
    }

    @Override
    public int bidMore(final int nextBidValue) {
        // create state because there's no setup func
        state = Util.CreateState(knowledge.getOwnCards());

        int result = 0;
        if (state.maxBid >= nextBidValue) {
            result = nextBidValue;
        }
        return result;
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        // create state because there's no setup func
        state = Util.CreateState(knowledge.getOwnCards());

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
        log.info("discarding cards");

        return Util.discardCards(knowledge.getOwnCards());
    }

    @Override
    public void prepareForNewGame() {

    }

    @Override
    public void finalizeGame() {
        state = null;
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
    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public static MyState CreateState(CardList myCards) {
        MyState newState = new MyState();

        // For the case of perfect cards
        if (Util.isGoodGrandHand(myCards)) {
            newState.game = GameType.GRAND;
            newState.maxBid = jacksMultiplier(myCards) * SkatConstants.getGameBaseValue(newState.game, true, true);;
            newState.pickUpSkat = false;
            newState.schneider = true;
            newState.hand = true;
            newState.schwartz = true;
        }

        // If we have 5 cards from one color plus jacks
        final int mostFrequentSuitCards = myCards.getSuitCount(myCards.getMostFrequentSuit(), false);
        final int jacks = Util.countRank(myCards, Rank.JACK);
        if ((mostFrequentSuitCards + jacks) >= 6) {
            newState.game = GameType.valueOf(myCards.getMostFrequentSuit().toString());
            // TODO check how much i can bid with jacks
            newState.maxBid = jacksMultiplier(myCards) * SkatConstants.getGameBaseValue(newState.game, false, false);
            newState.pickUpSkat = true;
        }

        return newState;
    }

    public static Suit getLeastFrequentSuit(CardList cards, boolean atleastOne, boolean countJack) {
        int count = 8;
        Suit suitResult = null;
        for (Suit suit : Suit.values()) {
            int suitCount = cards.getSuitCount(suit, countJack);
            if (suitCount <= count && !(atleastOne == true && suitCount == 0)) {
                count = suitCount;
                suitResult = suit;
            }
        }
        return suitResult;
    }

    // util bidder
    public final static boolean isGoodGrandHand(CardList hand) {
        final int jacks = countRank(hand, Rank.JACK);
        final int aces = countRank(hand, Rank.ACE);
        final int tens = countRank(hand, Rank.TEN);
        if (jacks == 4 && (aces + tens) >= 2) {
            return true;
        }
        return false;
    }

    // util bidder
    public final static CardList discardCards(CardList hand) {
        final CardList result = new CardList();
        CardList discardableCards = new CardList(hand);

        while (result.size() < 2) {
            Suit leastSuit = Util.getLeastFrequentSuit(discardableCards, true, false);
            CardList suitCards = Util.filterSuite(discardableCards, leastSuit, true);

            // kepp 10 if Ace and 10 is in throwable
            if (Util.countRank(suitCards, Rank.TEN) == 1 && Util.countRank(suitCards, Rank.ACE) == 1) {
                discardableCards.remove(Card.getCard(leastSuit, Rank.TEN));
                discardableCards.remove(Card.getCard(leastSuit, Rank.ACE));
                continue;
            }
            // do not discard Aces
            if (Util.countRank(suitCards, Rank.ACE) == 1) {
                discardableCards.remove(Card.getCard(leastSuit, Rank.ACE));
                continue;
            }
            // discard single tens
            if (Util.countRank(suitCards, Rank.TEN) == 1) {
                Card ten = Card.getCard(leastSuit, Rank.TEN);
                discardableCards.remove(ten);
                result.add(ten);
                continue;
            }

            // discard the lowest card of least suit
            Card lowestCard = Util.getLowest(suitCards, leastSuit, true);
            discardableCards.remove(lowestCard);
            result.add(lowestCard);
        }

        return result;
    }

    public final static int countRank(CardList hand, Rank rank) {
        int count = 0;
        for (final Card c: hand) {
            if (c.getRank() == rank) {
                count++;
            }
        }
        return count;
    }

    // util  bidder
    public final static int jacksMultiplier(CardList hand) {
        var mit = hand.hasJack(Suit.CLUBS);
        if (mit) {
            if (!hand.hasJack(Suit.SPADES))
                return 2;
            if (!hand.hasJack(Suit.HEARTS))
                return 3;
            if (!hand.hasJack(Suit.DIAMONDS))
                return 4;
            return 5;
        } else {
            if (hand.hasJack(Suit.SPADES))
                return 2;
            if (hand.hasJack(Suit.HEARTS))
                return 3;
            if (hand.hasJack(Suit.DIAMONDS))
                return 4;
            return 5;
        }
    }

    public static final CardList filterSuite(final CardList cards, final Suit suit, boolean ignoreJacks) {
        CardList r = new CardList();
        cards.forEach((c) -> {
            if (c.getSuit() == suit && !(ignoreJacks && c.getRank() == Rank.JACK)) {
                r.add(c);
            }
        });
        return r;
    }

    public static final Card getLowest(final CardList cards, final Suit suit, boolean ignoreJacks) {
        Card lowest = cards.get(cards.getFirstIndexOfSuit(suit));
        for (Card c : cards) {
            if (c.getSuit() == suit && !(ignoreJacks && c.getRank() == Rank.JACK)) {
                if (lowest.beats(GameType.CLUBS, c)) {
                    lowest = c;
                }
            }
        };
        return lowest;
    }
}