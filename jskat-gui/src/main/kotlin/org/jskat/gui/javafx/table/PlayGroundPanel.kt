package org.jskat.gui.javafx.table

import javafx.geometry.Insets
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
        rowConstraints.addAll(
            RowConstraints().apply { percentHeight = 30.0 },
            RowConstraints().apply { percentHeight = 40.0 },
            RowConstraints().apply { percentHeight = 30.0 }
        )
        // Configure column to grow
        columnConstraints.add(ColumnConstraints().apply { hgrow = Priority.ALWAYS })

        val gameInfoBox = HBox(gameInfoPanel).apply {
            padding = Insets(5.0)
            alignment = javafx.geometry.Pos.CENTER
            minHeight = 0.0 // Allow gameInfoBox to shrink
        }

        val opponentBox = HBox(leftOpponentPanel, rightOpponentPanel).apply {
            padding = Insets(5.0)
            alignment = javafx.geometry.Pos.CENTER
            spacing = 10.0
        }
        HBox.setHgrow(leftOpponentPanel, Priority.ALWAYS)
        HBox.setHgrow(rightOpponentPanel, Priority.ALWAYS)

        val topArea = VBox(gameInfoBox, opponentBox).apply {
            alignment = javafx.geometry.Pos.CENTER
            minHeight = 0.0 // Allow topArea to shrink
        }
        VBox.setVgrow(gameInfoBox, Priority.NEVER) // gameInfoBox takes only its preferred height
        VBox.setVgrow(opponentBox, Priority.ALWAYS) // opponentBox takes all remaining space and shrinks

        add(topArea, 0, 0)
        add(gameContextStackPane, 0, 1)
        add(userPanel, 0, 2)
    }
}
