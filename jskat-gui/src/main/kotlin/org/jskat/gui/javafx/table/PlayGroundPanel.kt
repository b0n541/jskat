package org.jskat.gui.javafx.table

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.jskat.gui.img.JSkatGraphicRepository

class PlayGroundPanel(
    private val gameInfoPanel: GameInformationPanel,
    private val leftOpponentPanel: OpponentPanel,
    private val rightOpponentPanel: OpponentPanel,
    private val gameContextStackPane: StackPane,
    private val userPanel: JSkatUserPanel
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
        // Use minHeight to ensure panels don't completely disappear when window is very small
        rowConstraints.addAll(
            RowConstraints().apply {
                percentHeight = 30.0
                minHeight = 50.0
            },
            RowConstraints().apply {
                percentHeight = 40.0
                minHeight = 100.0
            },
            RowConstraints().apply {
                percentHeight = 30.0
                minHeight = 50.0
            }
        )
        // Configure column to grow
        columnConstraints.add(ColumnConstraints().apply { hgrow = Priority.ALWAYS })

        val opponentBox = HBox(leftOpponentPanel, rightOpponentPanel).apply {
            alignment = Pos.CENTER
            spacing = 10.0
            minWidth = 0.0 // Allow shrinking
        }
        HBox.setHgrow(leftOpponentPanel, Priority.ALWAYS)
        HBox.setHgrow(rightOpponentPanel, Priority.ALWAYS)

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
        // Add some internal padding to the context pane so content doesn't touch the rounded edges
        gameContextStackPane.padding = Insets(10.0)

        add(topArea, 0, 0)
        add(gameContextStackPane, 0, 1)
        add(userPanel, 0, 2)
    }
}
