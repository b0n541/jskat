package org.jskat.gui.javafx.iss

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import org.jskat.data.iss.PlayerData
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.JSkatResourceBundle
import java.util.Locale

class IssPlayerInvitationDialog(players: Collection<PlayerData>) : Dialog<List<String>>() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val bitmaps = JSkatGraphicRepository.INSTANCE

    init {
        title = strings.getString("invite_players")

        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.padding = Insets(20.0)
        grid.columnConstraints.addAll(
            ColumnConstraints(),
            ColumnConstraints().apply { hgrow = Priority.ALWAYS },
            ColumnConstraints().apply { minWidth = 95.0 }
        )

        val playerSelections = players.sortedBy { it.login }.associateWith { CheckBox() }

        var row = 0
        playerSelections.forEach { (player, selection) ->
            grid.add(selection, 0, row)
            grid.add(playerNameAndFlags(player), 1, row)
            grid.add(Label(String.format(Locale.ROOT, "%.2f", player.strength)).apply {
                alignment = Pos.CENTER_RIGHT
                maxWidth = Double.MAX_VALUE
            }, 2, row)

            row++
        }

        dialogPane.content = grid
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        playerSelections.values.forEach { selection ->
            selection.selectedProperty().addListener { _, _, _ ->
                val maximumSelected = playerSelections.values.count { it.isSelected } >= MAXIMUM_INVITATIONS
                playerSelections.values.forEach { checkbox ->
                    checkbox.isDisable = maximumSelected && !checkbox.isSelected
                }
            }
        }

        setResultConverter { dialogButton ->
            if (dialogButton == ButtonType.OK) {
                playerSelections.filterValues { it.isSelected }.keys.map { it.login }
            } else {
                null
            }
        }
    }

    private fun playerNameAndFlags(player: PlayerData): HBox = HBox(4.0).apply {
        alignment = Pos.CENTER_LEFT
        children.add(Label(player.login))
        children.addAll(languageFlagImageViews(player.languages.orEmpty(), bitmaps))
    }

    private companion object {
        const val MAXIMUM_INVITATIONS = 2
    }
}
