package org.jskat.gui.javafx.table

import javafx.scene.Cursor
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
        const val FAN_CARD_GAP_RATIO = 0.50
        const val FAN_ANGLE_PER_CARD = 5.0
        const val FAN_ARC_DEPTH_PER_CARD = 1.25
        const val HOVER_LIFT_RATIO = 0.18
        const val FAN_BOTTOM_CLIP = 24.0
    }

    internal val cards = CardList()
    private val cardViews = mutableListOf<ImageView>()
    private val cardHitAreas = mutableListOf<Pane>()
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
        cardHitAreas.clear()

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
            imageView.isSmooth = true

            // The hit area must not move when the image is lifted.  Otherwise a
            // cursor near an edge can leave the image as it moves, which drops it
            // back under the cursor and causes the hover state to oscillate.
            val hitArea = Pane(imageView)
            hitArea.isPickOnBounds = true
            imageView.isMouseTransparent = true

            if (isHumanPlayer) {
                hitArea.cursor = Cursor.HAND
                hitArea.setOnMouseClicked {
                    onCardClicked?.invoke(card)
                }
                hitArea.setOnMouseEntered {
                    imageView.translateY = -imageView.fitHeight * HOVER_LIFT_RATIO
                }
                hitArea.setOnMouseExited {
                    imageView.translateY = 0.0
                }
            }

            cardViews.add(imageView)
            cardHitAreas.add(hitArea)
            children.add(hitArea)
        }
        requestLayout()
    }

    override fun layoutChildren() {
        super.layoutChildren()

        if (cards.isEmpty) return

        fitOpponentCardsToPanel()

        val sampleCard = cardViews.firstOrNull() ?: return
        val cardWidth = sampleCard.fitWidth
        val cardHeight = sampleCard.fitHeight
        val availableWidth = width
        val cardGap = fanCardGap(cardWidth, availableWidth, cards.size())
        val handWidth = cardWidth + cardGap * (cards.size() - 1)
        val handStartX = (availableWidth - handWidth) / 2
        val middleCardIndex = (cards.size() - 1) / 2.0
        val cardScale = cardWidth / (sampleCard.image.width * scaleFactor)
        val handLayoutY = handLayoutY(cardWidth, cardHeight, middleCardIndex)

        for (i in 0 until cards.size()) {
            val hitArea = cardHitAreas[i]
            val angle = (i - middleCardIndex) * FAN_ANGLE_PER_CARD
            hitArea.resizeRelocate(
                handStartX + i * cardGap,
                handLayoutY + fanArcOffset(i - middleCardIndex, cardScale),
                cardWidth,
                cardHeight
            )
            hitArea.transforms.setAll(Rotate(angle, cardWidth / 2, cardHeight))
        }
    }

    private fun fanCardGap(cardWidth: Double, availableWidth: Double, cardCount: Int): Double {
        if (cardCount < 2) return 0.0

        val preferredGap = cardWidth * FAN_CARD_GAP_RATIO
        val gapThatFits = (availableWidth - cardWidth) / (cardCount - 1)
        return preferredGap.coerceAtMost(gapThatFits.coerceAtLeast(0.0))
    }

    private fun handLayoutY(cardWidth: Double, cardHeight: Double, middleCardIndex: Double): Double {
        val outerCardAngle = Math.toRadians(middleCardIndex * FAN_ANGLE_PER_CARD)
        val outerCardTopOverhang = (
            cardWidth / 2 * kotlin.math.sin(outerCardAngle) - cardHeight * (1 - kotlin.math.cos(outerCardAngle))
            ).coerceAtLeast(0.0)
        val topClearance = if (isHumanPlayer) cardHeight * HOVER_LIFT_RATIO else 0.0
        val requiredTopClearance = topClearance + outerCardTopOverhang
        return if (isHumanPlayer) {
            (height - cardHeight + FAN_BOTTOM_CLIP).coerceAtLeast(requiredTopClearance)
        } else {
            requiredTopClearance
        }
    }

    private fun fitOpponentCardsToPanel() {
        if (isHumanPlayer || children.isEmpty() || height <= 0.0) return

        val sampleCard = cardViews.firstOrNull() ?: return
        val fullCardWidth = sampleCard.image.width * scaleFactor
        val fullCardHeight = sampleCard.image.height * scaleFactor
        val middleCardIndex = (cards.size() - 1) / 2.0
        val outerCardAngle = Math.toRadians(middleCardIndex * FAN_ANGLE_PER_CARD)
        val outerCardTopOverhang = (
            fullCardWidth / 2 * kotlin.math.sin(outerCardAngle) - fullCardHeight * (1 - kotlin.math.cos(outerCardAngle))
            ).coerceAtLeast(0.0)
        val outerCardBottomOverhang = fullCardWidth / 2 * kotlin.math.sin(outerCardAngle)
        val fullHandHeight = fullCardHeight + outerCardTopOverhang + outerCardBottomOverhang +
            fanArcOffset(middleCardIndex, 1.0)
        val cardScale = (height / fullHandHeight).coerceAtMost(1.0)

        cardViews.forEach { view ->
            view.fitWidth = fullCardWidth * cardScale
            view.fitHeight = fullCardHeight * cardScale
        }
    }

    private fun fanArcOffset(distanceFromMiddle: Double, cardScale: Double): Double =
        distanceFromMiddle * distanceFromMiddle * FAN_ARC_DEPTH_PER_CARD * cardScale
}
