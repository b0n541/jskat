package org.jskat.gui.javafx.table

import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.SkatGameData.GameState
import org.junit.jupiter.api.Test

class ContextCompositionHostTest {

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
