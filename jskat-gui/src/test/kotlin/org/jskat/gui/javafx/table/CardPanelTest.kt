package org.jskat.gui.javafx.table

import org.assertj.core.api.Assertions.assertThat
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
}
