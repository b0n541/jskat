package org.jskat.gui.human

import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer
import org.jskat.data.GameAnnouncement
import org.jskat.data.GameContract
import org.jskat.util.Card
import org.jskat.util.CardList
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Human player
 */
@Deprecated("Should be replaced by an implementation without waiting thread")
class SwingHumanPlayer : AbstractHumanJSkatPlayer() {
    private var idler = Idler()

    private var holdBid: Boolean = false
    private var bidValue: Int = 0
    private var gameAnnouncementStep: GameAnnouncementStep? = null
    private var playGrandHand: Boolean = false
    private var callContra: Boolean = false
    private var callRe: Boolean = false
    private var pickUpSkat: Boolean = false
    private var discardSkat: CardList = CardList()
    private var gameContract: GameContract? = null
    private var nextCard: Card? = null

    /**
     * Used for situations where a human player can make more than one move.
     */
    private enum class GameAnnouncementStep {
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
    init {
        resetPlayer()
    }

    /**
     * @see JSkatPlayer.announceGame
     */
    override fun announceGame(): GameContract? {
        Companion.log.debug("Waiting for human game announcing...")

        waitForUserInput()

        gameAnnouncementStep = GameAnnouncementStep.DONE_GAME_ANNOUNCEMENT

        return gameContract
    }

    /**
     * @see JSkatPlayer.bidMore
     */
    override fun bidMore(nextBidValue: Int): Int {
        Companion.log.debug("Waiting for human next bid value...")

        waitForUserInput()

        if (holdBid) {
            bidValue = nextBidValue
        } else {
            bidValue = 0
        }

        return bidValue!!
    }

    /**
     * @see JSkatPlayer.discardSkat
     */
    public override fun getCardsToDiscard(): CardList {
        Companion.log.info("Waiting for human discarding...")

        waitForUserInput()

        return discardSkat
    }

    /**
     * @see JSkatPlayer.prepareForNewGame
     */
    override fun prepareForNewGame() {
        resetPlayer()
    }

    /**
     * @see JSkatPlayer.finalizeGame
     */
    override fun finalizeGame() {
        // TODO implement it
    }

    /**
     * @see JSkatPlayer.holdBid
     */
    override fun holdBid(currBidValue: Int): Boolean {
        Companion.log.debug("Waiting for human holding bid...")

        waitForUserInput()

        return holdBid!!
    }

    /**
     * @see JSkatPlayer.pickUpSkat
     */
    override fun playGrandHand(): Boolean {
        Companion.log.debug("Waiting for human to decide if playing a grand hand...")

        waitForUserInput()

        return playGrandHand!!
    }

    /**
     * @see JSkatPlayer.pickUpSkat
     */
    override fun pickUpSkat(): Boolean {
        if (gameAnnouncementStep != GameAnnouncementStep.LOOKED_INTO_SKAT) {
            Companion.log.info("Waiting for human looking into skat...")

            waitForUserInput()
        }

        return pickUpSkat!!
    }

    /**
     * @see JSkatPlayer.playCard
     */
    override fun playCard(): Card? {
        Companion.log.debug("Waiting for human playing next card...")

        var cardToPlay: Card? = null

        if (nextCard == null) {
            waitForUserInput()
        }

        cardToPlay = nextCard
        nextCard = null

        return cardToPlay
    }

