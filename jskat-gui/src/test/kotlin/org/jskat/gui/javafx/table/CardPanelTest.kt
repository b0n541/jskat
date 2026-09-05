package org.jskat.gui.javafx.table

import org.assertj.core.api.Assertions.assertThat
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.util.Card
import org.jskat.util.CardList
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class CardPanelTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initializeOptions() {
            JSkatOptions.instance(DesktopSavePathResolver())
        }
    }

    @Test
    fun `lays out every hidden ISS card`() {
        val cardPanel = CardPanel()
        cardPanel.addCards(CardList(List(10) { null as Card? }))

        cardPanel.resize(600.0, 230.0)
        cardPanel.layout()

        assertThat(cardPanel.children.map { it.layoutX }.distinct()).hasSize(10)
    }

    @Test
    fun `renders hand cards with smooth scaling`() {
        val cardPanel = CardPanel()
        cardPanel.addCard(Card.CJ)

        val hitArea = cardPanel.children.single() as Pane
        val cardView = hitArea.children.single() as ImageView

        assertThat(cardView.isSmooth).isTrue()
    }
}
