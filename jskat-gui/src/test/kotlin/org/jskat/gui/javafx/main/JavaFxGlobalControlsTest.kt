package org.jskat.gui.javafx.main

import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.command.general.HideToolbarCommand
import org.jskat.control.command.general.ShowToolbarCommand
import org.jskat.control.event.table.SkatGameStateChangedEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.data.SkatGameData.GameState
import org.jskat.gui.action.AbstractJSkatAction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class JavaFxGlobalControlsTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
        }
    }

    @Test
    fun `applies action availability only for the active table`() {
        val actions = actionsForGameState()

        val changed = JavaFxGlobalControls.applyGameState(
            activeTableName = "Table 1",
            event = SkatGameStateChangedEvent("Table 2", GameState.GAME_OVER),
            playContra = false,
            actions = actions
        )

        assertThat(changed).isFalse()
        assertThat(actions.getValue(JSkatAction.REPLAY_GAME).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.NEXT_REPLAY_STEP).isEnabled).isFalse()
    }

    @Test
    fun `restores toolbar visibility when the hide toolbar command is reversed`() {
        val toolbar = JavaFxGlobalControls.ToolbarVisibility()

        toolbar.hide(HideToolbarCommand())
        assertThat(toolbar.isVisible).isFalse()

        toolbar.show(ShowToolbarCommand())
        assertThat(toolbar.isVisible).isTrue()
    }

    @Test
    fun `matches legacy action availability for each global game state`() {
        val actions = actionsForGameState()

        applyGameState(actions, GameState.GAME_START)
        assertThat(actions.getValue(JSkatAction.START_LOCAL_SERIES).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.REPLAY_GAME).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.NEXT_REPLAY_STEP).isEnabled).isFalse()

        applyGameState(actions, GameState.BIDDING)
        assertThat(actions.getValue(JSkatAction.ANNOUNCE_GAME).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.MAKE_BID).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.HOLD_BID).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.PASS_BID).isEnabled).isTrue()

        applyGameState(actions, GameState.DISCARDING)
        assertThat(actions.getValue(JSkatAction.ANNOUNCE_GAME).isEnabled).isTrue()

        applyGameState(actions, GameState.RAMSCH_GRAND_HAND_ANNOUNCING)
        assertThat(actions.getValue(JSkatAction.PLAY_GRAND_HAND).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.PLAY_SCHIEBERAMSCH).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.SCHIEBEN).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.PICK_UP_SKAT).isEnabled).isFalse()

        applyGameState(actions, GameState.SCHIEBERAMSCH)
        assertThat(actions.getValue(JSkatAction.PLAY_GRAND_HAND).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.PLAY_SCHIEBERAMSCH).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.SCHIEBEN).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.PICK_UP_SKAT).isEnabled).isTrue()

        applyGameState(actions, GameState.PICKING_UP_SKAT)
        assertThat(actions.getValue(JSkatAction.MAKE_BID).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.HOLD_BID).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.PASS_BID).isEnabled).isFalse()
        assertThat(actions.getValue(JSkatAction.PICK_UP_SKAT).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.ANNOUNCE_GAME).isEnabled).isTrue()

        applyGameState(actions, GameState.TRICK_PLAYING, playContra = true)
        assertThat(actions.getValue(JSkatAction.CALL_CONTRA).isEnabled).isTrue()

        applyGameState(actions, GameState.TRICK_PLAYING, playContra = false)
        assertThat(actions.getValue(JSkatAction.CALL_CONTRA).isEnabled).isFalse()

        applyGameState(actions, GameState.GAME_OVER)
        assertThat(actions.getValue(JSkatAction.CONTINUE_LOCAL_SERIES).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.REPLAY_GAME).isEnabled).isTrue()
        assertThat(actions.getValue(JSkatAction.NEXT_REPLAY_STEP).isEnabled).isTrue()
    }

    private fun actionsForGameState(): MutableMap<JSkatAction, AbstractJSkatAction> =
        JSkatAction.entries.associateWith { TestAction() as AbstractJSkatAction }.toMutableMap().apply {
            values.forEach { it.isEnabled = false }
        }

    private fun applyGameState(
        actions: Map<JSkatAction, AbstractJSkatAction>,
        state: GameState,
        playContra: Boolean = false
    ) {
        JavaFxGlobalControls.applyGameState(
            activeTableName = "Table",
            event = SkatGameStateChangedEvent("Table", state),
            playContra = playContra,
            actions = actions
        )
    }

    private class TestAction : AbstractJSkatAction() {
        override fun actionPerformed(event: JSkatActionEvent?) = Unit
    }
}
