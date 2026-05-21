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
    private val randomPlacement: Boolean
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

        val cardScale = getCardScale() * globalScale
        val image = bitmaps.getCardImageFX(Card.CJ)

        val xScaleSize = image.width
        val xAllTrickCardsSize = xScaleSize * TRICK_SIZE_FACTOR
        val xBorder = (panelWidth * (1 / cardScale) - xAllTrickCardsSize) / 2.0

        val yScaleSize = image.height
        val yAllTrickCardsSize = yScaleSize * TRICK_SIZE_FACTOR
        val yBorder = (panelHeight * (1 / cardScale) - yAllTrickCardsSize) / 2.0

        for (i in trick.indices) {
            val card = trick[i]
            val player = positions[i]

            var posX = 0.0
            var posY = 0.0
            if (player == leftOpponent) {
                posX = xBorder
                posY = yBorder + yScaleSize * (1.0 / 3.0)
            } else if (player == rightOpponent) {
                posX = xBorder + xScaleSize * (2.0 / 3.0)
                posY = yBorder
            } else if (player == userPosition) {
                posX = xBorder + xScaleSize * (1.0 / 3.0)
                posY = yBorder + yScaleSize * (2.0 / 3.0)
            }

            val cardView = ImageView(bitmaps.getCardImageFX(card))
            cardView.x = posX * cardScale
            cardView.y = posY * cardScale
            cardView.rotate = Math.toDegrees(cardRotations[i])
            cardView.scaleX = cardScale
            cardView.scaleY = cardScale
            children.add(cardView)
        }
    }

    private fun getCardScale(): Double {
        val sampleCard = bitmaps.getCardImageFX(Card.CJ)
        val imageWidth = sampleCard.width * TRICK_SIZE_FACTOR
        val imageHeight = sampleCard.height * TRICK_SIZE_FACTOR

        val scaleX = width / imageWidth
        val scaleY = height / imageHeight

        var scaleFactor = 1.0
        if (scaleX < 1.0 || scaleY < 1.0) {
            scaleFactor = if (scaleX < scaleY) scaleX else scaleY
        }
        return scaleFactor
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
    }
}
