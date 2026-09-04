package org.jskat.gui.javafx.iss

import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Button
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository.Icon
import org.jskat.gui.javafx.table.GameOverPanel
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
            val toolkitStarted = CountDownLatch(1)
            try {
                Platform.startup(toolkitStarted::countDown)
                check(toolkitStarted.await(1, TimeUnit.SECONDS))
            } catch (_: IllegalStateException) {
                // The JavaFX toolkit was initialized by another test class.
            }
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
