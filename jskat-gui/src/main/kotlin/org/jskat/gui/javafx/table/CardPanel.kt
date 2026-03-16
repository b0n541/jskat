package org.jskat.gui.javafx.table

import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import org.jskat.data.JSkatOptions
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.GameType

class CardPanel(
    private val scaleFactor: Double = 1.0,
    private var showBackside: Boolean = true
) : Pane() {

    internal val cards = CardList()
    private val cardViews = mutableMapOf<Card, ImageView>()
    private var sortGameType = GameType.GRAND

    var onCardClicked: ((Card) -> Unit)? = null
    var isHumanPlayer: Boolean = false

    init {
        // Initial setup if needed
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
                }
                imageView.setOnMouseExited {
                    imageView.style = "-fx-cursor: default;"
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
        val availableWidth = width

        var cardGap = cardWidth
        if (cards.size() * cardGap > availableWidth) {
            if (cards.size() > 1) {
                cardGap = (availableWidth - cardWidth) / (cards.size() - 1)
            }
        }

        for (i in 0 until cards.size()) {
            val card = cards[i]
            val view = cardViews[card]
            if (view != null) {
                view.layoutX = i * cardGap
                view.layoutY = 0.0
            }
        }
    }
}
