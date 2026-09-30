package org.jskat.gui.javafx.table

import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SchieberamschContextPanelTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `creates every shared action with the big action button factory`() {
        onFxThread {
            val actions = actionMap()
            val panel = SchieberamschContextPanel("local", actions, JSkatUserPanel("local", 10, false, actions), 4)

            assertThat(panel.grandHandActionControl.styleClass).contains(BigActionButton.STYLE_CLASS)
            assertThat(panel.schieberamschActionControl.styleClass).contains(BigActionButton.STYLE_CLASS)
            assertThat(panel.schiebenActionControl.styleClass).contains(BigActionButton.STYLE_CLASS)
        }
    }

    private fun actionMap(): Map<JSkatAction, AbstractJSkatAction> =
        JSkatAction.entries.associateWith { RecordingAction() }

    private class RecordingAction : AbstractJSkatAction() {
        override fun actionPerformed(event: org.jskat.control.gui.action.JSkatActionEvent) = Unit
    }
}
