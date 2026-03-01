package org.jskat.gui.javafx.table

import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.GameSummary
import org.jskat.util.CardList
import org.jskat.util.Player
import javax.swing.ActionMap
import java.awt.event.ActionEvent

class GameOverPanel(
    actions: ActionMap,
    activeActions: List<JSkatAction>
) : BorderPane() {

    private val gameResultPanel = GameResultPanel()
    private val skatPanel = SkatPanel()

    init {
        style = "-fx-background-color: transparent;"
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        center = gameResultPanel

        val buttonPanel = HBox()
        buttonPanel.spacing = 10.0
        
        HBox.setHgrow(skatPanel, Priority.ALWAYS)
        buttonPanel.children.add(skatPanel)
        
        for (action in activeActions) {
            val swingAction = actions.get(action)
            val button = Button(swingAction.getValue(javax.swing.Action.NAME) as String)
            button.setOnAction { 
                // Trigger Swing action
                swingAction.actionPerformed(ActionEvent(this, ActionEvent.ACTION_PERFORMED, null))
            }
            buttonPanel.children.add(button)
        }
        
        bottom = buttonPanel
    }

    fun setUserPosition(player: Player) {
        gameResultPanel.setUserPosition(player)
    }

    fun setGameSummary(summary: GameSummary) {
        gameResultPanel.setGameSummary(summary)
    }

    fun setDealtSkat(skat: CardList) {
        skatPanel.setSkatCards(skat)
    }

    fun resetPanel() {
        gameResultPanel.resetPanel()
        skatPanel.resetPanel()
    }
}
