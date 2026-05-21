package org.jskat.gui.javafx.table

import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import org.jskat.data.GameSummary
import org.jskat.util.Player

class GameOverTrickPanel : HBox() {
    private val trickPanels = mutableListOf<TrickPanel>()
    private var userPosition: Player? = null

    init {
        for (i in 0 until 10) {
            val panel = TrickPanel(randomPlacement = false)
            trickPanels.add(panel)
            children.add(panel)
            setHgrow(panel, Priority.ALWAYS)
        }
    }

    fun setGameSummary(summary: GameSummary) {
        for (i in 0 until 10) {
            trickPanels[i].apply {
                clearCards()

                if (i < summary.tricks.size) {
                    val trick = summary.tricks[i]
                    if (userPosition != null && trick.firstCard != null && trick.secondCard != null && trick.thirdCard != null) {
                        setUserPosition(userPosition!!)
                        addCard(trick.foreHand, trick.firstCard)
                        addCard(trick.foreHand.leftNeighbor, trick.secondCard)
                        addCard(trick.foreHand.rightNeighbor, trick.thirdCard)
                    }
                }
            }
        }
    }

    fun setUserPosition(player: Player) {
        userPosition = player
        trickPanels.forEach { it.setUserPosition(player) }
    }

    fun resetPanel() {
        trickPanels.forEach { it.clearCards() }
    }
}
