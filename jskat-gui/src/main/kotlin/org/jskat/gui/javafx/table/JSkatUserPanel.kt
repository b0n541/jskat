package org.jskat.gui.javafx.table

import org.jskat.data.SkatGameData
import org.jskat.util.CardList
import org.jskat.util.GameType
import org.slf4j.LoggerFactory

class JSkatUserPanel(
    maxCards: Int,
    showIssWidgets: Boolean
) : AbstractHandPanel(maxCards, showIssWidgets) {

    private val log = LoggerFactory.getLogger(JSkatUserPanel::class.java)

    var gameState: SkatGameData.GameState? = null

    init {
        showCards()
        minHeight = 0.0

        cardPanel.onCardClicked = { card ->
            when (gameState) {
                SkatGameData.GameState.DISCARDING, SkatGameData.GameState.SCHIEBERAMSCH -> {
                    // This would be where JSkatAction.PUT_CARD_INTO_SKAT is triggered
                    log.debug("Card clicked in discarding phase: $card")
                    // Example: getActionMap().get(JSkatAction.PUT_CARD_INTO_SKAT).actionPerformed(...)
                }
                SkatGameData.GameState.TRICK_PLAYING -> {
                    // This would be where JSkatAction.PLAY_CARD is triggered
                    log.debug("Card clicked in trick playing phase: $card")
                    // Example: getActionMap().get(JSkatAction.PLAY_CARD).actionPerformed(...)
                }
                else -> {
                    log.debug("Card clicked in unhandled state: $gameState")
                }
            }
        }
    }

    override fun hideCards() {
        // User cards are never hidden
    }

    fun getHandCards(): CardList {
        return cardPanel.cards
    }
}
