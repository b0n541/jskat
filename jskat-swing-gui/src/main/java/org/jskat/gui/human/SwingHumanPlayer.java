package org.jskat.gui.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Human player
 */
public class SwingHumanPlayer extends AbstractHumanJSkatPlayer {

    private static final Logger log = LoggerFactory.getLogger(SwingHumanPlayer.class);

    private Idler idler = new Idler();

    private Boolean holdBid;
    private Integer bidValue;
    private GameAnnouncementStep gameAnnouncementStep;
    private Boolean playGrandHand;
    private Boolean callContra;
    private Boolean callRe;
    private Boolean pickUpSkat;
    private CardList discardSkat;
    private GameAnnouncement gameAnnouncement;
    private Card nextCard;

    /**
     * Used for situations where a human player can make more than one move.
     */
    private enum GameAnnouncementStep {
        /**
         * Before any anouncement
         */
        BEFORE_ANNOUNCEMENT,
        /**
         * Player looked into skat
         */
        LOOKED_INTO_SKAT,
        /**
         * Player discarded skat
         */
        DISCARDED_SKAT,
        /**
         * Player announced hand game
         */
        PLAYS_HAND,
        /**
         * Game announcement is done
         */
        DONE_GAME_ANNOUNCEMENT
    }

    /**
     * Constructor
     */
    public SwingHumanPlayer() {
        resetPlayer();
    }

    /**
     * @see JSkatPlayer#announceGame()
     */
    @Override
    public GameAnnouncement announceGame() {

        log.debug("Waiting for human game announcing...");

        waitForUserInput();

        gameAnnouncementStep = GameAnnouncementStep.DONE_GAME_ANNOUNCEMENT;

        return this.gameAnnouncement;
    }

    /**
     * @see JSkatPlayer#bidMore(int)
     */
    @Override
    public Integer bidMore(final int nextBidValue) {

        log.debug("Waiting for human next bid value...");

        waitForUserInput();

        if (this.holdBid) {

            this.bidValue = nextBidValue;
        } else {

            this.bidValue = -1;
        }

        return this.bidValue;
    }

    /**
     * @see JSkatPlayer#discardSkat()
     */
    @Override
    public CardList getCardsToDiscard() {

        log.debug("Waiting for human discarding...");

        waitForUserInput();

        return this.discardSkat;
    }

    /**
     * @see JSkatPlayer#preparateForNewGame()
     */
    @Override
    public void preparateForNewGame() {

        resetPlayer();
    }

    /**
     * @see JSkatPlayer#finalizeGame()
     */
    @Override
    public void finalizeGame() {
        // TODO implement it
    }

    /**
     * @see JSkatPlayer#holdBid(int)
     */
    @Override
    public Boolean holdBid(final int currBidValue) {

        log.debug("Waiting for human holding bid...");

        waitForUserInput();

        return this.holdBid;
    }

    /**
     * @see JSkatPlayer#pickUpSkat()
     */
    @Override
    public Boolean playGrandHand() {

        log.debug("Waiting for human to decide if playing a grand hand...");

        waitForUserInput();

        return this.playGrandHand;
    }

    /**
     * @see JSkatPlayer#pickUpSkat()
     */
    @Override
    public Boolean pickUpSkat() {

        log.debug("Waiting for human looking into skat...");

        waitForUserInput();

        return this.pickUpSkat;
    }

    /**
     * @see JSkatPlayer#playCard()
     */
    @Override
    public Card playCard() {

        log.debug("Waiting for human playing next card...");

        Card cardToPlay = null;

        if (nextCard == null) {
            waitForUserInput();
        }

        cardToPlay = nextCard;
        nextCard = null;

        return cardToPlay;
    }

