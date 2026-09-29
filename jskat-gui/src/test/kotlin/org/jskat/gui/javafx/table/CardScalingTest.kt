package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.main.StartSkatSeriesAction
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.util.Card
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CardScalingTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeJavaFx() {
            JSkatOptions.instance(DesktopSavePathResolver())
            JavaFxTestSupport.initializeToolkit()
        }
    }

    @Test
    fun `player hand renders cards at one and a half times their source size`() {
        onFxThread {
            val playerHand = JSkatUserPanel("Local", 12, false, emptyMap())
            playerHand.cardPanel.addCard(Card.CJ)

            val cardView = ((playerHand.cardPanel.children.single() as Pane).children.single() as ImageView)

            assertThat(cardView.fitWidth).isEqualTo(cardView.image.width * 1.5)
            assertThat(cardView.fitHeight).isEqualTo(cardView.image.height * 1.5)
        }
    }

    @Test
    fun `only the current trick uses the enlarged trick play scale`() {
        onFxThread {
            val table = ScaleInspectableSkatTablePanel(
                "Scale test",
                mapOf(JSkatAction.START_LOCAL_SERIES to StartSkatSeriesAction())
            )

            assertThat(table.currentTrickScale()).isEqualTo(1.5)
            assertThat(table.lastTrickScale()).isEqualTo(0.6)
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

    private class ScaleInspectableSkatTablePanel(
        tableName: String,
        actions: Map<JSkatAction, AbstractJSkatAction>
    ) : SkatTablePanel(tableName, actions) {
        fun currentTrickScale(): Double = trickPanel.globalScale

        fun lastTrickScale(): Double = lastTrickPanel.globalScale
    }
}
