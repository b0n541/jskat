package org.jskat.gui.javafx.table

import javafx.scene.layout.FlowPane
import org.jskat.data.GameSummary
import org.jskat.util.Player

class GameResultPanel : FlowPane() {
    private val trickPanels = mutableListOf<TrickPanel>()
    private var userPosition: Player? = null

    init {
        // Set gap between trick panels
        hgap = 10.0
        vgap = 10.0
        
        for (i in 0 until 10) {
            val panel = TrickPanel(0.8, false)
            panel.setPrefSize(200.0, 200.0)
            trickPanels.add(panel)
            children.add(panel)
        }
    }

    fun setGameSummary(summary: GameSummary) {
        val tricks = summary.tricks
        for (i in 0 until 10) {
            val panel = trickPanels[i]
            panel.clearCards()
            if (i < tricks.size) {
                val trick = tricks[i]
                if (userPosition != null) {
                    panel.setUserPosition(userPosition!!)
                    panel.addCard(trick.foreHand, trick.firstCard)
                    panel.addCard(trick.foreHand.leftNeighbor, trick.secondCard)
                    panel.addCard(trick.foreHand.rightNeighbor, trick.thirdCard)
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
