package org.jskat.gui.javafx.table

import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
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
    actions: Map<JSkatAction, AbstractJSkatAction>,
    showReplayGameButton: Boolean = true,
    continueAction: JSkatAction = JSkatAction.CONTINUE_LOCAL_SERIES
) : VBox() {

    private val bitmaps = JSkatGraphicRepository.INSTANCE

    private val gameOverTrickPanel = GameOverTrickPanel()
    private val skatPanel = SkatPanel()

    init {
        style = "-fx-background-color: transparent;"
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        this.spacing = 10.0 // Padding between trick panel and button panel

        children.add(gameOverTrickPanel)
        setVgrow(gameOverTrickPanel, Priority.ALWAYS) // Allow trick panel to scale
        // Ensure trick panel is always visible with a minimum height
        gameOverTrickPanel.minHeight = 100.0
        gameOverTrickPanel.prefHeight = USE_COMPUTED_SIZE // Allow trick panel to grow
        gameOverTrickPanel.maxHeight = USE_COMPUTED_SIZE // Allow trick panel to grow

        val buttonPanel = HBox()
        buttonPanel.spacing = 10.0
        buttonPanel.minHeight = 75.0
        buttonPanel.prefHeight = 75.0 // Fixed height for stable layout
        buttonPanel.maxHeight = 75.0 // Prevent buttonPanel from growing

        buttonPanel.children.add(skatPanel)

        val buttonSpacer = Pane().apply { HBox.setHgrow(this, Priority.ALWAYS) }

        val continueSkatSeriesAction = actions[continueAction]
        val continueSkatSeriesButton =
            Button(continueSkatSeriesAction?.getValue(AbstractJSkatAction.NAME) as? String ?: "").apply {
                graphic = bitmaps.getImageView(JSkatGraphicRepository.Icon.PLAY, JSkatGraphicRepository.IconSize.BIG)
                setOnAction {
                    val event = if (continueAction == JSkatAction.CONTINUE_LOCAL_SERIES) {
                        JSkatActionEvent(tableName, it.source)
                    } else {
                        JSkatActionEvent(continueAction, tableName)
                    }
                    continueSkatSeriesAction?.actionPerformed(event)
                }
            }
        if (showReplayGameButton) {
            val replayGameAction = actions[JSkatAction.REPLAY_GAME]
            val replayGameButton =
                Button(replayGameAction?.getValue(AbstractJSkatAction.NAME) as? String ?: "").apply {
                    graphic = bitmaps.getImageView(JSkatGraphicRepository.Icon.FIRST, JSkatGraphicRepository.IconSize.BIG)
                    setOnAction {
                        replayGameAction?.actionPerformed(JSkatActionEvent(tableName, it.source))
                    }
                }
            buttonPanel.children.addAll(buttonSpacer, replayGameButton, continueSkatSeriesButton)
        } else {
            buttonPanel.children.addAll(buttonSpacer, continueSkatSeriesButton)
        }

        children.add(buttonPanel)
        setVgrow(buttonPanel, Priority.NEVER) // Button panel has fixed height, doesn't compete for space
    }

    fun setUserPosition(player: Player) {
        gameOverTrickPanel.setUserPosition(player)
    }

    fun setGameSummary(summary: GameSummary) {
        gameOverTrickPanel.setGameSummary(summary)
    }

    fun setDealtSkat(skat: CardList) {
        skatPanel.setSkatCards(skat)
    }

    fun resetPanel() {
        gameOverTrickPanel.resetPanel()
        skatPanel.resetPanel()
    }
}
