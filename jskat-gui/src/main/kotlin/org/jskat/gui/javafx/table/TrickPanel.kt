package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import org.jskat.control.gui.img.CardFace
import org.jskat.data.JSkatOptions
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.Card
import org.jskat.util.Player
import java.util.*

class TrickPanel(
    private val globalScale: Double = 1.0,
    private val randomPlacement: Boolean = true
) : Pane() {

    private val jskatOptions = JSkatOptions.instance()
    private val bitmaps = JSkatGraphicRepository.INSTANCE
    private val cardRotations: MutableList<Double> = mutableListOf()
    private val positions: MutableList<Player> = mutableListOf()
    private val trick: MutableList<Card> = mutableListOf()
    private val rand = Random()
    private var userPosition: Player? = null
    private var rightOpponent: Player? = null
    private var leftOpponent: Player? = null

    private var cardFace: CardFace = jskatOptions.cardSet.cardFace

    init {
        style = "-fx-background-color: transparent;"
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }
    }

    fun addCard(player: Player, card: Card) {
        Platform.runLater {
            positions.add(player)
            trick.add(card)

            if (randomPlacement) {
                cardRotations.add(0.5 * rand.nextDouble() - 0.25)
            } else {
                cardRotations.add(0.0)
            }
            requestLayout()
        }
    }

    fun removeCard() {
        Platform.runLater {
            if (positions.isNotEmpty()) {
                positions.removeAt(positions.size - 1)
                trick.removeAt(trick.size - 1)
                cardRotations.removeAt(cardRotations.size - 1)
                requestLayout()
            }
        }
    }

    fun clearCards() {
        Platform.runLater {
            positions.clear()
            trick.clear()
            cardRotations.clear()
            requestLayout()
        }
    }

    override fun layoutChildren() {
        super.layoutChildren()

        if (isNewCardFace) {
            cardFace = jskatOptions.cardSet.cardFace
        }

        children.clear()

        val panelWidth = width
        val panelHeight = height

        val image = bitmaps.getCardImageFX(Card.CJ)
        val unscaledCardWidth = image.width
        val unscaledCardHeight = image.height

        val trickAreaUnscaledWidth = unscaledCardWidth * TRICK_SIZE_FACTOR
        val trickAreaUnscaledHeight = unscaledCardHeight * TRICK_SIZE_FACTOR

        // Avoid division by zero if panel dimensions are zero or unscaled trick area is zero
        if (panelWidth <= 0 || panelHeight <= 0 || trickAreaUnscaledWidth <= 0 || trickAreaUnscaledHeight <= 0) {
            return
        }

        // 1. Calculate maxFitScale: the largest scale factor that allows the entire unscaled trick area to fit within the panel
        val maxFitScaleX = panelWidth / trickAreaUnscaledWidth
        val maxFitScaleY = panelHeight / trickAreaUnscaledHeight
        var maxFitScale = minOf(maxFitScaleX, maxFitScaleY)

        // Apply safety margin to ensure cards are always within bounds
        maxFitScale *= SAFETY_MARGIN_FACTOR

        // 2. Calculate desiredScale: maxFitScale multiplied by globalScale
        val desiredScale = maxFitScale * globalScale

        // 3. Determine finalScaleFactor: ensure cards are always inside the panel
        // globalScale can only make the cards smaller than the maximum fit, or keep them at max fit if globalScale >= 1.0
        val finalScaleFactor = minOf(maxFitScale, desiredScale)

        // Calculate the actual displayed width and height of the trick area with the finalScaleFactor
        val finalTrickAreaWidth = trickAreaUnscaledWidth * finalScaleFactor
        val finalTrickAreaHeight = trickAreaUnscaledHeight * finalScaleFactor

        // Calculate the offsets to center this 'finalTrickArea' within the panel
        val xOffset = (panelWidth - finalTrickAreaWidth) / 2.0
        val yOffset = (panelHeight - finalTrickAreaHeight) / 2.0

        for (i in trick.indices) {
            val card = trick[i]
            val player = positions[i]

            var relativePosX = 0.0
            var relativePosY = 0.0

            // Calculate relative positions within the unscaled trick area
            if (player == leftOpponent) {
                relativePosX = 0.0
                relativePosY = unscaledCardHeight * (1.0 / 3.0)
            } else if (player == rightOpponent) {
                relativePosX = unscaledCardWidth * (2.0 / 3.0)
                relativePosY = 0.0
            } else if (player == userPosition) {
                relativePosX = unscaledCardWidth * (1.0 / 3.0)
                relativePosY = unscaledCardHeight * (2.0 / 3.0)
            }

            val cardView = ImageView(bitmaps.getCardImageFX(card))

            // Use fitWidth and fitHeight for scaling
            cardView.fitWidth = unscaledCardWidth * finalScaleFactor
            cardView.fitHeight = unscaledCardHeight * finalScaleFactor
            cardView.isPreserveRatio = true // Maintain aspect ratio
            cardView.isSmooth = true

            // Apply the centering offset and then scale the relative position using finalScaleFactor
            cardView.x = xOffset + (relativePosX * finalScaleFactor)
            cardView.y = yOffset + (relativePosY * finalScaleFactor)
            cardView.rotate = Math.toDegrees(cardRotations[i])

            children.add(cardView)
        }
    }

    private val isNewCardFace: Boolean
        get() = cardFace != jskatOptions.cardSet.cardFace

    fun setUserPosition(newUserPosition: Player) {
        Platform.runLater {
            userPosition = newUserPosition
            leftOpponent = userPosition!!.leftNeighbor
            rightOpponent = userPosition!!.rightNeighbor
            requestLayout()
        }
    }

    companion object {
        private const val TRICK_SIZE_FACTOR = 1.0 + 2.0 / 3.0
        private const val SAFETY_MARGIN_FACTOR = 0.95 // Added safety margin
    }
}
