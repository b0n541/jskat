package org.jskat.gui.javafx.table

import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.SkatGameData
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.util.CardList
import org.slf4j.LoggerFactory

class JSkatUserPanel(
    maxCards: Int,
    showIssWidgets: Boolean,
    actions: Map<JSkatAction, AbstractJSkatAction>
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
                    actions[JSkatAction.PUT_CARD_INTO_SKAT]?.actionPerformed(
                        JSkatActionEvent(JSkatAction.PUT_CARD_INTO_SKAT, card)
                    )
                }

                SkatGameData.GameState.TRICK_PLAYING -> {
                    log.debug("Card clicked in trick playing phase: $card")
                    actions[JSkatAction.PLAY_CARD]?.actionPerformed(
                        JSkatActionEvent(JSkatAction.PLAY_CARD, card)
                    )
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
