package org.jskat.gui.javafx

import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.javafx.iss.IssLobbyPanel
import org.jskat.gui.javafx.iss.IssLoginPanel
import org.jskat.gui.javafx.main.WelcomePanel
import org.jskat.gui.javafx.table.BigActionButton
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class BigActionButtonScreenTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeJavaFx() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `uses big action buttons on every large action screen`() {
        onFxThread {
            val actions = JSkatAction.entries.associateWith { RecordingAction() as AbstractJSkatAction }

            assertThat(WelcomePanel(actions).lookupAll(".${BigActionButton.STYLE_CLASS}")).hasSize(4)
            assertThat(IssLoginPanel(actions).lookupAll(".${BigActionButton.STYLE_CLASS}")).hasSize(3)
            assertThat(IssLobbyPanel(actions, null).lookupAll(".${BigActionButton.STYLE_CLASS}")).hasSize(2)
        }
    }

    private class RecordingAction : AbstractJSkatAction() {
        override fun actionPerformed(event: JSkatActionEvent) = Unit
    }
}
