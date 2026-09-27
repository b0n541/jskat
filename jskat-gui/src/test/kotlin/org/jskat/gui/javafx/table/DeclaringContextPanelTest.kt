package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.scene.layout.GridPane
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

class DeclaringContextPanelTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `game announce panel is positioned to the right of the declaring context`() {
        onFxThread {
            val actions = emptyMap<JSkatAction, AbstractJSkatAction>()
            val userPanel = JSkatUserPanel("local", 10, true, actions)
            val panel = DeclaringContextPanel("local", actions, userPanel)

            val announcePanel = panel.children.single { it is GameAnnouncePanel }
            assertThat(GridPane.getColumnIndex(announcePanel)).isEqualTo(2)
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
}
