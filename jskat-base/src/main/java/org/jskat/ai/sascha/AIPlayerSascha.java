package org.jskat.ai.sascha;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Random player for testing purposes and driving the other players nuts.
 */
public class AIPlayerSascha extends AbstractAIPlayer {

    private static final Logger log = LoggerFactory.getLogger(AIPlayerSascha.class);

    /**
     * Random generator for decision making.
     */
    private final Random random = new Random();

    private Bidder bider = null;
    private int aggroLevel = 0;

    /**
     * Creates a new instance of AIPlayerRND.
     */
    public AIPlayerSascha() {

        this("unknown");
    }

    /**
     * Creates a new instance of AIPlayerRND.
     *
     * @param newPlayerName Player's name
     */
    public AIPlayerSascha(final String newPlayerName) {

        log.debug("Constructing new AIPlayerRND");
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
        return bider.gameAnnouncement();
    }

    @Override
    public int bidMore(final int nextBidValue) {
        if (bider == null) {
            bider = new Bidder(knowledge.getOwnCards(), knowledge.getPlayerPosition().getOrder(), aggroLevel);
        }
        if (bider.getGameValue() >= nextBidValue) {
            return nextBidValue;
        }
        return 0;
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        if (bider == null) {
            bider = new Bidder(knowledge.getOwnCards(), knowledge.getPlayerPosition().getOrder(), aggroLevel);
        }
        return bider.getGameValue() >= currBidValue;
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
        final CardList possibleCards = getPlayableCards(knowledge
                .getTrickCards());

        log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards);

        // then choose a random one
        index = random.nextInt(possibleCards.size());

        log.debug("choosing card " + index);
        log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(index));

        return possibleCards.get(index);
    }

    @Override
    public CardList getCardsToDiscard() {
        return bider.getCardsToDiscard();
    }

    @Override
    public void prepareForNewGame() {
        // nothing to do for AIPlayerRND
    }

    @Override
    public void finalizeGame() {
        bider = null;
        // nothing to do for AIPlayerRND
    }

    @Override
    public boolean callContra() {
        return random.nextBoolean();
    }

    @Override
    public boolean callRe() {
        return random.nextBoolean();
    }
}