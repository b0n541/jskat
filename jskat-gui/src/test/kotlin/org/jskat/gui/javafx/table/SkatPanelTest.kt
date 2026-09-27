package org.jskat.gui.javafx.table

import javafx.scene.image.ImageView
import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.util.Card
import org.jskat.util.CardList
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SkatPanelTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
        }
    }

    @Test
    fun `renders completion skat cards at compact replay height`() {
        val panel = SkatPanel()

        panel.setSkatCards(CardList(Card.C7, Card.DT))

        assertThat(panel.children.filterIsInstance<ImageView>()).allSatisfy { card ->
            assertThat(card.viewport.height).isEqualTo(70.0)
        }
    }
}
