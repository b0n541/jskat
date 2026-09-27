package org.jskat.gui.javafx.table

import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.SkatGameData.GameState

/** The table mode whose established phase content is being composed. */
enum class ContextMode { LOCAL, ISS }

/** All inputs needed to select the centre table presentation. */
data class ContextRenderingState(
    val mode: ContextMode,
    val gameState: GameState,
    val isReplay: Boolean = false,
    val isDeclarer: Boolean = true,
    val isContraAvailable: Boolean = false
)

/** The only card content the shared lower-left slot may select. */
enum class LowerLeftContent { NONE, REPLAY_SKAT, GAME_OVER_SKAT }

/** A presentation-only selection; action enablement remains owned by the existing actions. */
data class ContextRenderingProjection(
    val phaseContent: ContextPanelType,
    val lowerLeftContent: LowerLeftContent,
    val actions: List<JSkatAction>
)

/** Projects the current table context into the three independently rendered centre slots. */
object SharedContextRenderer {
    internal fun render(state: ContextRenderingState, host: ContextCompositionHost): ContextRenderingProjection =
        project(state).also(host::render)

    fun project(state: ContextRenderingState): ContextRenderingProjection {
        val phaseContent = phaseFor(state)
        if (state.isReplay) {
            return ContextRenderingProjection(
                phaseContent,
                when (state.gameState) {
                    GameState.TRICK_PLAYING -> LowerLeftContent.REPLAY_SKAT
                    GameState.GAME_OVER -> LowerLeftContent.GAME_OVER_SKAT
                    else -> LowerLeftContent.NONE
                },
                if (state.gameState == GameState.GAME_OVER) {
                    listOf(JSkatAction.REPLAY_GAME, JSkatAction.CONTINUE_LOCAL_SERIES)
                } else {
                    listOf(JSkatAction.REPLAY_GAME, JSkatAction.NEXT_REPLAY_STEP)
                }
            )
        }

        return ContextRenderingProjection(
            phaseContent,
            if (state.gameState == GameState.GAME_OVER) LowerLeftContent.GAME_OVER_SKAT else LowerLeftContent.NONE,
            actionsFor(state)
        )
    }

    private fun phaseFor(state: ContextRenderingState): ContextPanelType = when (state.gameState) {
        GameState.GAME_START, GameState.DEALING -> ContextPanelType.START
        GameState.BIDDING -> ContextPanelType.BIDDING
        GameState.RAMSCH_GRAND_HAND_ANNOUNCING, GameState.SCHIEBERAMSCH -> ContextPanelType.SCHIEBERAMSCH
        GameState.PICKING_UP_SKAT, GameState.DISCARDING, GameState.DECLARING ->
            if (state.isReplay || state.isDeclarer) ContextPanelType.DECLARING else ContextPanelType.BIDDING
        GameState.RE, GameState.CONTRA -> ContextPanelType.RE_AFTER_CONTRA
        GameState.TRICK_PLAYING -> ContextPanelType.TRICK_PLAYING
        GameState.CALCULATING_GAME_VALUE, GameState.PRELIMINARY_GAME_END, GameState.GAME_OVER -> ContextPanelType.GAME_OVER
    }

    private fun actionsFor(state: ContextRenderingState): List<JSkatAction> = when (state.mode) {
        ContextMode.ISS -> when (state.gameState) {
            GameState.TRICK_PLAYING -> listOf(JSkatAction.RESIGN, JSkatAction.SHOW_CARDS)
            else -> emptyList()
        }

        ContextMode.LOCAL -> when (state.gameState) {
            GameState.GAME_START -> listOf(JSkatAction.START_LOCAL_SERIES)
            GameState.BIDDING -> listOf(JSkatAction.MAKE_BID, JSkatAction.HOLD_BID, JSkatAction.PASS_BID)
            GameState.RAMSCH_GRAND_HAND_ANNOUNCING -> listOf(JSkatAction.PLAY_GRAND_HAND, JSkatAction.PLAY_SCHIEBERAMSCH)
            GameState.SCHIEBERAMSCH -> listOf(JSkatAction.SCHIEBEN, JSkatAction.PICK_UP_SKAT)
            GameState.PICKING_UP_SKAT -> listOf(JSkatAction.PICK_UP_SKAT, JSkatAction.ANNOUNCE_GAME)
            GameState.DISCARDING, GameState.DECLARING -> listOf(JSkatAction.ANNOUNCE_GAME)
            GameState.RE, GameState.CONTRA -> listOf(JSkatAction.CALL_RE)
            GameState.TRICK_PLAYING -> listOfNotNull(JSkatAction.CALL_CONTRA.takeIf { state.isContraAvailable })
            GameState.CALCULATING_GAME_VALUE, GameState.PRELIMINARY_GAME_END, GameState.GAME_OVER ->
                listOf(JSkatAction.REPLAY_GAME, JSkatAction.CONTINUE_LOCAL_SERIES)
            GameState.DEALING -> emptyList()
        }
    }
}
