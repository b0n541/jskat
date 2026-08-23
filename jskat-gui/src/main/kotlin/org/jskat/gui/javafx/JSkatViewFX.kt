package org.jskat.gui.javafx

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.stage.Modality
import org.jskat.control.JSkatEventBus
import org.jskat.control.command.general.ShowAboutInformationCommand
import org.jskat.control.command.general.ShowHelpCommand
import org.jskat.control.command.general.ShowLicenseCommand
import org.jskat.control.command.general.ShowPreferencesCommand
import org.jskat.control.command.table.ShowCardsCommand
import org.jskat.control.event.skatgame.*
import org.jskat.control.event.table.*
import org.jskat.control.gui.JSkatView
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer
import org.jskat.data.JSkatOptions
import org.jskat.data.SkatGameData
import org.jskat.data.SkatGameData.GameState
import org.jskat.data.iss.MoveInformation
import org.jskat.data.iss.MoveType
import org.jskat.gui.javafx.dialog.help.JSkatHelpDialog
import org.jskat.gui.dialog.JSkatAboutDialog
import org.jskat.gui.javafx.dialog.options.JSkatOptionsDialog
import org.jskat.gui.javafx.iss.IssPlayerInvitationDialog
import org.jskat.gui.javafx.main.JSkatMainWindowFX
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.JSkatResourceBundle
import org.jskat.util.Player
import org.slf4j.LoggerFactory
import java.util.concurrent.FutureTask

