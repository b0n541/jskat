package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.Icon
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.JSkatResourceBundle

class DiscardPanel(
    private val tableName: String,
    private val actions: Map<JSkatAction, AbstractJSkatAction>,
    private val maxCardCount: Int
) : StackPane() {

    private val cards = CardList()
    private val cardViews = HBox()
    private val pickUpSkatButton = Button(JSkatResourceBundle.INSTANCE.getString("pickUpSkat"))
    private val bitmaps = JSkatGraphicRepository.INSTANCE
    private var announcePanel: GameAnnouncePanel? = null

    var userPickedUpSkat: Boolean = false
        private set

    val discardedCards: CardList
        get() = CardList(cards)

    init {
        alignment = Pos.CENTER

        pickUpSkatButton.graphic = bitmaps.getImageView(Icon.PLAY, JSkatGraphicRepository.IconSize.BIG)
        pickUpSkatButton.setOnAction {
            pickUpSkatButton.isDisable = true

            // Fire the event to request the skat cards from the game logic
            actions[JSkatAction.PICK_UP_SKAT]?.actionPerformed(
                JSkatActionEvent(JSkatAction.PICK_UP_SKAT, it.source)
            )
        }

        cardViews.alignment = Pos.CENTER
        cardViews.spacing = 8.0

        // Initially, only the button is visible
        children.add(pickUpSkatButton)
    }

    fun setSkat(skat: CardList) {
        // This method is called when the SkatCardsPickedUpEvent is received
        userPickedUpSkat = true
        announcePanel?.setUserPickedUpSkat(true)
        children.setAll(cardViews)
        cards.clear()
        cards.addAll(skat)
        updateView()
    }

    fun clearSkat() {
        Platform.runLater {
            cards.clear()
            updateView()
        }
    }

    fun addCard(card: Card) {
        Platform.runLater {
            if (cards.size() < maxCardCount) {
                cards.add(card)
                updateView()
            }
        }
    }

    fun removeCard(card: Card) {
        Platform.runLater {
            cards.remove(card)
            updateView()
        }
    }

    fun resetPanel() {
        Platform.runLater {
            userPickedUpSkat = false
            cards.clear()
            updateView()
            pickUpSkatButton.isDisable = false
            children.setAll(pickUpSkatButton)
        }
    }

    fun isHandFull(): Boolean {
        return cards.size() == maxCardCount
    }

    fun setAnnouncePanel(announcePanel: GameAnnouncePanel) {
        this.announcePanel = announcePanel
    }

    private fun updateView() {
        cardViews.children.clear()
        for (card in cards) {
            val cardView = ImageView(bitmaps.getCardImageFX(card))
            cardView.setOnMouseClicked {
                actions[JSkatAction.TAKE_CARD_FROM_SKAT]?.actionPerformed(
                    JSkatActionEvent(tableName, card)
                )
            }
            cardViews.children.add(cardView)
        }
    }
}
