package org.jskat.gui.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
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
    private GameContract gameContract;
    private Card nextCard;

    /**
     * Used for situations where a human player can make more than one move.
     */
    private enum GameAnnouncementStep {
        /**
         * Before any announcement
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
    public GameContract announceGame() {

        log.debug("Waiting for human game announcing...");

        waitForUserInput();

        gameAnnouncementStep = GameAnnouncementStep.DONE_GAME_ANNOUNCEMENT;

        return gameContract;
    }

    /**
     * @see JSkatPlayer#bidMore(int)
     */
    @Override
    public int bidMore(final int nextBidValue) {

        log.debug("Waiting for human next bid value...");

        waitForUserInput();

        if (holdBid) {
            bidValue = nextBidValue;
        } else {
            bidValue = 0;
        }

        return bidValue;
    }

    /**
     * @see JSkatPlayer#discardSkat()
     */
    @Override
    public CardList getCardsToDiscard() {

        log.info("Waiting for human discarding...");

        waitForUserInput();

        return discardSkat;
    }

    /**
     * @see JSkatPlayer#prepareForNewGame()
     */
    @Override
    public void prepareForNewGame() {

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
    public boolean holdBid(final int currBidValue) {

        log.debug("Waiting for human holding bid...");

        waitForUserInput();

        return holdBid;
    }

    /**
     * @see JSkatPlayer#pickUpSkat()
     */
    @Override
    public boolean playGrandHand() {

        log.debug("Waiting for human to decide if playing a grand hand...");

        waitForUserInput();

        return playGrandHand;
    }

    /**
     * @see JSkatPlayer#pickUpSkat()
     */
    @Override
    public boolean pickUpSkat() {

        log.info("Waiting for human looking into skat...");

        waitForUserInput();

        return pickUpSkat;
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

        final Object source = e.getSource();
        final String command = e.getActionCommand();
        boolean interrupt = true;

        if (JSkatAction.PASS_BID.toString().equals(command)) {
            // player passed
            holdBid = false;
        } else if (JSkatAction.MAKE_BID.toString().equals(command)) {
            // player makes next bid value
            holdBid = true;
        } else if (JSkatAction.HOLD_BID.toString().equals(command)) {
            // player hold bid
            holdBid = true;
        } else if (JSkatAction.PLAY_GRAND_HAND.toString().equals(command)) {
            // player wants to play a grand hand
            playGrandHand = true;
        } else if (JSkatAction.PLAY_SCHIEBERAMSCH.toString().equals(command)) {
            playGrandHand = false;
        } else if (JSkatAction.CALL_CONTRA.toString().equals(command)) {
            callContra = true;
        } else if (JSkatAction.CALL_RE.toString().equals(command)) {
            if (source instanceof Boolean) {
                callRe = (Boolean) source;
            }
        } else if (JSkatAction.PICK_UP_SKAT.toString().equals(command)) {
            // player wants to pick up the skat
            pickUpSkat = true;
            gameAnnouncementStep = GameAnnouncementStep.LOOKED_INTO_SKAT;
        } else if (JSkatAction.SCHIEBEN.toString().equals(command)) {
            if (source instanceof final CardList cards) {
                if (cards.size() == 0) {
                    pickUpSkat = false;
                } else {
                    pickUpSkat = true;
                    discardSkat = new CardList(cards);
                }
            } else {
                log.warn("Wrong source {} for command {}", source, command);
                interrupt = false;
            }
        } else if (JSkatAction.ANNOUNCE_GAME.toString().equals(command)) {
            if (source instanceof final GameAnnouncement announcement) {
                gameContract = announcement.contract();

                if (gameContract.hand()) {
                    gameAnnouncementStep = GameAnnouncementStep.PLAYS_HAND;
                } else {
                    discardSkat = announcement.discardedCards();
                    gameAnnouncementStep = GameAnnouncementStep.DISCARDED_SKAT;
                }
            } else {
                log.warn("Wrong source for " + command);
                interrupt = false;
            }
        } else if (JSkatAction.PLAY_CARD.toString().equals(command) && source instanceof Card) {

            nextCard = (Card) source;

        } else {

            log.warn("Unknown action event occurred: " + command + " from " + source);
        }

        if (interrupt) {

            idler.interrupt();
        }
    }

    /**
     * Starts waiting for user input
     */
    public void waitForUserInput() {

        idler = new Idler();
        idler.setMonitor(this);

        if (!isPlayerHasAlreadyPlayed()) {

            idler.start();
            try {
                idler.join();
            } catch (final InterruptedException e) {
                log.warn("wait for user input was interrupted");
            }
        }
    }

    private boolean isPlayerHasAlreadyPlayed() {

        log.debug("Game announcement step: " + gameAnnouncementStep);

        final boolean result = GameAnnouncementStep.DISCARDED_SKAT.equals(gameAnnouncementStep)
                || GameAnnouncementStep.PLAYS_HAND.equals(gameAnnouncementStep);

        return result;
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

            monitor = newMonitor;
        }

        /**
         * Stops the waiting
         */
        public void stopWaiting() {
            doWait = false;
        }

        /**
         * @see Thread#run()
         */
        @Override
        public void run() {
            synchronized (monitor) {
                while (doWait) {
                    try {
                        monitor.wait();
                    } catch (final InterruptedException e) {
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
        gameContract = null;
        nextCard = null;
    }

    @Override
    public boolean callContra() {

        log.debug("Waiting for human calling contra...");

        if (callContra == null) {
            waitForUserInput();
        }

        return callContra != null && callContra;
    }

    @Override
    public boolean callRe() {

        log.debug("Waiting for human calling re...");

        if (callRe == null) {
            waitForUserInput();
        }

        return callRe != null && callRe;
    }
}
