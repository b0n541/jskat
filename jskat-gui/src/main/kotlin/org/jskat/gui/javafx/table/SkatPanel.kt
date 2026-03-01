package org.jskat.gui.javafx.table

import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.CardList

class SkatPanel : HBox() {
    private val bitmaps = JSkatGraphicRepository.INSTANCE
    private val card1View = ImageView()
    private val card2View = ImageView()

    init {
        val spacer = Region()
        setHgrow(spacer, Priority.ALWAYS)
        children.addAll(card1View, spacer, card2View)

        // Make sure images preserve ratio if resized, though usually they are fixed size
        card1View.isPreserveRatio = true
        card2View.isPreserveRatio = true
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
