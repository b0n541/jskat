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
        log.info("pickUpSkat");
        return true;
    }

    @Override
    public boolean playGrandHand() {
        return false;
    }

    @Override
    public GameAnnouncement announceGame() {
        log.info("announceGame");
        myGame = true;
        var a = bidder.gameAnnouncement();
        log.info("announcing game " + a + " on bid: " + knowledge.getHighestBid(knowledge.getPlayerPosition()));
        return bidder.gameAnnouncement();
    }

    @Override
    public int bidMore(final int nextBidValue) {
        log.info("bidMore");
        if (bidder == null) {
            bidder = new Bidder(knowledge.getOwnCards(), knowledge.getPlayerPosition());
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
        log.info("holdBid");
        if (bidder == null) {
            log.info("holding bid");
            bidder = new Bidder(knowledge.getOwnCards(), knowledge.getPlayerPosition());
        }
        log.info("passing");
        return bidder.getGameValue() >= currBidValue;
    }

    @Override
    public void startGame() {
        log.info("startGame");
        try {
            if (knowledge.getDeclarer() == null || knowledge.getDeclarer() == knowledge.getPlayerPosition()) {
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
        } catch (Exception e) {

            log.error("", e);
            System.exit(1);
        }
    }

    @Override
    public Card playCard() {
        log.info("playCard");
        Card c;
        try {
            c = player.playCard();
        } catch (Exception e) {
            log.error("exception", e);
            System.exit(1);
            throw e;
        }

        log.info("playing card: " + c);

        if (!knowledge.getOwnCards().contains(c)) {
            log.error("trying to play non-own card: " + c);
            System.exit(1);
        }
        if (!getPlayableCards(knowledge.getCurrentTrick().getCardList()).contains(c)) {
            log.error("trying to play non-allowed card: " + c);
            log.error(player.getClass().getName());
            System.exit(1);
        }

        return c;
    }

    @Override
    public CardList getCardsToDiscard() {
        log.info("getCardsToDiscard " + Util.makeReadable(knowledge.getOwnCards()));
        bidder = new Bidder(knowledge.getOwnCards(), knowledge.getPlayerPosition());
        log.info("discarding: " + bidder.getCardsToDiscard());
        try {
            return bidder.getCardsToDiscard();
        } catch (Exception e) {
            log.error("", e);
            System.exit(1);
            throw e;
        }

    }

    @Override
    public void prepareForNewGame() {
        log.info("prepareForNewGame");
        myGame = false;
        // nothing to do for AIPlayerRND
    }

    @Override
    public void finalizeGame() {
        log.info("finalizeGame");
        bidder = null;
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