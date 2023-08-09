package org.jskat.ai.sascha;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.ai.sascha.opponent.LeftOpponentGrand;
import org.jskat.ai.sascha.opponent.LeftOpponentNull;
import org.jskat.ai.sascha.opponent.LeftOpponentSuit;
import org.jskat.ai.sascha.opponent.RightOpponentGrand;
import org.jskat.ai.sascha.opponent.RightOpponentNull;
import org.jskat.ai.sascha.opponent.RightOpponentSuit;
import org.jskat.ai.sascha.solo.GrandPlayer;
import org.jskat.ai.sascha.solo.NullPlayer;
import org.jskat.ai.sascha.solo.SuitPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;

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

    private Bidder bidder = null;
    private boolean myGame = false;
    private AbstractPlayer player = null;
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

        log.debug("Constructing new Sascha");
        setPlayerName(newPlayerName);
    }

    @Override
    public boolean pickUpSkat() {
        log.info("picking up skat");
        return true;
    }

    @Override
    public boolean playGrandHand() {
        return false;
    }

    @Override
    public GameAnnouncement announceGame() {
        myGame = true;
        var a = bidder.gameAnnouncement();
        log.info("announcing game " + a + " on bid: " + knowledge.getHighestBid(knowledge.getPlayerPosition()));
        return bidder.gameAnnouncement();
    }

    @Override
    public int bidMore(final int nextBidValue) {

        if (bidder == null) {
            bidder = new Bidder(knowledge.getOwnCards(), knowledge.getPlayerPosition().getOrder(), aggroLevel);
        }
        if (bidder.getGameValue() >= nextBidValue) {
            log.info("bidding more");
            return nextBidValue;
        }
        log.info("passing");
        return 0;
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        if (bidder == null) {
            log.info("holding bid");
            bidder = new Bidder(knowledge.getOwnCards(), knowledge.getPlayerPosition().getOrder(), aggroLevel);
        }
        log.info("passing");
        return bidder.getGameValue() >= currBidValue;
    }

    @Override
    public void startGame() {
        if (knowledge.getDeclarer() == knowledge.getPlayerPosition()) {
            switch (knowledge.getGameType()) {
                case GRAND:
                    player = new GrandPlayer(knowledge);
                    break;
                case NULL:
                    player = new NullPlayer(knowledge);
                    break;
                default:
                    player = new SuitPlayer(knowledge);
                    break;
            }

        } else if (knowledge.getDeclarer().getLeftNeighbor() == knowledge.getPlayerPosition()) {
            switch (knowledge.getGameType()) {
                case GRAND:
                    player = new LeftOpponentGrand(knowledge);
                    break;
                case NULL:
                    player = new LeftOpponentNull(knowledge);
                    break;
                default:
                    player = new LeftOpponentSuit(knowledge);
                    break;
            }

        } else if (knowledge.getDeclarer().getRightNeighbor() == knowledge.getPlayerPosition()) {
            switch (knowledge.getGameType()) {
                case GRAND:
                    player = new RightOpponentGrand(knowledge);
                    break;
                case NULL:
                    player = new RightOpponentNull(knowledge);
                    break;
                default:
                    player = new RightOpponentSuit(knowledge);
                    break;
            }

        } else {
            log.error("no player stance created");
        }
    }

    @Override
    public Card playCard() {
        Card c;
        try {
            c = player.playCard();
        } catch (Exception e) {
            log.error("exception", e);
            throw e;
        }

        log.info("playing card: " + c);

        if (!knowledge.getOwnCards().contains(c)) {
            log.error("trying to play non-own card: " + c);
        }
        if (!getPlayableCards(knowledge.getCurrentTrick().getCardList()).contains(c)) {
            log.error("trying to play non-allowed card: " + c);
            log.error(player.getClass().getName());
        }

        return c;
    }

    @Override
    public CardList getCardsToDiscard() {
        bidder = new Bidder(knowledge.getOwnCards(), aggroLevel, aggroLevel);
        log.info("discarding: " + bidder.getCardsToDiscard());
        return bidder.getCardsToDiscard();
    }

    @Override
    public void prepareForNewGame() {
        myGame = false;
        // nothing to do for AIPlayerRND
    }

    @Override
    public void finalizeGame() {
        bidder = null;
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