    @Override
    public void actionPerformed(final JSkatActionEvent e) {

        Object source = e.getSource();
        String command = e.getActionCommand();
        boolean interrupt = true;

        if (JSkatAction.PASS_BID.toString().equals(command)) {
            // player passed
            this.holdBid = false;
        } else if (JSkatAction.MAKE_BID.toString().equals(command)) {
            // player makes next bid value
            this.holdBid = true;
        } else if (JSkatAction.HOLD_BID.toString().equals(command)) {
            // player hold bid
            this.holdBid = true;
        } else if (JSkatAction.PLAY_GRAND_HAND.toString().equals(command)) {
            // player wants to play a grand hand
            this.playGrandHand = true;
        } else if (JSkatAction.PLAY_SCHIEBERAMSCH.toString().equals(command)) {
            this.playGrandHand = false;
        } else if (JSkatAction.CALL_CONTRA.toString().equals(command)) {
            callContra = true;
        } else if (JSkatAction.CALL_RE.toString().equals(command)) {
            if (source instanceof Boolean) {
                callRe = (Boolean) source;
            }
        } else if (JSkatAction.PICK_UP_SKAT.toString().equals(command)) {
            // player wants to pick up the skat
            this.pickUpSkat = true;
            gameAnnouncementStep = GameAnnouncementStep.LOOKED_INTO_SKAT;
        } else if (JSkatAction.SCHIEBEN.toString().equals(command)) {
            if (source instanceof CardList) {
                CardList cards = (CardList) source;
                if (cards.size() == 0) {
                    pickUpSkat = false;
                } else {
                    pickUpSkat = true;
                    discardSkat = new CardList(cards);
                }
            }
        } else if (JSkatAction.ANNOUNCE_GAME.toString().equals(command)) {
            if (source instanceof GameAnnouncement) {
                // player did game announcement
                gameAnnouncement = (GameAnnouncement) source;

                if (gameAnnouncement.isHand()) {
                    gameAnnouncementStep = GameAnnouncementStep.PLAYS_HAND;
                } else {
                    setDiscardedSkatCards(gameAnnouncement.getDiscardedCards());
                    gameAnnouncementStep = GameAnnouncementStep.DISCARDED_SKAT;
                }
            } else {
                log.warn("Wrong source for " + command);
                interrupt = false;
            }
        } else if (JSkatAction.PLAY_CARD.toString().equals(command)
                && source instanceof Card) {

            this.nextCard = (Card) source;

        } else {

            log.warn("Unknown action event occured: " + command + " from " + source);
        }

        if (interrupt) {

            this.idler.interrupt();
        }
    }

    /**
     * Starts waiting for user input
     */
    public void waitForUserInput() {

        this.idler = new Idler();
        this.idler.setMonitor(this);

        if (!isPlayerHasAlreadyPlayed()) {

            this.idler.start();
            try {
                this.idler.join();
            } catch (InterruptedException e) {
                log.warn("wait for user input was interrupted");
            }
        }
    }

    private boolean isPlayerHasAlreadyPlayed() {

        log.debug("Game announcement step: " + gameAnnouncementStep);

        boolean result = GameAnnouncementStep.DISCARDED_SKAT.equals(gameAnnouncementStep)
                || GameAnnouncementStep.PLAYS_HAND.equals(gameAnnouncementStep);

        return result;
    }

    private void setDiscardedSkatCards(final CardList discardedCards) {
        discardSkat = discardedCards;
    }

    /*-------------------------------------------------------------------
     * Inner class
     *-------------------------------------------------------------------*/

    /**
     * Protected class implementing the waiting thread for user input
     */
    protected static class Idler extends Thread {

        /**
         * Sets the monitoring object
         *
         * @param newMonitor Monitor
         */
        public void setMonitor(final Object newMonitor) {

            this.monitor = newMonitor;
        }

        /**
         * Stops the waiting
         */
        public void stopWaiting() {

            this.doWait = false;
        }

        /**
         * @see Thread#run()
         */
        @Override
        public void run() {

            synchronized (this.monitor) {

                while (this.doWait) {
                    try {
                        this.monitor.wait();
                    } catch (InterruptedException e) {
                        stopWaiting();
                    }
                }
            }
        }

        private boolean doWait = true;
        private Object monitor = null;
    }

    /**
     * @see org.jskat.player.AbstractJSkatPlayer#startGame()
     */
    @Override
    public void startGame() {
        // TODO is there something todo?
    }

    private void resetPlayer() {
        bidValue = 0;
        holdBid = false;
        playGrandHand = false;
        callContra = false;
        callRe = false;
        gameAnnouncementStep = GameAnnouncementStep.BEFORE_ANNOUNCEMENT;
        pickUpSkat = false;
        discardSkat = null;
        gameAnnouncement = null;
        nextCard = null;
    }

    @Override
    public Boolean callContra() {

        log.debug("Waiting for human calling contra...");

        if (callContra == null) {
            waitForUserInput();
        }

        return callContra != null && callContra;
    }

    @Override
    public Boolean callRe() {

        log.debug("Waiting for human calling re...");

        if (callRe == null) {
            waitForUserInput();
        }

        return callRe != null && callRe;
    }
}
