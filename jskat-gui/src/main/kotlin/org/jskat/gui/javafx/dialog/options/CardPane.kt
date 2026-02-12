package org.jskat.gui.javafx.dialog.options

import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.CardDeck
import kotlin.math.floor

class CardPane : Region() {

    private val cards = ArrayList<ImageView>()
    private val cardDeck = CardDeck.getAllCards()

    private var mouseX = Double.MAX_VALUE
    private var activeCardMinX = Double.MAX_VALUE
    private var activeCardMaxX = Double.MAX_VALUE

    init {
        // Set preferred size to match Swing implementation roughly
        prefWidth = 600.0
        prefHeight = 200.0

        redraw()

        setOnMouseMoved { e ->
            mouseX = e.x
            requestLayout()
        }

        setOnMouseExited {
            mouseX = Double.MAX_VALUE
            activeCardMinX = Double.MAX_VALUE
            activeCardMaxX = Double.MAX_VALUE
            requestLayout()
        }
    }

    fun redraw() {
        children.clear()
        cards.clear()
        for (card in cardDeck) {
            val img = JSkatGraphicRepository.INSTANCE.getCardImageFX(card)
            val imageView = ImageView(img)
            imageView.isPreserveRatio = true
            cards.add(imageView)
            children.add(imageView)
        }
        requestLayout()
    }

    override fun layoutChildren() {
        if (cards.isEmpty()) return

        val width = width
        val height = height

        // Assuming all cards have same aspect ratio
        val firstCard = cards[0]
        val img = firstCard.image ?: return
        val aspectRatio = img.width / img.height
        val cardHeight = height
        val cardWidth = cardHeight * aspectRatio

        // Update fitHeight/fitWidth for all cards
        cards.forEach {
            it.fitHeight = cardHeight
            it.fitWidth = cardWidth
        }

        var cardGap = cardWidth
        if (cards.size * cardGap > width) {
            cardGap = (width - cardWidth) / (cards.size - 1)
        }

        // Update active card position logic
        if (mouseX != Double.MAX_VALUE) {
            if (mouseX < activeCardMinX) {
                activeCardMinX = floor(mouseX / cardGap) * cardGap
            } else if (mouseX > activeCardMaxX) {
                activeCardMinX = floor((mouseX - cardWidth + cardGap) / cardGap) * cardGap
            }
            activeCardMaxX = activeCardMinX + cardWidth
        }

        // Position cards
        for (i in cards.indices) {
            val card = cards[i]
            var x = 0.0

            if (i * cardGap <= activeCardMinX) {
                x = i * cardGap
            } else {
                x = (i - 1) * cardGap + cardWidth
            }

            card.layoutX = x
            card.layoutY = 0.0
        }
    }
}
