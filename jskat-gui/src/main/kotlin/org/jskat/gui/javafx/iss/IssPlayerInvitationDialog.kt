package org.jskat.gui.javafx.iss

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jskat.data.iss.PlayerData
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.JSkatResourceBundle
import java.util.Locale

class IssPlayerInvitationDialog(players: Collection<PlayerData>) : Dialog<List<String>>() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val bitmaps = JSkatGraphicRepository.INSTANCE

    init {
        title = strings.getString("invite_players")

        val invitations = mutableListOf<PlayerData>()
        val invitationSlots = HBox(10.0)
        val availablePlayers = VBox(4.0)
        val content = VBox(10.0, invitationSlots, availablePlayers).apply {
            padding = Insets(20.0)
        }

        fun refresh() {
            invitationSlots.children.setAll((0 until MAXIMUM_INVITATIONS).map { index ->
                invitations.getOrNull(index)?.let { player ->
                    Button("${index + 1}. ${player.login}").apply {
                        tooltip = Tooltip("Remove ${player.login}")
                        setOnAction {
                            invitations.removeAt(index)
                            refresh()
                        }
                    }
                } ?: Label("${index + 1}. Open place")
            })

            availablePlayers.children.setAll(
                players.sortedBy { it.login }
                    .filter { player -> player.isKIPlayer || player !in invitations }
                    .map { player ->
                        Button().apply {
                            accessibleText = "Invite ${player.login}"
                            graphic = playerRow(player)
                            contentDisplay = ContentDisplay.GRAPHIC_ONLY
                            alignment = Pos.CENTER_LEFT
                            maxWidth = Double.MAX_VALUE
                            isDisable = invitations.size == MAXIMUM_INVITATIONS
                            setOnAction {
                                invitations += player
                                refresh()
                            }
                        }
                    }
            )
        }

        refresh()
        dialogPane.content = content
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        setResultConverter { dialogButton ->
            if (dialogButton == ButtonType.OK) {
                invitations.map { it.login }
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

    private fun playerRow(player: PlayerData): HBox = HBox(10.0).apply {
        alignment = Pos.CENTER_LEFT
        children.add(playerNameAndFlags(player).apply { HBox.setHgrow(this, Priority.ALWAYS) })
        children.add(Label(String.format(Locale.ROOT, "%.2f", player.strength)).apply {
            alignment = Pos.CENTER_RIGHT
            minWidth = 95.0
        })
    }

    private companion object {
        const val MAXIMUM_INVITATIONS = 2
    }
}
