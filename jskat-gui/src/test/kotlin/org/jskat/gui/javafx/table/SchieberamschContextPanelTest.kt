package org.jskat.gui.javafx.table

import javafx.application.Platform
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.javafx.JavaFxTestSupport
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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

    private class RecordingAction : AbstractJSkatAction() {
        override fun actionPerformed(event: org.jskat.control.gui.action.JSkatActionEvent) = Unit
    }
}
