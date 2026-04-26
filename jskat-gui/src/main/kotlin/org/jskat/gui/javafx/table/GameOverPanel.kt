package org.jskat.gui.javafx.table

import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.GameSummary
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.CardList
import org.jskat.util.Player

class GameOverPanel(
    private val tableName: String,
    actions: Map<JSkatAction, AbstractJSkatAction>
) : BorderPane() {

    private val bitmaps = JSkatGraphicRepository.INSTANCE

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

        val continueSkatSeriesAction = actions[JSkatAction.CONTINUE_LOCAL_SERIES]
        val continueSkatSeriesButton =
            Button(continueSkatSeriesAction?.getValue(AbstractJSkatAction.NAME) as? String ?: "").apply {
                graphic = bitmaps.getImageView(JSkatGraphicRepository.Icon.PLAY, JSkatGraphicRepository.IconSize.BIG)
                setOnAction {
                    continueSkatSeriesAction?.actionPerformed(JSkatActionEvent(tableName, it.source))
                }
            }

        buttonPanel.children.add(continueSkatSeriesButton)
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
