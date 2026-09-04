package org.jskat.gui.javafx.table

import javafx.scene.layout.Pane
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContextPanelStackTest {

    @Test
    fun `replacing the start context removes the local start panel`() {
        val contextPanels = ContextPanelStack()
        val localStart = Pane()
        val issStart = Pane()

        contextPanels.add(ContextPanelType.START, localStart)
        contextPanels.show(ContextPanelType.START)
        contextPanels.add(ContextPanelType.START, issStart)
        contextPanels.show(ContextPanelType.START)

        assertThat(contextPanels.pane.children.toList()).containsExactly(issStart)
        assertThat(localStart.isVisible).isFalse()
        assertThat(issStart.isVisible).isTrue()
    }
}
