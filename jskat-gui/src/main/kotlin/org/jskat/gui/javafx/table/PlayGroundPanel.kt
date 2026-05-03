package org.jskat.gui.javafx.table

import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.jskat.gui.img.JSkatGraphicRepository

class PlayGroundPanel(
    gameInfoPanel: GameInformationPanel,
    leftOpponentPanel: OpponentPanel,
    rightOpponentPanel: OpponentPanel,
    gameContextStackPane: StackPane,
    userPanel: JSkatUserPanel
) : GridPane() {

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

        padding = Insets(10.0)
        vgap = 10.0
        hgap = 10.0

        // Configure rows for 30/40/30 distribution
        rowConstraints.addAll(
            RowConstraints().apply {
                percentHeight = 30.0
                valignment = VPos.CENTER
                vgrow = Priority.ALWAYS
            },
            RowConstraints().apply {
                percentHeight = 40.0
                valignment = VPos.CENTER
                vgrow = Priority.ALWAYS
            },
            RowConstraints().apply {
                percentHeight = 30.0
                valignment = VPos.CENTER
                vgrow = Priority.ALWAYS
            }
        )
        // Configure column to grow
        columnConstraints.add(ColumnConstraints().apply {
            hgrow = Priority.ALWAYS
            halignment = HPos.CENTER
        })

        val opponentBox = HBox(leftOpponentPanel, rightOpponentPanel).apply {
            alignment = Pos.CENTER
            spacing = 10.0
            minWidth = 0.0 // Allow shrinking
        }
        HBox.setHgrow(leftOpponentPanel, Priority.ALWAYS)
        HBox.setHgrow(rightOpponentPanel, Priority.ALWAYS)
        leftOpponentPanel.prefWidth = 0.0 // Ensure equal distribution
        rightOpponentPanel.prefWidth = 0.0 // Ensure equal distribution

        val topArea = VBox(gameInfoPanel, opponentBox).apply {
            alignment = Pos.TOP_CENTER
            spacing = 10.0
            minHeight = 0.0
            minWidth = 0.0
        }
        VBox.setVgrow(opponentBox, Priority.ALWAYS)

        gameContextStackPane.background = Background(
            BackgroundFill(
                Color.rgb(255, 255, 255, 0.2),
                CornerRadii(10.0),
                null
            )
        )
        gameContextStackPane.padding = Insets(10.0)
        gameContextStackPane.minHeight = 0.0
        gameContextStackPane.minWidth = 0.0

        // Add clipping to the middle panel to prevent it from overflowing into other rows
        val clipRect = Rectangle()
        clipRect.widthProperty().bind(gameContextStackPane.widthProperty())
        clipRect.heightProperty().bind(gameContextStackPane.heightProperty())
        gameContextStackPane.clip = clipRect

        add(topArea, 0, 0)
        add(gameContextStackPane, 0, 1)
        add(userPanel, 0, 2)
    }
}
