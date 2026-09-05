package org.jskat.gui.javafx.iss

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
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
        title = strings.getString("invitePlayers")

        val invitations = mutableListOf<PlayerData>()
        val invitationSlots = HBox(SLOT_GAP)
        val availablePlayers = VBox(PLAYER_GAP)
        val seatSection = section(strings.getString("issInvitationTableSeats"), invitationSlots)
        val playerSection = section(strings.getString("issInvitationAvailablePlayer"), availablePlayers)
        val content = VBox(CONTENT_GAP, seatSection, playerSection).apply {
            padding = Insets(CONTENT_PADDING)
        }

        fun refresh() {
            invitationSlots.children.setAll((0 until MAXIMUM_INVITATIONS).map { index ->
                invitations.getOrNull(index)?.let { player ->
                    Button(strings.getString("issInvitationOccupiedPlace", index + 1, player.login)).apply {
                        id = "invitation-place-$index"
                        accessibleText = strings.getString("issInvitationRemovePlayerFromPlace", player.login, index + 1)
                        graphic = bitmaps.getImageView(JSkatGraphicRepository.Icon.CLOSE, JSkatGraphicRepository.IconSize.SMALL)
                        contentDisplay = ContentDisplay.RIGHT
                        graphicTextGap = 10.0
                        setSlotWidth()
                        tooltip = Tooltip(strings.getString("issInvitationRemovePlayer", player.login))
                        setOnAction {
                            invitations.removeAt(index)
                            refresh()
                        }
                    }
                } ?: Button(strings.getString("issInvitationOpenPlace", index + 1)).apply {
                    id = "invitation-place-$index"
                    accessibleText = strings.getString("issInvitationOpenPlaceAccessible", index + 1)
                    isDisable = true
                    setSlotWidth()
                }
            })

            availablePlayers.children.setAll(
                players.sortedBy { it.login }
                    .map { player ->
                        Button().apply {
                            id = "invite-player-${player.login}"
                            accessibleText = strings.getString("issInvitationInvitePlayer", player.login)
                            graphic = playerRow(player)
                            contentDisplay = ContentDisplay.GRAPHIC_ONLY
                            alignment = Pos.CENTER_LEFT
                            prefWidth = PLAYER_ROW_WIDTH
                            maxWidth = PLAYER_ROW_WIDTH
                            minHeight = PLAYER_BUTTON_HEIGHT
                            prefHeight = PLAYER_BUTTON_HEIGHT
                            maxHeight = PLAYER_BUTTON_HEIGHT
                            isDisable = invitations.size == MAXIMUM_INVITATIONS ||
                                (!player.isKIPlayer && player in invitations)
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
        dialogPane.prefWidth = DIALOG_WIDTH
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

    private fun playerRow(player: PlayerData): HBox = HBox(PLAYER_ROW_GAP).apply {
        alignment = Pos.CENTER_LEFT
        prefWidth = PLAYER_ROW_WIDTH
        children.add(playerNameAndFlags(player).apply { HBox.setHgrow(this, Priority.ALWAYS) })
        children.add(Label(String.format(Locale.ROOT, "%.2f", player.strength)).apply {
            alignment = Pos.CENTER_RIGHT
            minWidth = 95.0
        })
    }

    private fun Button.setSlotWidth() {
        minWidth = SLOT_WIDTH
        prefWidth = SLOT_WIDTH
        maxWidth = SLOT_WIDTH
        minHeight = SLOT_BUTTON_HEIGHT
        prefHeight = SLOT_BUTTON_HEIGHT
        maxHeight = SLOT_BUTTON_HEIGHT
    }

    private fun section(title: String, content: Node): VBox = VBox(SECTION_GAP, Label(title).apply {
        styleClass.add("action-panel-section-label")
    }, content)

    private companion object {
        const val MAXIMUM_INVITATIONS = 2
        const val DIALOG_WIDTH = 680.0
        const val CONTENT_PADDING = 28.0
        const val CONTENT_GAP = 18.0
        const val SLOT_GAP = 16.0
        const val PLAYER_GAP = 8.0
        const val SLOT_WIDTH = 304.0
        const val PLAYER_ROW_WIDTH = 624.0
        const val PLAYER_ROW_GAP = 16.0
        const val SECTION_GAP = 7.0
        const val SLOT_BUTTON_HEIGHT = 48.0
        const val PLAYER_BUTTON_HEIGHT = 48.0
    }
}
