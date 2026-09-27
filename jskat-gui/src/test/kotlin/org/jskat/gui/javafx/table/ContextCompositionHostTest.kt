package org.jskat.gui.javafx.table

import org.assertj.core.api.Assertions.assertThat
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.SkatGameData.GameState
import org.junit.jupiter.api.Test

class ContextCompositionHostTest {

    @Test
    fun `renders selected command controls only in the shared action area`() {
        val host = ContextCompositionHost()
        val bidControl = Pane()
        val passControl = Pane()
        host.sharedActionArea.register(ContextPanelType.BIDDING, JSkatAction.MAKE_BID, bidControl)
        host.sharedActionArea.register(ContextPanelType.BIDDING, JSkatAction.HOLD_BID, bidControl)
        host.sharedActionArea.register(ContextPanelType.BIDDING, JSkatAction.PASS_BID, passControl)

        host.render(
            ContextRenderingProjection(
                ContextPanelType.BIDDING,
                LowerLeftContent.NONE,
                listOf(JSkatAction.MAKE_BID, JSkatAction.HOLD_BID, JSkatAction.PASS_BID)
            )
        )

        assertThat(host.sharedActionArea.pane.children).containsExactly(bidControl, passControl)
        assertThat(host.lowerRight.children).containsExactly(host.sharedActionArea.pane)
    }

    @Test
    fun `keeps phase content above an always-present stable lower row`() {
        val host = ContextCompositionHost()

        assertThat(host.pane.children).containsExactly(host.contextPanelStack.pane, host.lowerRow)
        assertThat(host.lowerRow.children).contains(host.lowerLeft, host.lowerRight)
        assertThat(host.lowerRow.minHeight).isEqualTo(75.0)
        assertThat(host.lowerRow.prefHeight).isEqualTo(75.0)
        assertThat(host.lowerRow.maxHeight).isEqualTo(75.0)
    }

    @Test
    fun `aligns lower-left content with the table edge`() {
        val host = ContextCompositionHost()

        assertThat(host.lowerLeft.alignment).isEqualTo(Pos.CENTER_LEFT)
        assertThat(HBox.getHgrow(host.lowerLeft)).isEqualTo(Priority.NEVER)
        assertThat(HBox.getHgrow(host.lowerRight)).isEqualTo(Priority.NEVER)
        assertThat(HBox.getHgrow(host.lowerRow.children[1])).isEqualTo(Priority.ALWAYS)
    }

    @Test
    fun `records all renderer slot selections at the composition seam`() {
        val host = ContextCompositionHost()
        host.contextPanelStack.add(ContextPanelType.TRICK_PLAYING, javafx.scene.layout.Pane())

        val projection = SharedContextRenderer.render(
            ContextRenderingState(ContextMode.LOCAL, GameState.TRICK_PLAYING, isReplay = true),
            host
        )

        assertThat(host.renderedProjection).isEqualTo(projection)
        assertThat(projection.lowerLeftContent).isEqualTo(LowerLeftContent.REPLAY_SKAT)
        assertThat(projection.actions).containsExactly(JSkatAction.REPLAY_GAME, JSkatAction.NEXT_REPLAY_STEP)
    }
}
