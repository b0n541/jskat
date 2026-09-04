package org.jskat.gui.javafx.table

import javafx.scene.control.Button
import javafx.application.Platform
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BiddingContextPanelTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
            val toolkitStarted = CountDownLatch(1)
            Platform.startup(toolkitStarted::countDown)
            check(toolkitStarted.await(1, TimeUnit.SECONDS))
        }
    }

    @Test
    fun `holding an ISS bid dispatches hold rather than making the next bid`() {
        val makeBid = RecordingAction()
        val holdBid = RecordingAction()
        val actions = mapOf(
            JSkatAction.MAKE_BID to makeBid,
            JSkatAction.HOLD_BID to holdBid,
            JSkatAction.PASS_BID to RecordingAction()
        )
        onFxThread {
            val userPanel = JSkatUserPanel("ISS-42", 10, true, actions)
            val panel = BiddingContextPanel(actions, JSkatGraphicRepository.INSTANCE, userPanel)
            panel.setBidValueToHold(18)
            bidButton(panel).fire()
        }

        assertThat(holdBid.awaitEvent()?.actionCommand).isEqualTo(JSkatAction.HOLD_BID.toString())
        assertThat(makeBid.awaitEvent(10)).isNull()
    }

    private fun bidButton(panel: BiddingContextPanel): Button {
        val field = BiddingContextPanel::class.java.getDeclaredField("bidButton")
        field.isAccessible = true
        return field.get(panel) as Button
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
        private val eventReceived = CountDownLatch(1)
        private var receivedEvent: JSkatActionEvent? = null

        override fun actionPerformed(event: JSkatActionEvent) {
            receivedEvent = event
            eventReceived.countDown()
        }

        fun awaitEvent(timeoutMillis: Long = 1_000): JSkatActionEvent? {
            eventReceived.await(timeoutMillis, TimeUnit.MILLISECONDS)
            return receivedEvent
        }
    }
}