    override fun actionPerformed(e: JSkatActionEvent) {
        val source = e.source
        val command = e.actionCommand
        var interrupt = true

        if (JSkatAction.PASS_BID.toString() == command) {
            // player passed
            holdBid = false
        } else if (JSkatAction.MAKE_BID.toString() == command) {
            // player makes next bid value
            holdBid = true
        } else if (JSkatAction.HOLD_BID.toString() == command) {
            // player hold bid
            holdBid = true
        } else if (JSkatAction.PLAY_GRAND_HAND.toString() == command) {
            // player wants to play a grand hand
            playGrandHand = true
        } else if (JSkatAction.PLAY_SCHIEBERAMSCH.toString() == command) {
            playGrandHand = false
        } else if (JSkatAction.CALL_CONTRA.toString() == command) {
            callContra = true
        } else if (JSkatAction.CALL_RE.toString() == command) {
            if (source is Boolean) {
                callRe = source
            }
        } else if (JSkatAction.PICK_UP_SKAT.toString() == command) {
            // player wants to pick up the skat
            pickUpSkat = true
            gameAnnouncementStep = GameAnnouncementStep.LOOKED_INTO_SKAT
        } else if (JSkatAction.PLAY_HAND_GAME.toString() == command) {
            pickUpSkat = false
            gameAnnouncementStep = GameAnnouncementStep.PLAYS_HAND
        } else if (JSkatAction.SCHIEBEN.toString() == command) {
            if (source is CardList) {
                if (source.size() == 0) {
                    pickUpSkat = false
                } else {
                    pickUpSkat = true
                    discardSkat = CardList(source)
                }
            } else {
                Companion.log.warn("Wrong source {} for command {}", source, command)
                interrupt = false
            }
        } else if (JSkatAction.ANNOUNCE_GAME.toString() == command) {
            if (source is GameAnnouncement) {
                gameContract = source.contract

                if (gameContract!!.hand) {
                    gameAnnouncementStep = GameAnnouncementStep.PLAYS_HAND
                } else {
                    discardSkat = source.discardedCards
                    gameAnnouncementStep = GameAnnouncementStep.DISCARDED_SKAT
                }
            } else {
                Companion.log.warn("Wrong source for " + command)
                interrupt = false
            }
        } else if (JSkatAction.PLAY_CARD.toString() == command && source is Card) {
            nextCard = source
        } else {
            Companion.log.warn("Unknown action event occurred: " + command + " from " + source)
        }

        if (interrupt) {
            idler.interrupt()
        }
    }

    /**
     * Starts waiting for user input
     */
    fun waitForUserInput() {
        idler = Idler()
        idler.setMonitor(this)

        if (!this.isPlayerHasAlreadyPlayed) {
            idler.start()
            try {
                idler.join()
            } catch (e: InterruptedException) {
                Companion.log.warn("wait for user input was interrupted")
            }
        }
    }

    private val isPlayerHasAlreadyPlayed: Boolean
        get() {
            Companion.log.debug("Game announcement step: $gameAnnouncementStep")

            val result = GameAnnouncementStep.DISCARDED_SKAT == gameAnnouncementStep
                    || GameAnnouncementStep.PLAYS_HAND == gameAnnouncementStep

            return result
        }

    /*-------------------------------------------------------------------
     * Inner class
     *-------------------------------------------------------------------*/
    /**
     * Protected class implementing the waiting thread for user input
     */
    protected class Idler : Thread() {
        /**
         * Sets the monitoring object
         *
         * @param newMonitor Monitor
         */
        fun setMonitor(newMonitor: Any?) {
            monitor = newMonitor
        }

        /**
         * Stops the waiting
         */
        fun stopWaiting() {
            doWait = false
        }

        /**
         * @see Thread.run
         */
        override fun run() {
            synchronized(monitor!!) {
                while (doWait) {
                    try {
                        (monitor as Object).wait()
                    } catch (e: InterruptedException) {
                        stopWaiting()
                    }
                }
            }
        }

        private var doWait = true
        private var monitor: Any? = null
    }

    /**
     * @see org.jskat.player.AbstractJSkatPlayer.startGame
     */
    override fun startGame() {
        // TODO is there something todo?
    }

    private fun resetPlayer() {
        bidValue = 0
        holdBid = false
        playGrandHand = false
        callContra = false
        callRe = false
        gameAnnouncementStep = GameAnnouncementStep.BEFORE_ANNOUNCEMENT
        pickUpSkat = false
        discardSkat = CardList()
        gameContract = null
        nextCard = null
    }

    override fun callContra(): Boolean {
        Companion.log.debug("Waiting for human calling contra...")

        if (callContra == null) {
            waitForUserInput()
        }

        return callContra != null && callContra
    }

    override fun callRe(): Boolean {
        Companion.log.debug("Waiting for human calling re...")

        if (callRe == null) {
            waitForUserInput()
        }

        return callRe != null && callRe
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SwingHumanPlayer::class.java)
    }
}
