package org.jskat.gui.javafx.table

import com.google.common.eventbus.EventBus
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.JSkatEventBus
import org.jskat.control.event.skatgame.DiscardSkatEvent
import org.jskat.control.event.skatgame.GameStartedEvent
import org.jskat.control.event.skatgame.PickUpSkatEvent
import org.jskat.control.event.table.SkatGameReplayFinishedEvent
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
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.GameVariant
import org.jskat.util.Player
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

    @Test
    fun `local bidding keeps controls in the shared action area and dispatches them`() {
        val makeBid = RecordingAction("Make bid")
        val holdBid = RecordingAction("Hold bid")
        val passBid = RecordingAction("Pass bid")
        val tableName = "Local-42"
        val tableEvents = EventBus("Table $tableName")
        val panel = onFxThread {
            JSkatEventBus.TABLE_EVENT_BUSSES[tableName] = tableEvents
            SkatTablePanel(
                tableName,
                mapOf(
                    JSkatAction.START_LOCAL_SERIES to StartSkatSeriesAction(),
                    JSkatAction.MAKE_BID to makeBid,
                    JSkatAction.HOLD_BID to holdBid,
                    JSkatAction.PASS_BID to passBid,
                ),
            ).also(::Scene)
        }

        try {
            tableEvents.post(SkatGameStateChangedEvent(tableName, GameState.BIDDING))
            flushFxEvents()

            val biddingButtons = sharedActionButtons(panel)
            assertThat(biddingButtons.map(Button::getText)).containsExactly("Make bid", "Pass bid")
            assertThat(contextButtons(panel).map(Button::getText)).doesNotContain("Make bid", "Pass bid")

            onFxThread { biddingButtons.forEach(Button::fire) }
            flushFxEvents()

            assertThat(makeBid.actionCommands).containsExactly(JSkatAction.MAKE_BID.toString())
            assertThat(passBid.actionCommands).containsExactly(JSkatAction.PASS_BID.toString())
            assertThat(holdBid.actionCommands).isEmpty()
        } finally {
            JSkatEventBus.TABLE_EVENT_BUSSES.remove(tableName)
        }
    }

    @Test
    fun `replay skat is inert, shown only during trick play, and cleared when replay ends`() {
        val takeCard = RecordingAction("Take card")
        val tableName = "Replay-skat"
        val tableEvents = EventBus("Table $tableName")
        val panel = onFxThread {
            JSkatEventBus.TABLE_EVENT_BUSSES[tableName] = tableEvents
            SkatTablePanel(
                tableName,
                mapOf(
                    JSkatAction.START_LOCAL_SERIES to StartSkatSeriesAction(),
                    JSkatAction.TAKE_CARD_FROM_SKAT to takeCard,
                ),
            ).also(::Scene)
        }

        try {
            tableEvents.post(GameStartedEvent(1, GameVariant.STANDARD, Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND))
            flushFxEvents()
            tableEvents.post(SkatGameReplayStartedEvent())
            flushFxEvents()
            tableEvents.post(SkatGameStateChangedEvent(tableName, GameState.PICKING_UP_SKAT))
            onFxThread { Unit }
            tableEvents.post(PickUpSkatEvent(Player.FOREHAND, CardList(Card.C9, Card.S9)))
            onFxThread { Unit }
            assertThat(discardCards(panel).children.filterIsInstance<ImageView>()).hasSize(2)
            tableEvents.post(SkatGameStateChangedEvent(tableName, GameState.DISCARDING))
            onFxThread { Unit }
            tableEvents.post(DiscardSkatEvent(Player.FOREHAND, CardList(Card.H9, Card.D9)))
            onFxThread { Unit }

            assertThat(replaySkatSlot(panel).children).isEmpty()

            tableEvents.post(SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING))
            onFxThread { Unit }

            val replaySkat = replaySkatSlot(panel)
            val cards = discardCards(panel)
            assertThat(cards.children.filterIsInstance<ImageView>()).hasSize(2)
            onFxThread {
                cards.children.filterIsInstance<ImageView>().first().fireEvent(
                    MouseEvent(MouseEvent.MOUSE_CLICKED, 0.0, 0.0, 0.0, 0.0, javafx.scene.input.MouseButton.PRIMARY, 1,
                        false, false, false, false, true, false, false, true, false, false, null)
                )
            }
            assertThat(takeCard.actionCommands).isEmpty()

            tableEvents.post(SkatGameReplayFinishedEvent())
            onFxThread { Unit }
            assertThat(replaySkatSlot(panel).children).isEmpty()
            assertThat(discardPanel(panel).children).isEmpty()

            tableEvents.post(SkatGameReplayStartedEvent())
            flushFxEvents()
            tableEvents.post(PickUpSkatEvent(Player.FOREHAND, CardList(Card.C9, Card.S9)))
            tableEvents.post(DiscardSkatEvent(Player.FOREHAND, CardList(Card.H9, Card.D9)))
            tableEvents.post(SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING))
            onFxThread { Unit }
            assertThat(replaySkatSlot(panel).children).isNotEmpty()

            tableEvents.post(GameStartedEvent(2, GameVariant.STANDARD, Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND))
            onFxThread { Unit }
            assertThat(replaySkatSlot(panel).children).isEmpty()
            assertThat(discardPanel(panel).children).isEmpty()
        } finally {
            JSkatEventBus.TABLE_EVENT_BUSSES.remove(tableName)
        }
    }

    private fun sharedActionButtons(panel: SkatTablePanel): List<Button> = onFxThread {
        (panel.lookup("#shared-action-area") as javafx.scene.layout.HBox).children.filterIsInstance<Button>()
    }

    private fun replaySkatSlot(panel: SkatTablePanel): StackPane = onFxThread {
        panel.lookup("#shared-lower-left") as StackPane
    }

    private fun discardCards(panel: SkatTablePanel): HBox = onFxThread {
        panel.lookup("#discard-card-views") as HBox
    }

    private fun discardPanel(panel: SkatTablePanel): StackPane = onFxThread {
        panel.lookup("#discard-panel") as StackPane
    }

    private fun contextButtons(panel: SkatTablePanel): Set<Button> = onFxThread {
        (panel.lookup("#context-panel-stack") as StackPane).lookupAll(".button").filterIsInstance<Button>().toSet()
    }

    private fun flushFxEvents() {
        onFxThread { Unit }
        onFxThread { Unit }
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
