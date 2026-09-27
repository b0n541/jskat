package org.jskat.gui.javafx.table

import javafx.geometry.Rectangle2D
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.CardList

class SkatPanel : HBox() {
    private val bitmaps = JSkatGraphicRepository.INSTANCE
    private val card1View = ImageView()
    private val card2View = ImageView()

    init {
        spacing = 8.0
        children.addAll(card1View, card2View)

        // Make sure images preserve ratio if resized, though usually they are fixed size
        card1View.isPreserveRatio = true
        card2View.isPreserveRatio = true
        card1View.viewport = COMPACT_CARD_VIEWPORT
        card2View.viewport = COMPACT_CARD_VIEWPORT
    }

    fun setSkatCards(skat: CardList) {
        if (skat.size() == 2) {
            card1View.image = bitmaps.getCardImageFX(skat[0])
            card2View.image = bitmaps.getCardImageFX(skat[1])
        }
    }

    fun resetPanel() {
        card1View.image = null
        card2View.image = null
    }

    private companion object {
        val COMPACT_CARD_VIEWPORT = Rectangle2D(0.0, 0.0, 200.0, 70.0)
    }
}
