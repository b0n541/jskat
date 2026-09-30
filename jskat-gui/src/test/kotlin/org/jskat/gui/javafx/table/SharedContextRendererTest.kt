package org.jskat.gui.javafx.table

import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.SkatGameData.GameState
import org.junit.jupiter.api.Test

class SharedContextRendererTest {

    @Test
    fun `projects local trick play into trick content with its applicable action`() {
        val projection = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.LOCAL, GameState.TRICK_PLAYING, isContraAvailable = true)
        )

        assertThat(projection.phaseContent).isEqualTo(ContextPanelType.TRICK_PLAYING)
        assertThat(projection.lowerLeftContent).isEqualTo(LowerLeftContent.NONE)
        assertThat(projection.actions).containsExactly(JSkatAction.CALL_CONTRA)
    }

    @Test
    fun `projects every local phase independently of the lower row`() {
        val expectedPhases = mapOf(
            GameState.GAME_START to ContextPanelType.START,
            GameState.DEALING to ContextPanelType.START,
            GameState.BIDDING to ContextPanelType.BIDDING,
            GameState.RAMSCH_GRAND_HAND_ANNOUNCING to ContextPanelType.SCHIEBERAMSCH,
            GameState.SCHIEBERAMSCH to ContextPanelType.SCHIEBERAMSCH,
            GameState.PICKING_UP_SKAT to ContextPanelType.DECLARING,
            GameState.DISCARDING to ContextPanelType.DECLARING,
            GameState.DECLARING to ContextPanelType.DECLARING,
            GameState.RE to ContextPanelType.RE_AFTER_CONTRA,
            GameState.CONTRA to ContextPanelType.RE_AFTER_CONTRA,
            GameState.TRICK_PLAYING to ContextPanelType.TRICK_PLAYING,
            GameState.CALCULATING_GAME_VALUE to ContextPanelType.GAME_OVER,
            GameState.PRELIMINARY_GAME_END to ContextPanelType.GAME_OVER,
            GameState.GAME_OVER to ContextPanelType.GAME_OVER
        )

        expectedPhases.forEach { (gameState, phaseContent) ->
            val projection = SharedContextRenderer.project(ContextRenderingState(ContextMode.LOCAL, gameState))

            assertThat(projection.phaseContent).isEqualTo(phaseContent)
            assertThat(projection.lowerLeftContent).isEqualTo(
                if (gameState == GameState.GAME_OVER) LowerLeftContent.GAME_OVER_SKAT else LowerLeftContent.NONE
            )
        }
    }

    @Test
    fun `projects ISS trick play into its applicable commands`() {
        val projection = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.ISS, GameState.TRICK_PLAYING)
        )

        assertThat(projection.phaseContent).isEqualTo(ContextPanelType.TRICK_PLAYING)
        assertThat(projection.lowerLeftContent).isEqualTo(LowerLeftContent.NONE)
        assertThat(projection.actions).containsExactly(JSkatAction.RESIGN, JSkatAction.SHOW_CARDS)
    }

    @Test
    fun `projects ISS bidding into its applicable commands`() {
        val projection = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.ISS, GameState.BIDDING)
        )

        assertThat(projection.phaseContent).isEqualTo(ContextPanelType.BIDDING)
        assertThat(projection.actions).containsExactly(
            JSkatAction.MAKE_BID,
            JSkatAction.HOLD_BID,
            JSkatAction.PASS_BID
        )
    }

    @Test
    fun `projects ISS game over into ready and leave table`() {
        val projection = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.ISS, GameState.GAME_OVER)
        )

        assertThat(projection.phaseContent).isEqualTo(ContextPanelType.GAME_OVER)
        assertThat(projection.actions).containsExactly(
            JSkatAction.READY_TO_PLAY,
            JSkatAction.LEAVE_ISS_TABLE
        )
    }

    @Test
    fun `projects ISS declarer actions into the shared area`() {
        val pickingUpSkat = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.ISS, GameState.PICKING_UP_SKAT)
        )
        val discarding = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.ISS, GameState.DISCARDING)
        )

        assertThat(pickingUpSkat.actions).containsExactly(JSkatAction.PICK_UP_SKAT, JSkatAction.ANNOUNCE_GAME)
        assertThat(discarding.actions).containsExactly(JSkatAction.ANNOUNCE_GAME)
    }

    @Test
    fun `does not select contra when it is unavailable`() {
        val projection = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.LOCAL, GameState.TRICK_PLAYING)
        )

        assertThat(projection.actions).isEmpty()
    }

    @Test
    fun `projects active replay into navigation without ordinary content`() {
        val projection = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.LOCAL, GameState.TRICK_PLAYING, isReplay = true)
        )

        assertThat(projection.phaseContent).isEqualTo(ContextPanelType.TRICK_PLAYING)
        assertThat(projection.lowerLeftContent).isEqualTo(LowerLeftContent.REPLAY_SKAT)
        assertThat(projection.actions).containsExactly(JSkatAction.REPLAY_GAME, JSkatAction.NEXT_REPLAY_STEP)
    }

    @Test
    fun `projects replay completion without next move`() {
        val projection = SharedContextRenderer.project(
            ContextRenderingState(ContextMode.LOCAL, GameState.GAME_OVER, isReplay = true)
        )

        assertThat(projection.phaseContent).isEqualTo(ContextPanelType.GAME_OVER)
        assertThat(projection.lowerLeftContent).isEqualTo(LowerLeftContent.GAME_OVER_SKAT)
        assertThat(projection.actions).containsExactly(JSkatAction.REPLAY_GAME, JSkatAction.CONTINUE_LOCAL_SERIES)
    }
}
