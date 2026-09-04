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

        // Keep the cards compact while showing enough of their faces to identify them easily.
        val visibleCardHeight = 70.0
        card1View.viewport = Rectangle2D(0.0, 0.0, 200.0, visibleCardHeight)
        card2View.viewport = Rectangle2D(0.0, 0.0, 200.0, visibleCardHeight)
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
}
