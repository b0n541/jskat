package org.jskat.gui.javafx.table

import com.google.common.eventbus.EventBus
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Button
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.JSkatEventBus
import org.jskat.control.event.table.SkatGameReplayStartedEvent
import org.jskat.control.event.table.SkatGameStateChangedEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.data.SkatGameData.GameState
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.main.StartSkatSeriesAction
import org.jskat.gui.javafx.JavaFxTestSupport
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SkatTablePanelReplayNavigationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `replay navigation replaces normal actions throughout replay and at completion`() {
        val reset = RecordingAction("Reset to Start")
        val nextMove = RecordingAction("Next Move")
        val continueSeries = RecordingAction("Continue Skat Series")
        val tableName = "Replay-42"
        val tableEvents = EventBus("Table $tableName")
        val panel = onFxThread {
            JSkatEventBus.TABLE_EVENT_BUSSES[tableName] = tableEvents
            SkatTablePanel(
                tableName,
                mapOf(
                    JSkatAction.START_LOCAL_SERIES to StartSkatSeriesAction(),
                    JSkatAction.REPLAY_GAME to reset,
                    JSkatAction.NEXT_REPLAY_STEP to nextMove,
                    JSkatAction.CONTINUE_LOCAL_SERIES to continueSeries,
                ),
            ).also(::Scene)
        }

        try {
            tableEvents.post(SkatGameStateChangedEvent(tableName, GameState.GAME_OVER))
            onFxThread { Unit }
            tableEvents.post(SkatGameReplayStartedEvent())
            onFxThread { Unit }

            val activeReplayButtons = sharedActionButtons(panel)
            assertThat(activeReplayButtons.map(Button::getText))
                .containsExactly("Reset to Start", "Next Move")
            onFxThread { activeReplayButtons.forEach(Button::fire) }
            assertThat(reset.actionCommands).containsExactly("Replay-42")
            assertThat(nextMove.actionCommands).containsExactly("Replay-42")

            tableEvents.post(SkatGameStateChangedEvent(tableName, GameState.GAME_OVER))
            onFxThread { Unit }

            val completedReplayButtons = sharedActionButtons(panel)
            assertThat(completedReplayButtons.map(Button::getText))
                .containsExactly("Reset to Start", "Continue Skat Series")
            onFxThread { completedReplayButtons.forEach(Button::fire) }
            assertThat(reset.actionCommands).containsExactly("Replay-42", "Replay-42")
            assertThat(continueSeries.actionCommands).containsExactly("Replay-42")
        } finally {
            JSkatEventBus.TABLE_EVENT_BUSSES.remove(tableName)
        }
    }

    private fun sharedActionButtons(panel: SkatTablePanel): List<Button> = onFxThread {
        (panel.lookup("#shared-action-area") as javafx.scene.layout.HBox).children.filterIsInstance<Button>()
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
        val actionCommands = mutableListOf<String>()

        init {
            putValue(NAME, name)
        }

        override fun actionPerformed(event: JSkatActionEvent) {
            actionCommands += event.actionCommand
        }
    }
}
