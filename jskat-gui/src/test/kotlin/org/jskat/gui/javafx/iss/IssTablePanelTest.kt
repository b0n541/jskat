package org.jskat.gui.javafx.iss

import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Button
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.event.iss.IssTableGameStartedEvent
import org.jskat.control.event.iss.IssTableStateChangedEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatApplicationData
import org.jskat.data.JSkatOptions
import org.jskat.data.iss.GameStartInformation
import org.jskat.data.iss.PlayerStatus
import org.jskat.data.iss.TablePanelStatus
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.main.StartSkatSeriesAction
import org.jskat.gui.img.JSkatGraphicRepository.Icon
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.table.AbstractHandPanel
import org.jskat.gui.javafx.table.GameOverPanel
import org.jskat.gui.javafx.table.ScoreHistoryPlayerOrder
import org.jskat.gui.javafx.table.SkatTableNode
import org.jskat.util.Player
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class IssTablePanelTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `ISS game actions use the table name`() {
        val resign = RecordingAction("Give up")
        val showCards = RecordingAction("Show cards")

        performIssTableAction(resign, JSkatAction.RESIGN, "ISS-42")
        performIssTableAction(showCards, JSkatAction.SHOW_CARDS, "ISS-42")

        assertThat(resign.source).isEqualTo("ISS-42")
        assertThat(showCards.source).isEqualTo("ISS-42")
    }

    @Test
    fun `ISS game actions provide big-button icons`() {
        assertThat(org.jskat.gui.action.iss.ResignAction().icon).isEqualTo(Icon.WHITE_FLAG)
        assertThat(org.jskat.gui.action.iss.ShowCardsAction().icon).isEqualTo(Icon.PLAY)
    }

    @Test
    fun `ISS game-over continuation uses the ready action`() {
        val localContinuation = RecordingAction("Continue local series")
        val ready = RecordingAction("Ready")

        val button = onFxThread {
            val panel = GameOverPanel(
                "ISS-42",
                mapOf(
                    JSkatAction.CONTINUE_LOCAL_SERIES to localContinuation,
                    JSkatAction.READY_TO_PLAY to ready
                ),
                showReplayGameButton = false,
                continueAction = JSkatAction.READY_TO_PLAY
            )
            Scene(panel)
            (panel.children[1] as javafx.scene.layout.HBox).children.filterIsInstance<Button>().single()
        }

        onFxThread { button.fire() }

        assertThat(ready.source).isEqualTo("ISS-42")
        assertThat(localContinuation.source).isNull()
    }

    @Test
    fun `ISS game-over leave-table action uses the table name`() {
        val leaveTable = RecordingAction("Leave table")

        val button = onFxThread {
            val panel = GameOverPanel(
                "ISS-42",
                mapOf(
                    JSkatAction.READY_TO_PLAY to RecordingAction("Ready"),
                    JSkatAction.LEAVE_ISS_TABLE to leaveTable
                ),
                showReplayGameButton = false,
                continueAction = JSkatAction.READY_TO_PLAY,
                additionalAction = JSkatAction.LEAVE_ISS_TABLE
            )
            Scene(panel)
            (panel.children[1] as javafx.scene.layout.HBox).children
                .filterIsInstance<Button>()
                .single { it.text == "Leave table" }
        }

        onFxThread { button.fire() }

        assertThat(leaveTable.source).isEqualTo("ISS-42")
    }

    @Test
    fun `ISS start-context actions have the same width`() {
        val buttons = onFxThread {
            val panel = IssStartContextPanel(
                "ISS-42",
                mapOf(
                    JSkatAction.INVITE_ISS_PLAYER to RecordingAction("Invite player"),
                    JSkatAction.READY_TO_PLAY to RecordingAction("Ready")
                ),
                listOf(JSkatAction.INVITE_ISS_PLAYER, JSkatAction.READY_TO_PLAY)
            )
            Scene(panel)
            panel.children.filterIsInstance<Button>()
        }

        onFxThread { Unit }

        assertThat(buttons).hasSize(2)
        assertThat(buttons.map(Button::getPrefWidth)).containsOnly(buttons.first().prefWidth)
        assertThat(buttons.map(Button::getMaxWidth)).containsOnly(buttons.first().maxWidth)
    }

    @Test
    fun `ISS table state keeps the signed-in player in the user panel`() {
        val status = TablePanelStatus().apply {
            addPlayer("Alice", PlayerStatus())
            addPlayer("Me", PlayerStatus())
            addPlayer("Zoe", PlayerStatus())
        }

        val panel = onFxThread {
            JSkatApplicationData.INSTANCE.setIssUserName("Me")
            IssTablePanel(
                "ISS-42",
                mapOf(JSkatAction.START_LOCAL_SERIES to StartSkatSeriesAction())
            ).also {
                it.updateTableStatusOn(IssTableStateChangedEvent("ISS-42", status))
            }
        }

        onFxThread { Unit }

        assertThat(handPanelNames(panel)).containsExactlyInAnyOrder("Alice", "Me", "Zoe")
        assertThat(handPanelNames(panel).last()).isEqualTo("Me")
    }

    @Test
    fun `ISS game start initializes the score history player order`() {
        val node = onFxThread {
            JSkatApplicationData.INSTANCE.setIssUserName("Me")
            SkatTableNode(
                IssTablePanel(
                    "ISS-score-history",
                    mapOf(JSkatAction.START_LOCAL_SERIES to StartSkatSeriesAction()),
                ),
            )
        }

        node.setPlayerOrderOn(
            IssTableGameStartedEvent(
                "ISS-score-history",
                GameStartInformation(
                    "Me",
                    7,
                    mapOf(Player.FOREHAND to "Alice", Player.MIDDLEHAND to "Me", Player.REARHAND to "Zoe"),
                    mapOf(Player.FOREHAND to 0.0, Player.MIDDLEHAND to 0.0, Player.REARHAND to 0.0),
                ),
            ),
        )

        assertThat(playerOrderOf(node)).isEqualTo(
            ScoreHistoryPlayerOrder(Player.REARHAND, Player.FOREHAND, Player.MIDDLEHAND),
        )
    }

    private fun handPanelNames(panel: IssTablePanel): List<String?> {
        val fields = listOf("leftOpponentPanel", "rightOpponentPanel", "userPanel")
        return fields.map { fieldName ->
            val field = panel.javaClass.superclass.getDeclaredField(fieldName)
            field.isAccessible = true
            (field.get(panel) as AbstractHandPanel).playerName
        }
    }

    private fun playerOrderOf(node: SkatTableNode): ScoreHistoryPlayerOrder {
        val playerOrder = node.javaClass.getDeclaredField("playerOrder")
        playerOrder.isAccessible = true
        return playerOrder.get(node) as ScoreHistoryPlayerOrder
    }

    private fun <T> onFxThread(action: () -> T): T {
        val result = arrayOfNulls<Any>(1)
        val failure = arrayOfNulls<Throwable>(1)
        val completed = CountDownLatch(1)
        Platform.runLater {
            try {
                result[0] = action()
            } catch (error: Throwable) {
                failure[0] = error
            } finally {
                completed.countDown()
            }
        }
        check(completed.await(1, TimeUnit.SECONDS))
        failure[0]?.let { throw it }
        @Suppress("UNCHECKED_CAST")
        return result[0] as T
    }

    private class RecordingAction(name: String) : AbstractJSkatAction() {
        var source: Any? = null

        init {
            putValue(NAME, name)
        }

        override fun actionPerformed(event: JSkatActionEvent) {
            source = event.source
        }
    }

}