class JSkatViewFX(
    val mainWindow: JSkatMainWindowFX,
    private val human: AbstractHumanJSkatPlayer,
    private val documentOpener: JavaFxHostDocumentOpener,
    private val applicationVersion: String
) : JSkatView {

    private val log = LoggerFactory.getLogger(JSkatViewFX::class.java)
    private val strings = JSkatResourceBundle.INSTANCE

    init {
        JSkatEventBus.INSTANCE.register(this)
    }

    @Subscribe
    fun showAboutDialogOn(command: ShowAboutInformationCommand) {
        Platform.runLater {
            JSkatAboutDialog(applicationVersion, mainWindow.scene?.window).showAndWait()
        }
    }

    @Subscribe
    fun showPreferencesDialogOn(command: ShowPreferencesCommand) {
        Platform.runLater {
            JSkatOptionsDialog(null).showAndWait()
        }
    }

    @Subscribe
    fun showHelpDialogOn(command: ShowHelpCommand) {
        Platform.runLater {
            JSkatHelpDialog(
                JSkatResourceBundle.INSTANCE.getString("help"),
                "org/jskat/gui/help/" + JSkatOptions.instance().i18NCode + "/contents.html",
                documentOpener
            ).showAndWait()
        }
    }

    @Subscribe
    fun showLicenceDialogOn(command: ShowLicenseCommand) {
        Platform.runLater {
            JSkatHelpDialog(
                JSkatResourceBundle.INSTANCE.getString("license"),
                "org/jskat/gui/help/apache2.html",
                documentOpener
            ).showAndWait()
        }
    }

    override fun getNewTableName(localTablesCreated: Int): String {
        // TODO: i18n
        return "Table " + (localTablesCreated + 1)
    }

    override fun getPlayerForInvitation(playerNames: Set<String>): List<String> {
        return runOnFxThread {
            IssPlayerInvitationDialog(playerNames).apply {
                initModality(Modality.APPLICATION_MODAL)
            }.showAndWait().orElse(emptyList())
        }
    }

    override fun showMessage(title: String, message: String) {
        Platform.runLater {
            Alert(Alert.AlertType.INFORMATION).apply {
                setTitle(title)
                headerText = null // or null
                contentText = message
                showAndWait()
            }
        }
    }

    override fun showErrorMessage(title: String, message: String) {
        Platform.runLater {
            Alert(Alert.AlertType.ERROR).apply {
                setTitle(title)
                headerText = null // or null
                contentText = message
                showAndWait()
            }
        }
    }

    @Subscribe
    fun showErrorMessageOn(event: InvalidNumberOfCardsInDiscardedSkatEvent) {
        showError(JavaFxFeedback.invalidNumberOfDiscardedCards())
    }

    @Subscribe
    fun showErrorMessageOn(event: CardNotAllowedToPlayEvent) {
        showErrorMessage(
            strings.getString("card_not_allowed_title"),
            strings.getString(
                "card_not_allowed_message",
                strings.getSuitStringForCardFace(event.card.suit),
                strings.getRankStringForCardFace(event.card.rank)
            )
        )
    }

    @Subscribe
    fun showErrorMessageOn(event: NoJacksAllowedInDiscardedSkatEvent) {
        showError(JavaFxFeedback.noJacksAllowedInDiscardedSkat())
    }

    @Subscribe
    fun showErrorMessageOn(event: DuplicateTableNameInputEvent) {
        showError(JavaFxFeedback.duplicateTableName(event.tableName))
    }

    @Subscribe
    fun showErrorMessageOn(event: EmptyTableNameInputEvent) {
        showError(JavaFxFeedback.emptyTableName())
    }

    override fun updateISSMove(tableName: String, gameData: SkatGameData, moveInformation: MoveInformation) {
        val movePlayer = moveInformation.player

        when (moveInformation.type) {
            MoveType.DEAL -> {
                JSkatEventBus.INSTANCE.post(SkatGameStateChangedEvent(tableName, GameState.DEALING))
                withTablePanel(tableName) { table -> Player.entries.forEach(table::hideCards) }

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
                // Player clocks are refreshed before the notification below.
            }

            MoveType.LEAVE_TABLE -> {
                log.debug("Player ${moveInformation.leavingPlayer} left ISS table $tableName")
            }
        }

        withTablePanel(tableName) { table ->
            IssMoveFeedback.clockUpdates(moveInformation).forEach { (player, time) ->
                table.setPlayerTime(player, time)
            }
        }
        if (moveInformation.type == MoveType.TIME_OUT) {
            showMessage(
                strings.getString("iss_timeout_title"),
                strings.getString(
                    "iss_timeout_message",
                    IssMoveFeedback.timedOutPlayerName(gameData, moveInformation)
                )
            )
        }
    }

    override fun setResign(tableName: String, player: Player) {
        withTablePanel(tableName) { it.setResign(player) }
    }

    override fun showISSTableInvitation(invitor: String, tableName: String): Boolean {
        return IssMoveFeedback.invitationAccepted(runOnFxThread {
            Alert(Alert.AlertType.CONFIRMATION).apply {
                title = strings.getString("iss_table_invitation_title")
                headerText = null
                contentText = strings.getString("iss_table_invitation", invitor, tableName)
            }.showAndWait()
        })
    }

    override fun setGeschoben(tableName: String, player: Player) {
        withTablePanel(tableName) { it.setGeschoben(player) }
    }

    override fun openWebPage(link: String) {
        documentOpener.open(link)
    }

    override fun getHumanPlayerForGUI(): AbstractHumanJSkatPlayer {
        return human
    }

    override fun showAIPlayedSchwarzMessageDiscarding(playerName: String, discardedCards: CardList) {
        showMessage(JavaFxFeedback.schwarzDiscarding(playerName, discardedCards))
    }

    override fun showAIPlayedSchwarzMessageCardPlay(playerName: String, card: Card) {
        showMessage(JavaFxFeedback.schwarzCardPlay(playerName, card))
    }

    override fun setSkat(tableName: String?, skat: CardList?) {
        if (tableName != null && skat != null) {
            withTablePanel(tableName) { it.setSkat(skat) }
        }
    }

    private fun withTablePanel(tableName: String, action: (org.jskat.gui.javafx.table.SkatTablePanel) -> Unit) {
        if (Platform.isFxApplicationThread()) {
            mainWindow.tablePanel(tableName)?.let(action)
        } else {
            Platform.runLater { mainWindow.tablePanel(tableName)?.let(action) }
        }
    }

    private fun showError(feedback: JavaFxFeedback) {
        showErrorMessage(feedback.title, feedback.message)
    }

    private fun showMessage(feedback: JavaFxFeedback) {
        showMessage(feedback.title, feedback.message)
    }

    private fun <T> runOnFxThread(action: () -> T): T =
        if (Platform.isFxApplicationThread()) {
            action()
        } else {
            val task = FutureTask(action)
            Platform.runLater(task)
            task.get()
        }
}
