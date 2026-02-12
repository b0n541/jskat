package org.jskat.gui.javafx.dialog.options

import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.CardDeck

class CardPane : HBox() {

    init {
        // Set a negative spacing to make cards overlap
        spacing = -100.0
        redraw()
    }

    fun redraw() {
        children.clear()
        val cardDeck = CardDeck.getAllCards()
        for (card in cardDeck) {
            children.add(ImageView(JSkatGraphicRepository.INSTANCE.getCardImageFX(card)))
        }
    }
}
