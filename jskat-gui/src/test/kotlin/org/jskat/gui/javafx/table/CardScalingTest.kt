package org.jskat.gui.javafx.table

import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import org.assertj.core.api.Assertions.assertThat
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.main.StartSkatSeriesAction
import org.jskat.gui.javafx.JavaFxTestSupport
import org.jskat.gui.javafx.onFxThread
import org.jskat.util.Card
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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
    fun `player hand renders cards at one and a quarter times their source size`() {
        onFxThread {
            val playerHand = JSkatUserPanel("Local", 12, false, emptyMap())
            playerHand.cardPanel.addCard(Card.CJ)

            val cardView = ((playerHand.cardPanel.children.single() as Pane).children.single() as ImageView)

            assertThat(cardView.fitWidth).isEqualTo(cardView.image.width * 1.25)
            assertThat(cardView.fitHeight).isEqualTo(cardView.image.height * 1.25)
        }
    }

    @Test
    fun `player hand caps its fan width while allowing bottom clipping`() {
        onFxThread {
            val playerHand = JSkatUserPanel("Local", 12, false, emptyMap())
            Card.entries.take(12).forEach(playerHand.cardPanel::addCard)
            playerHand.cardPanel.resize(300.0, 50.0)
            playerHand.cardPanel.layout()

            val hitAreas = playerHand.cardPanel.children.map { it as Pane }

            assertThat(hitAreas).allSatisfy { hitArea ->
                assertThat(hitArea.boundsInParent.minX).isGreaterThanOrEqualTo(-0.001)
                assertThat(hitArea.boundsInParent.maxX).isLessThanOrEqualTo(playerHand.cardPanel.width + 0.001)
            }
            assertThat(hitAreas.map { it.boundsInParent.maxY }).anySatisfy {
                assertThat(it).isGreaterThan(playerHand.cardPanel.height)
            }
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

    private class ScaleInspectableSkatTablePanel(
        tableName: String,
        actions: Map<JSkatAction, AbstractJSkatAction>
    ) : SkatTablePanel(tableName, actions) {
        fun currentTrickScale(): Double = trickPanel.globalScale

        fun lastTrickScale(): Double = lastTrickPanel.globalScale
    }
}
