package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import org.jskat.control.gui.action.JSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.JSkatResourceBundle
import java.awt.event.ActionEvent
import javax.swing.ActionMap
import javax.swing.SwingUtilities

class DiscardPanel(
    private val actions: ActionMap,
    private val maxCardCount: Int
) : StackPane() {

    private val cards = CardList()
    private val cardViews = HBox()
    private val pickUpSkatButton = Button(JSkatResourceBundle.INSTANCE.getString("pick_up_skat"))
    private val bitmaps = JSkatGraphicRepository.INSTANCE
    private var announcePanel: GameAnnouncePanel? = null

    var userPickedUpSkat: Boolean = false
        private set

    val discardedCards: CardList
        get() = CardList(cards)

    init {
        stylesheets.add("/org/jskat/gui/javafx/jskat.css")

        pickUpSkatButton.setOnAction {
            pickUpSkatButton.isDisable = true

            // Fire the event to request the skat cards from the game logic
            val action = actions.get(JSkatAction.PICK_UP_SKAT)
            if (action != null) {
                SwingUtilities.invokeLater {
                    action.actionPerformed(
                        ActionEvent(
                            this,
                            ActionEvent.ACTION_PERFORMED,
                            JSkatAction.PICK_UP_SKAT.toString()
                        )
                    )
                }
            }
        }

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
        announcePanel.stylesheets.add("/org/jskat/gui/javafx/jskat.css")
    }

    private fun updateView() {
        cardViews.children.clear()
        for (card in cards) {
            val cardView = ImageView(bitmaps.getCardImageFX(card))
            cardView.setOnMouseClicked {
                val action = actions.get(JSkatAction.TAKE_CARD_FROM_SKAT)
                if (action != null) {
                    SwingUtilities.invokeLater {
                        action.actionPerformed(
                            ActionEvent(
                                card,
                                ActionEvent.ACTION_PERFORMED,
                                JSkatAction.TAKE_CARD_FROM_SKAT.toString()
                            )
                        )
                    }
                }
            }
            cardViews.children.add(cardView)
        }
    }
}
