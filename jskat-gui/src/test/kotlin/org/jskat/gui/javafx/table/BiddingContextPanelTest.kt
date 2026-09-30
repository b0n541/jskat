package org.jskat.gui.javafx.table

import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.layout.GridPane
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.jskat.util.GameType
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
            JavaFxTestSupport.initializeToolkit()
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

    @Test
    fun `selected bidding game type preselects the declaring announcement`() {
        onFxThread {
            val actions = emptyMap<JSkatAction, AbstractJSkatAction>()
            val userPanel = JSkatUserPanel("local", 10, true, actions)
            val biddingPanel = BiddingContextPanel(actions, JSkatGraphicRepository.INSTANCE, userPanel)
            val declaringPanel = DeclaringContextPanel("local", actions, userPanel)

            biddingPanel.lookupAll(".radio-button")
                .filterIsInstance<RadioButton>()
                .single { it.userData == GameType.HEARTS }
                .fire()

            declaringPanel.preselectGameTypeIfUnset(biddingPanel.selectedGameType())

            assertThat(declaringPanel.selectedGameType()).isEqualTo(GameType.HEARTS)
        }
    }

    @Test
    fun `game announce panel is positioned to the right of the bidding context`() {
        onFxThread {
            val actions = emptyMap<JSkatAction, AbstractJSkatAction>()
            val userPanel = JSkatUserPanel("local", 10, true, actions)
            val panel = BiddingContextPanel(actions, JSkatGraphicRepository.INSTANCE, userPanel)

            val announcePanel = panel.children.single { it is GameAnnouncePanel }
            assertThat(GridPane.getColumnIndex(announcePanel)).isEqualTo(2)
        }
    }

    private fun bidButton(panel: BiddingContextPanel): Button {
        val field = BiddingContextPanel::class.java.getDeclaredField("bidButton")
        field.isAccessible = true
        return field.get(panel) as Button
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
