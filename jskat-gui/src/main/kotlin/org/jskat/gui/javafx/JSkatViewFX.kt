package org.jskat.gui.javafx

import org.jskat.control.gui.JSkatView
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer
import org.jskat.control.iss.ChatMessageType
import org.jskat.data.SkatGameData
import org.jskat.data.iss.ChatMessage
import org.jskat.data.iss.MoveInformation
import org.jskat.gui.javafx.main.JSkatMainWindowFX
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.Player
import org.slf4j.LoggerFactory

class JSkatViewFX(
    val mainWindow: JSkatMainWindowFX,
    private val human: AbstractHumanJSkatPlayer
) : JSkatView {

    private val log = LoggerFactory.getLogger(JSkatViewFX::class.java)

    override fun getNewTableName(localTablesCreated: Int): String {
        return "Table " + (localTablesCreated + 1)
    }

    override fun startGame(tableName: String) {
        log.debug("startGame: $tableName")
    }

    override fun getPlayerForInvitation(playerNames: Set<String>): List<String> {
        return emptyList()
    }

    override fun showMessage(title: String, message: String) {
        log.debug("showMessage: $title, $message")
    }

    override fun showErrorMessage(title: String, message: String) {
        log.debug("showErrorMessage: $title, $message")
    }

    override fun showCardNotAllowedMessage(card: Card) {
        log.debug("showCardNotAllowedMessage: $card")
    }

    override fun appendISSChatMessage(messageType: ChatMessageType, message: ChatMessage) {
        log.debug("appendISSChatMessage: $messageType, $message")
    }

    override fun updateISSMove(tableName: String, gameData: SkatGameData, moveInformation: MoveInformation) {
        log.debug("updateISSMove: $tableName, $gameData, $moveInformation")
    }

    override fun setResign(tableName: String, player: Player) {
        log.debug("setResign: $tableName, $player")
    }

    override fun showISSTableInvitation(invitor: String, tableName: String): Boolean {
        log.debug("showISSTableInvitation: $invitor, $tableName")
        return false
    }

    override fun setGeschoben(tableName: String, player: Player) {
        log.debug("setGeschoben: $tableName, $player")
    }

    override fun setDiscardedSkat(
        tableName: String,
        activePlayer: Player,
        skatBefore: CardList,
        discardedSkat: CardList
    ) {
        log.debug("setDiscardedSkat: $tableName, $activePlayer, $skatBefore, $discardedSkat")
    }

    override fun openWebPage(link: String) {
        log.debug("openWebPage: $link")
    }

    override fun getHumanPlayerForGUI(): AbstractHumanJSkatPlayer {
        return human
    }

    override fun setActiveView(name: String) {
        mainWindow.setActiveView(name)
    }

    override fun showAIPlayedSchwarzMessageDiscarding(playerName: String, discardedCards: CardList) {
        log.debug("showAIPlayedSchwarzMessageDiscarding: $playerName, $discardedCards")
    }

    override fun showAIPlayedSchwarzMessageCardPlay(playerName: String, card: Card) {
        log.debug("showAIPlayedSchwarzMessageCardPlay: $playerName, $card")
    }

    override fun setSkat(tableName: String?, skat: CardList?) {
        TODO("Not yet implemented")
    }
}
