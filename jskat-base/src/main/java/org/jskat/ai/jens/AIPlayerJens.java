package org.jskat.ai.jens;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Random player for testing purposes and driving the other players nuts.
 */
public class AIPlayerJens extends AbstractAIPlayer {

    private static final Logger log = LoggerFactory.getLogger(AIPlayerJens.class);

    /**
     * Random generator for decision making.
     */
    private final Random random = new Random();

    /**
     * Creates a new instance of AIPlayerRND.
     */
    public AIPlayerJens() {

        this("Karl-Heinz");
    }

    /**
     * Creates a new instance of AIPlayerRND.
     *
     * @param newPlayerName Player's name
     */
    public AIPlayerJens(final String newPlayerName) {
        setPlayerName(newPlayerName);
    }

    @Override
    public boolean pickUpSkat() {
        return true;
    }

    @Override
    public boolean playGrandHand() {
        return false;
    }

    @Override
    public GameAnnouncement announceGame() {
        log.debug("position: " + knowledge.getPlayerPosition());
        log.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) +
                " " + knowledge.getHighestBid(Player.MIDDLEHAND) +
                " " + knowledge.getHighestBid(Player.REARHAND));

        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();

        factory.setGameType(gameType);

        // select a random game type (without RAMSCH and PASSED_IN)
        // final GameType gameType =
        // GameType.values()[random.nextInt(GameType.values().length - 2)];
        // if (false && Boolean.valueOf(random.nextBoolean())) {
        // factory.setOuvert(true);
        // if (gameType != GameType.NULL) {
        // factory.setHand(true);
        // factory.setSchneider(true);
        // factory.setSchwarz(true);
        // }
        // }

        return factory.getAnnouncement();
    }

    @Override
    public int bidMore(final int nextBidValue) {
        int result = 0;

        Boolean ok = bid(nextBidValue);
        if (ok) {
            result = nextBidValue;
        }

        log.error("Bid {}, {}", nextBidValue, ok ? "yes" : "no");

        return result;
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        Boolean ok = bid(currBidValue);
        log.error("Bid {}, {}", currBidValue, ok ? "yes" : "no");
        return ok;
    }

    private boolean bid(int bidValue) {
        CardList cards = knowledge.getOwnCards();
        int numJacks = 0;
        numJacks += cards.hasJack(Suit.CLUBS) ? 1 : 0;
        numJacks += cards.hasJack(Suit.SPADES) ? 1 : 0;
        numJacks += cards.hasJack(Suit.HEARTS) ? 1 : 0;
        numJacks += cards.hasJack(Suit.DIAMONDS) ? 1 : 0;

        return (numJacks > 1 && bidValue <= 18) ||
                (numJacks > 2 && bidValue <= 20) ||
                (numJacks > 3 && bidValue <= 22) ||
                (numJacks > 4 && bidValue <= 24);
    }

    int gameNum = 1;

    @Override
    public void startGame() {
        // do nothing
        log.error(" ");
        log.error("Gametype: {} ", knowledge.getGameType());
        log.error(" ");
        log.error("Got: {} ", knowledge.getOwnCards());
        log.error(" ");

    }

    @Override
    public Card playCard() {
        log.debug('\n' + knowledge.toString());

        gameType = knowledge.getGameType();

        // first find all possible cards
        CardList trickCards = knowledge.getTrickCards();
        CardList possibleCards = getPlayableCards(trickCards);

        possibleCards.sort(gameType);
        int index = possibleCards.size() - 1;

        Card card = null;
        try {
            if (trickCards.size() == 0 || gameType == null) {
                card = possibleCards.get(index);
            } else {
                for (int i = 0; i < possibleCards.size(); i++) {
                    var c = possibleCards.get(i);

                    Boolean beatsTrick = 
                        (trickCards.size() == 1 && c.beats(gameType, trickCards.get(0))) || 
                        (trickCards.size() == 2 && c.beats(gameType, trickCards.get(0)) && c.beats(gameType, trickCards.get(1)));
                    
                    if (beatsTrick && (!c.name().endsWith("J") || trickCards.getTotalValue() >= 10)) {
                        card = c;
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            log.error("WTF? {} - {}", ex.getMessage(), ex.getStackTrace());
        }

        if (card == null) {
            card = possibleCards.get(possibleCards.size() - 1);
        }

        String trick1 = trickCards.size() > 0 ? trickCards.get(0).toString() : "__";
        String trick2 = trickCards.size() > 1 ? trickCards.get(1).toString() : "__";
        log.error("Stack is {}, {} ({})- playing {} of {}", trick1, trick2, String.format("%02d", trickCards.getTotalValue()), card.toString(), possibleCards);
        return card;
    }

    int numClubs = 0;
    int numSpades = 0;
    int numHearts = 0;
    int numDiamonds = 0;
    GameType gameType = GameType.PASSED_IN;

    @Override
    public CardList getCardsToDiscard() {
        final CardList result = new CardList();

        CardList discardableCards = new CardList(knowledge.getOwnCards());

        for (var card : discardableCards) {
            switch (card.getSuit()) {
                case SPADES:
                    numSpades++;
                    break;

                case CLUBS:
                    numClubs++;
                    break;

                case HEARTS:
                    numHearts++;
                    break;

                case DIAMONDS:
                    numDiamonds++;
                    break;
            }
        }

        if (numSpades >= numClubs && numSpades >= numHearts && numSpades >= numDiamonds) {
            gameType = GameType.SPADES;
        }
        if (numClubs >= numSpades && numClubs >= numHearts && numClubs >= numDiamonds) {
            gameType = GameType.CLUBS;
        }
        if (numHearts >= numClubs && numHearts >= numSpades && numHearts >= numDiamonds) {
            gameType = GameType.HEARTS;
        }
        if (numDiamonds >= numClubs && numDiamonds >= numHearts && numDiamonds >= numSpades) {
            gameType = GameType.DIAMONDS;
        }

        discardableCards.sort(gameType);

        // just discard two random cards
        result.add(discardableCards.get(0));
        result.add(discardableCards.get(1));

        return result;
    }

    @Override
    public void prepareForNewGame() {
        log.error(" ");
        log.error("------ NEW GAME ({}) -------", gameNum++);
        log.error(" ");
    }

    @Override
    public void finalizeGame() {
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
