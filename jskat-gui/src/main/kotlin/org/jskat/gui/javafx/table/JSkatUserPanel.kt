package org.jskat.gui.javafx.table

import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.SkatGameData
import org.jskat.util.CardList
import org.slf4j.LoggerFactory
import java.awt.event.ActionEvent
import javax.swing.ActionMap
import javax.swing.SwingUtilities

class JSkatUserPanel(
    maxCards: Int,
    showIssWidgets: Boolean,
    actions: ActionMap
) : AbstractHandPanel(maxCards, showIssWidgets) {

    private val log = LoggerFactory.getLogger(JSkatUserPanel::class.java)

    var gameState: SkatGameData.GameState? = null

    init {
        showCards()
        minHeight = 0.0

        cardPanel.onCardClicked = { card ->
            when (gameState) {
                SkatGameData.GameState.DISCARDING, SkatGameData.GameState.SCHIEBERAMSCH -> {
                    log.debug("Card clicked in discarding phase: $card")
                    // Fire the event to request the skat cards from the game logic
                    val action = actions.get(JSkatAction.PUT_CARD_INTO_SKAT)
                    if (action != null) {
                        SwingUtilities.invokeLater {
                            action.actionPerformed(
                                ActionEvent(
                                    card,
                                    ActionEvent.ACTION_PERFORMED,
                                    JSkatAction.PUT_CARD_INTO_SKAT.toString()
                                )
                            )
                        }
                    }
                }

                SkatGameData.GameState.TRICK_PLAYING -> {
                    log.debug("Card clicked in trick playing phase: $card")
                    // Fire the event to request the skat cards from the game logic
                    val action = actions.get(JSkatAction.PLAY_CARD)
                    if (action != null) {
                        SwingUtilities.invokeLater {
                            action.actionPerformed(
                                ActionEvent(
                                    card,
                                    ActionEvent.ACTION_PERFORMED,
                                    JSkatAction.PLAY_CARD.toString()
                                )
                            )
                        }
                    }
                }

                else -> {
                    log.debug("Card clicked in unhandled state: $gameState")
                }
            }
        }
    }

    override fun createCardPanel(): CardPanel {
        val panel = CardPanel(1.0, false)
        panel.isHumanPlayer = true
        return panel
    }

    override fun hideCards() {
        // User cards are never hidden
    }

    fun getHandCards(): CardList {
        return cardPanel.cards
    }
}
