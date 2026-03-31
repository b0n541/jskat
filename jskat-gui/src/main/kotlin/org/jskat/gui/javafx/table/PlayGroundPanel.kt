package org.jskat.gui.javafx.table

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
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

        val gameInfoBox = HBox(gameInfoPanel).apply {
            padding = Insets(5.0)
            alignment = Pos.CENTER
            minHeight = USE_PREF_SIZE
            minWidth = 0.0 // Allow shrinking
            HBox.setHgrow(gameInfoPanel, Priority.ALWAYS)
        }

        val opponentBox = HBox(leftOpponentPanel, rightOpponentPanel).apply {
            padding = Insets(5.0)
            alignment = Pos.CENTER
            spacing = 10.0
            minWidth = 0.0 // Allow shrinking
        }
        HBox.setHgrow(leftOpponentPanel, Priority.ALWAYS)
        HBox.setHgrow(rightOpponentPanel, Priority.ALWAYS)

        val topArea = VBox(gameInfoBox, opponentBox).apply {
            alignment = Pos.TOP_CENTER // Use TOP_CENTER to prevent gameInfoPanel from being clipped first
            minHeight = 0.0
            minWidth = 0.0 // Allow shrinking
        }
        VBox.setVgrow(gameInfoBox, Priority.NEVER)
        VBox.setVgrow(opponentBox, Priority.ALWAYS)

        add(topArea, 0, 0)
        add(gameContextStackPane, 0, 1)
        add(userPanel, 0, 2)
    }
}
