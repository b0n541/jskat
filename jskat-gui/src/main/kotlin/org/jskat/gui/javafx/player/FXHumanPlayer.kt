package org.jskat.gui.javafx.player

import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer
import org.jskat.data.GameAnnouncement
import org.jskat.data.GameContract
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.GameType
import org.slf4j.LoggerFactory

class FXHumanPlayer : AbstractHumanJSkatPlayer() {

    private val log = LoggerFactory.getLogger(FXHumanPlayer::class.java)
    private val discardedCards = CardList()

    override fun prepareForNewGame() {
        log.debug("prepareForNewGame")
    }

    override fun finalizeGame() {
        log.debug("finalizeGame")
    }

    override fun bidMore(nextBidValue: Int): Int {
        log.debug("bidMore: $nextBidValue")
        return 0
    }

    override fun holdBid(currBidValue: Int): Boolean {
        log.debug("holdBid: $currBidValue")
        return false
    }

    override fun playGrandHand(): Boolean {
        log.debug("playGrandHand")
        return false
    }

    override fun callContra(): Boolean {
        log.debug("callContra")
        return false
    }

    override fun callRe(): Boolean {
        log.debug("callRe")
        return false
    }

    override fun pickUpSkat(): Boolean {
        log.debug("pickUpSkat")
        return false
    }

    override fun announceGame(): GameContract {
        log.debug("announceGame")
        return GameContract(GameType.GRAND)
    }

    override fun playCard(): Card {
        log.debug("playCard")
        return Card.CJ
    }

    override fun getCardsToDiscard(): CardList {
        log.debug("getCardsToDiscard")
        return discardedCards
    }

    override fun startGame() {
        log.debug("startGame")
    }

    override fun actionPerformed(e: JSkatActionEvent) {
        log.debug("actionPerformed: $e")
        if (e.actionCommand == JSkatAction.PUT_CARD_INTO_SKAT.toString()) {
            val card = e.source as Card
            discardedCards.add(card)
        } else if (e.actionCommand == JSkatAction.PLAY_CARD.toString()) {
            log.debug("Card played: " + e.source)
        }
    }
}
