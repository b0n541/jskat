package org.jskat.gui.javafx

import javafx.application.Platform
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
        override fun actionPerformed(event: JSkatActionEvent) = Unit
    }
}
