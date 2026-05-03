package org.jskat.gui.javafx.iss

import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import org.jskat.util.JSkatResourceBundle

class IssPlayerInvitationDialog(playerNames: Set<String>) : Dialog<List<String>>() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val firstPlayerGroup = ToggleGroup()
    private val secondPlayerGroup = ToggleGroup()

    init {
        title = strings.getString("invite_players")

        dialogPane.stylesheets.add("/org/jskat/gui/javafx/jskat.css")

        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.padding = Insets(20.0)

        var row = 0
        playerNames.sorted().forEach { playerName ->
            grid.add(Label(playerName), 0, row)

            val firstButton = RadioButton()
            firstButton.userData = playerName
            firstButton.toggleGroup = firstPlayerGroup
            grid.add(firstButton, 1, row)

            val secondButton = RadioButton()
            secondButton.userData = playerName
            secondButton.toggleGroup = secondPlayerGroup
            grid.add(secondButton, 2, row)

            row++
        }

        dialogPane.content = grid
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        setResultConverter { dialogButton ->
            if (dialogButton == ButtonType.OK) {
                val result = mutableListOf<String>()
                val first = firstPlayerGroup.selectedToggle?.userData as? String
                if (first != null) result.add(first)

                val second = secondPlayerGroup.selectedToggle?.userData as? String
                if (second != null) result.add(second)

                result
            } else {
                null
            }
        }
    }
}
