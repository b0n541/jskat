package org.jskat.gui.javafx.table

import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Rotate
import org.jskat.data.JSkatOptions
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.GameType

class CardPanel(
    private val scaleFactor: Double = 1.0,
    private var showBackside: Boolean = true
) : Pane() {

    private companion object {
        const val FAN_CARD_GAP_RATIO = 0.42
        const val FAN_ANGLE_PER_CARD = 5.0
        const val HOVER_LIFT_RATIO = 0.18
    }

    internal val cards = CardList()
    private val cardViews = mutableMapOf<Card, ImageView>()
    private var sortGameType = GameType.GRAND

    var onCardClicked: ((Card) -> Unit)? = null
    var isHumanPlayer: Boolean = false

    init {
        minHeight = 0.0
        minWidth = 0.0
        val clipRect = Rectangle()
        clipRect.widthProperty().bind(widthProperty())
        clipRect.heightProperty().bind(heightProperty())
        clip = clipRect
    }

    fun addCard(newCard: Card) {
        if (!cards.contains(newCard)) {
            cards.add(newCard)
            cards.sort(sortGameType)
            updateCardViews()
        }
    }

    fun addCards(newCards: CardList) {
        cards.addAll(newCards)
        cards.sort(sortGameType)
        updateCardViews()
    }

    fun removeCard(cardToRemove: Card) {
        if (cards.contains(cardToRemove)) {
            cards.remove(cardToRemove)
            updateCardViews()
        } else if (!cards.isEmpty) {
            // Fallback: remove last card if specific card not found (e.g. hidden cards)
            cards.remove(cards.size() - 1)
            updateCardViews()
        }
    }

    fun clearCards() {
        cards.clear()
        updateCardViews()
    }

    fun showCards() {
        showBackside = false
        updateCardViews()
    }

    fun hideCards() {
        if (!JSkatOptions.instance().isCheatDebugMode) {
            showBackside = true
            updateCardViews()
        }
    }

    fun setSortType(newGameType: GameType) {
        sortGameType = newGameType
        cards.sort(sortGameType)
        updateCardViews()
    }

    val cardCount: Int
        get() = cards.size()

    private fun updateCardViews() {
        children.clear()
        cardViews.clear()

        for (card in cards) {
            val image = if (showBackside) {
                JSkatGraphicRepository.INSTANCE.getCardImageFX(null)
            } else {
                JSkatGraphicRepository.INSTANCE.getCardImageFX(card)
            }
            val imageView = ImageView(image)
            imageView.fitWidth = image.width * scaleFactor
            imageView.fitHeight = image.height * scaleFactor
            imageView.isPreserveRatio = true

            if (isHumanPlayer) {
                imageView.setOnMouseClicked {
                    onCardClicked?.invoke(card)
                }
                imageView.setOnMouseEntered {
                    imageView.style = "-fx-cursor: hand;"
                    imageView.translateY = -imageView.fitHeight * HOVER_LIFT_RATIO
                }
                imageView.setOnMouseExited {
                    imageView.style = "-fx-cursor: default;"
                    imageView.translateY = 0.0
                }
            }

            cardViews[card] = imageView
            children.add(imageView)
        }
        requestLayout()
    }

    override fun layoutChildren() {
        super.layoutChildren()

        if (cards.isEmpty) return

        val cardWidth = if (children.isNotEmpty()) (children[0] as ImageView).fitWidth else 0.0
        val cardHeight = if (children.isNotEmpty()) (children[0] as ImageView).fitHeight else 0.0
        val availableWidth = width
        val cardGap = fanCardGap(cardWidth, availableWidth, cards.size())
        val handWidth = cardWidth + cardGap * (cards.size() - 1)
        val handStartX = (availableWidth - handWidth) / 2
        val middleCardIndex = (cards.size() - 1) / 2.0

        for (i in 0 until cards.size()) {
            val card = cards[i]
            val view = cardViews[card]
            if (view != null) {
                val angle = (i - middleCardIndex) * FAN_ANGLE_PER_CARD
                view.layoutX = handStartX + i * cardGap
                view.layoutY = 0.0
                view.transforms.setAll(Rotate(angle, cardWidth / 2, cardHeight))
            }
        }
    }

    private fun fanCardGap(cardWidth: Double, availableWidth: Double, cardCount: Int): Double {
        if (cardCount < 2) return 0.0

        val preferredGap = cardWidth * FAN_CARD_GAP_RATIO
        val gapThatFits = (availableWidth - cardWidth) / (cardCount - 1)
        return preferredGap.coerceAtMost(gapThatFits.coerceAtLeast(0.0))
    }
}
