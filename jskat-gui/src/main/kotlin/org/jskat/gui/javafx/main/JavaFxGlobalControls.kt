package org.jskat.gui.javafx.main

import org.jskat.control.command.general.HideToolbarCommand
import org.jskat.control.command.general.ShowToolbarCommand
import org.jskat.control.event.table.SkatGameStateChangedEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.SkatGameData.GameState
import org.jskat.gui.action.AbstractJSkatAction

/** Applies the legacy global-action contract to the JavaFX controls. */
object JavaFxGlobalControls {
    class ToolbarVisibility {
        var isVisible: Boolean = true
            private set

        fun hide(command: HideToolbarCommand) {
            isVisible = false
        }

        fun show(command: ShowToolbarCommand) {
            isVisible = true
        }
    }

    fun applyGameState(
        activeTableName: String?,
        event: SkatGameStateChangedEvent,
        playContra: Boolean,
        actions: Map<JSkatAction, AbstractJSkatAction>
    ): Boolean = applyGameState(activeTableName, event.tableName, event.gameState, playContra, actions)

    fun applyGameState(
        activeTableName: String?,
        eventTableName: String,
        state: GameState,
        playContra: Boolean,
        actions: Map<JSkatAction, AbstractJSkatAction>
    ): Boolean {
        if (activeTableName != eventTableName) {
            return false
        }

        when (state) {
            GameState.GAME_START -> {
                enable(actions, JSkatAction.START_LOCAL_SERIES)
                disable(actions, JSkatAction.REPLAY_GAME, JSkatAction.NEXT_REPLAY_STEP)
            }

            GameState.BIDDING -> {
                disable(actions, JSkatAction.ANNOUNCE_GAME)
                enable(actions, JSkatAction.MAKE_BID, JSkatAction.HOLD_BID, JSkatAction.PASS_BID)
            }

            GameState.DISCARDING -> enable(actions, JSkatAction.ANNOUNCE_GAME)

            GameState.RAMSCH_GRAND_HAND_ANNOUNCING -> {
                enable(actions, JSkatAction.PLAY_GRAND_HAND, JSkatAction.PLAY_SCHIEBERAMSCH)
                disable(actions, JSkatAction.SCHIEBEN, JSkatAction.PICK_UP_SKAT)
            }

            GameState.SCHIEBERAMSCH -> {
                disable(actions, JSkatAction.PLAY_GRAND_HAND, JSkatAction.PLAY_SCHIEBERAMSCH)
                enable(actions, JSkatAction.SCHIEBEN, JSkatAction.PICK_UP_SKAT)
            }

            GameState.PICKING_UP_SKAT -> {
                disable(actions, JSkatAction.MAKE_BID, JSkatAction.HOLD_BID, JSkatAction.PASS_BID)
                enable(actions, JSkatAction.PICK_UP_SKAT, JSkatAction.ANNOUNCE_GAME)
            }

            GameState.TRICK_PLAYING -> actions[JSkatAction.CALL_CONTRA]?.isEnabled = playContra

            GameState.GAME_OVER -> enable(
                actions,
                JSkatAction.CONTINUE_LOCAL_SERIES,
                JSkatAction.REPLAY_GAME,
                JSkatAction.NEXT_REPLAY_STEP
            )

            else -> Unit
        }
        return true
    }

    private fun enable(actions: Map<JSkatAction, AbstractJSkatAction>, vararg keys: JSkatAction) =
        keys.forEach { actions[it]?.isEnabled = true }

    private fun disable(actions: Map<JSkatAction, AbstractJSkatAction>, vararg keys: JSkatAction) =
        keys.forEach { actions[it]?.isEnabled = false }
}
