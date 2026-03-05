package org.jskat.gui.javafx.table

import javafx.geometry.Insets
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.jskat.gui.img.JSkatGraphicRepository

class PlayGroundPanel(
    private val gameInfoPanel: GameInformationPanel,
    private val leftOpponentPanel: OpponentPanel,
    private val rightOpponentPanel: OpponentPanel,
    private val gameContextStackPane: StackPane,
    private val userPanel: JSkatUserPanel
) : BorderPane() {

    init {
        val backgroundImage = JSkatGraphicRepository.INSTANCE.skatTableImageFX
        val background = Background(
            BackgroundImage(
                backgroundImage,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
            )
        )
        this.background = background

        val topBox = HBox(gameInfoPanel).apply {
            padding = Insets(5.0)
            alignment = javafx.geometry.Pos.CENTER
        }
        this.top = topBox

        val centerBox = HBox(leftOpponentPanel, rightOpponentPanel).apply {
            padding = Insets(5.0)
            alignment = javafx.geometry.Pos.CENTER
            spacing = 10.0
        }
        HBox.setHgrow(leftOpponentPanel, Priority.ALWAYS)
        HBox.setHgrow(rightOpponentPanel, Priority.ALWAYS)
        
        val mainContent = VBox(centerBox, gameContextStackPane, userPanel).apply {
            alignment = javafx.geometry.Pos.CENTER
            spacing = 10.0
        }
        VBox.setVgrow(gameContextStackPane, Priority.ALWAYS)
        VBox.setVgrow(centerBox, Priority.ALWAYS)
        VBox.setVgrow(userPanel, Priority.ALWAYS)

        this.center = mainContent
    }
}
