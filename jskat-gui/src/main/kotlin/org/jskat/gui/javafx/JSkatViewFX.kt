package org.jskat.gui.javafx

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.stage.Modality
import org.jskat.control.JSkatEventBus
import org.jskat.control.command.general.ShowHelpCommand
import org.jskat.control.command.general.ShowLicenseCommand
import org.jskat.control.command.general.ShowPreferencesCommand
import org.jskat.control.command.table.ShowCardsCommand
import org.jskat.control.command.table.StartSkatSeriesCommand
import org.jskat.control.event.skatgame.*
import org.jskat.control.event.table.*
import org.jskat.control.gui.JSkatView
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer
import org.jskat.control.iss.ChatMessageType
import org.jskat.data.JSkatOptions
import org.jskat.data.SkatGameData
import org.jskat.data.SkatGameData.GameState
import org.jskat.data.iss.ChatMessage
import org.jskat.data.iss.MoveInformation
import org.jskat.data.iss.MoveType
import org.jskat.gui.javafx.dialog.help.JSkatHelpDialog
import org.jskat.gui.javafx.dialog.options.JSkatOptionsDialog
import org.jskat.gui.javafx.iss.PlayerInvitationDialog
import org.jskat.gui.javafx.main.JSkatMainWindowFX
import org.jskat.gui.javafx.table.SkatSeriesStartDialog
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.JSkatResourceBundle
import org.jskat.util.Player
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.FutureTask

class JSkatViewFX(
    val mainWindow: JSkatMainWindowFX,
    private val human: AbstractHumanJSkatPlayer
) : JSkatView {

    private val log = LoggerFactory.getLogger(JSkatViewFX::class.java)

    init {
        JSkatEventBus.INSTANCE.register(this)
    }

    @Subscribe
    fun showPreferencesDialogOn(command: ShowPreferencesCommand) {
        Platform.runLater { JSkatOptionsDialog(null).showAndWait() }
    }

    @Subscribe
    fun showHelpDialogOn(command: ShowHelpCommand) {
        Platform.runLater {
            JSkatHelpDialog(
                JSkatResourceBundle.INSTANCE.getString("help"),
                "org/jskat/gui/help/" + JSkatOptions.instance().i18NCode + "/contents.html"
            ).showAndWait()
        }
    }

    @Subscribe
    fun showLicenceDialogOn(command: ShowLicenseCommand) {
        Platform.runLater {
            JSkatHelpDialog(
                JSkatResourceBundle.INSTANCE.getString("license"),
                "org/jskat/gui/help/apache2.html"
            ).showAndWait()
        }
    }

    @Subscribe
    fun showSkatSeriesStartDialogOn(command: StartSkatSeriesCommand) {
        Platform.runLater {
            val dialog = SkatSeriesStartDialog(null)
            dialog.showAndWaitAndStartSeries()
        }
    }

    override fun getNewTableName(localTablesCreated: Int): String {
        return "Table " + (localTablesCreated + 1)
    }

    override fun startGame(tableName: String) {
        log.debug("startGame: $tableName")
    }

    override fun getPlayerForInvitation(playerNames: Set<String>): List<String> {
        val result = mutableListOf<String>()

        // TODO: there should only be one case
        if (Platform.isFxApplicationThread()) {
            try {
                val dialog = PlayerInvitationDialog(playerNames)
                dialog.initModality(Modality.APPLICATION_MODAL)
                val dialogResult = dialog.showAndWait()
                if (dialogResult.isPresent) {
                    result.addAll(dialogResult.get())
                }
            } catch (e: Throwable) {
                log.error("Error showing invitation dialog", e)
            }
        } else {
            val task = FutureTask {
                val dialog = PlayerInvitationDialog(playerNames)
                dialog.initModality(Modality.APPLICATION_MODAL)
                val dialogResult = dialog.showAndWait()
                dialogResult.orElse(Collections.emptyList())
            }
            Platform.runLater(task)
            try {
                result.addAll(task.get())
            } catch (e: Exception) {
                log.error("Error showing invitation dialog", e)
            }
        }
        return result
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
        val movePlayer = moveInformation.player

        when (moveInformation.type) {
            MoveType.DEAL -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.DEALING))

                val dealtCards = mutableMapOf<Player, CardList>()
                dealtCards[Player.FOREHAND] = moveInformation.getCards(Player.FOREHAND)
                dealtCards[Player.MIDDLEHAND] = moveInformation.getCards(Player.MIDDLEHAND)
                dealtCards[Player.REARHAND] = moveInformation.getCards(Player.REARHAND)

                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)?.post(CardDealEvent(dealtCards, CardList()))
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.BIDDING))
            }

            MoveType.BID -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.BIDDING))
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)?.post(BidEvent(movePlayer, moveInformation.bidValue))
            }

            MoveType.HOLD_BID -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.BIDDING))
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)?.post(HoldBidEvent(movePlayer, gameData.maxBidValue))
            }

            MoveType.PASS -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.BIDDING))
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)?.post(PassBidEvent(movePlayer, gameData.nextBidValue))
            }

            MoveType.SKAT_REQUEST -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.PICKING_UP_SKAT))
            }

            MoveType.PICK_UP_SKAT -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.DISCARDING))
                if (moveInformation.skat.size() == 2) {
                    JSkatEventBus.INSTANCE.post(SkatCardsPickedUpEvent(tableName, moveInformation.skat))
                }
            }

            MoveType.GAME_ANNOUNCEMENT -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.DECLARING))
                JSkatEventBus.INSTANCE.post(
                    TableGameMoveEvent(
                        tableName,
                        GameAnnouncementEvent(movePlayer, moveInformation.gameAnnouncement)
                    )
                )
                if (!moveInformation.gameAnnouncement.contract().hand()) {
                    JSkatEventBus.INSTANCE.post(
                        SkatCardsChangedEvent(
                            tableName,
                            moveInformation.gameAnnouncement.discardedCards()
                        )
                    )
                }
                if (moveInformation.gameAnnouncement.contract().ouvert()) {
                    JSkatEventBus.INSTANCE.post(
                        ShowCardsCommand(
                            tableName,
                            movePlayer,
                            moveInformation.gameAnnouncement.contract().ouvertCards()
                        )
                    )
                }
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING))
            }

            MoveType.CARD_PLAY -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING))
                if (gameData.tricks.size > 1) {
                    val currentTrick = gameData.currentTrick
                    val lastTrick = gameData.lastCompletedTrick
                    if (currentTrick.firstCard != null && currentTrick.secondCard == null && currentTrick.thirdCard == null) {
                        JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)?.post(TrickCompletedEvent(lastTrick))
                    }
                }
                JSkatEventBus.INSTANCE.post(
                    TableGameMoveEvent(
                        tableName,
                        TrickCardPlayedEvent(movePlayer, moveInformation.card)
                    )
                )
            }

            MoveType.SHOW_CARDS -> {
                JSkatEventBus.INSTANCE.post(ShowCardsCommand(tableName, movePlayer, moveInformation.revealedCards))
            }

            MoveType.RESIGN -> {
                setResign(tableName, movePlayer)
            }

            MoveType.TIME_OUT -> {
                // TODO show message box
            }

            else -> {
                log.warn("Unknown move type: ${moveInformation.type}")
            }
        }
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
