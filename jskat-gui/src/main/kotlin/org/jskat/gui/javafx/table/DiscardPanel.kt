package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
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
    private val pickUpSkatButton = BigActionButton.create(
        JSkatResourceBundle.INSTANCE.getString("pickUpSkat"), JSkatGraphicRepository.Icon.PLAY
    ) {
        (it.source as Button).isDisable = true
        BigActionButton.dispatch(actions[JSkatAction.PICK_UP_SKAT], JSkatAction.PICK_UP_SKAT, it.source)
    }
    private val bitmaps = JSkatGraphicRepository.INSTANCE
    private var announcePanel: GameAnnouncePanel? = null
    private var cardSelectionEnabled = true
    private var compactReplayPresentation = false

    var userPickedUpSkat: Boolean = false
        private set

    val discardedCards: CardList
        get() = CardList(cards)
    val pickUpActionControl: Button
        get() = pickUpSkatButton

    init {
        id = "discard-panel"
        alignment = Pos.CENTER

        cardViews.alignment = Pos.CENTER
        cardViews.spacing = 8.0
        cardViews.id = "discard-card-views"

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
            children.clear()
        }
    }

    fun isHandFull(): Boolean {
        return cards.size() == maxCardCount
    }

    fun setAnnouncePanel(announcePanel: GameAnnouncePanel) {
        this.announcePanel = announcePanel
    }

    fun setReplaySkatPresentation(isPresented: Boolean) {
        cardSelectionEnabled = !isPresented
        compactReplayPresentation = isPresented
        updateView()
    }

    private fun updateView() {
        cardViews.children.clear()
        for (card in cards) {
            val cardView = ImageView(bitmaps.getCardImageFX(card)).apply {
                viewport = COMPACT_CARD_VIEWPORT.takeIf { compactReplayPresentation }
            }
            if (cardSelectionEnabled) {
                cardView.setOnMouseClicked {
                    actions[JSkatAction.TAKE_CARD_FROM_SKAT]?.actionPerformed(
                        JSkatActionEvent(tableName, card)
                    )
                }
            }
            cardViews.children.add(cardView)
        }
    }

    private companion object {
        val COMPACT_CARD_VIEWPORT = Rectangle2D(0.0, 0.0, 200.0, 70.0)
    }
}
