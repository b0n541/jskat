package org.jskat.gui.javafx.iss

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jskat.control.event.iss.IssTableGameStartedEvent
import org.jskat.control.event.iss.IssTableStateChangedEvent
import org.jskat.control.event.skatgame.GameStartedEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.iss.GameStartInformation
import org.jskat.data.iss.TablePanelStatus
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.javafx.table.ContextPanelType
import org.jskat.gui.javafx.table.JSkatUserPanel
import org.jskat.gui.javafx.table.OpponentPanel
import org.jskat.gui.javafx.table.SkatTablePanel
import org.jskat.util.GameVariant
import org.jskat.util.Player

class IssTablePanel(tableName: String, actions: Map<JSkatAction, AbstractJSkatAction>) :
    SkatTablePanel(tableName, actions) {

    private lateinit var chatPanel: IssChatPanel
    private var lastTableStatus: TablePanelStatus? = null

    override fun initPanel() {
        // Don't call super.initPanel() as we are replacing the entire layout

        chatPanel = IssChatPanel(actions)
        chatPanel.addNewChat(strings.getString("table") + " " + tableName, tableName)

        val playGroundPanel = super.getPlayGroundPanel()
        val issContent = HBox(playGroundPanel, chatPanel)
        HBox.setHgrow(playGroundPanel, Priority.ALWAYS)

        center = issContent

        // Replace the default start panel with the ISS version
        val issStartActions = listOf(
            JSkatAction.INVITE_ISS_PLAYER,
            JSkatAction.READY_TO_PLAY,
            JSkatAction.TALK_ENABLED,
            JSkatAction.LEAVE_ISS_TABLE
        )
        val issStartPanel = IssStartContextPanel(tableName, actions, issStartActions)
        addContextPanel(ContextPanelType.START, issStartPanel)
        setContextPanel(ContextPanelType.START)
    }

    override fun getOpponentPanel(): OpponentPanel {
        return OpponentPanel(12, true)
    }

    override fun createPlayerPanel(): JSkatUserPanel {
        return JSkatUserPanel(tableName, 12, true, actions)
    }

    override fun getRightPanelForTrickPanel(): Pane {
        val additionalActionsPanel = VBox()
        additionalActionsPanel.spacing = 10.0

        val resignAction = actions[JSkatAction.RESIGN]
        if (resignAction != null) {
            val resignButton =
                Button(resignAction.getValue(AbstractJSkatAction.NAME) as? String ?: JSkatAction.RESIGN.name).apply {
                    graphic =
                        bitmaps.getImageView(
                            JSkatGraphicRepository.Icon.WHITE_FLAG,
                            JSkatGraphicRepository.IconSize.BIG
                        )
                    setOnAction {
                        resignAction.actionPerformed(JSkatActionEvent(JSkatAction.RESIGN, it.source))
                    }
                }
            additionalActionsPanel.children.add(resignButton)
        }

        val showCardsAction = actions[JSkatAction.SHOW_CARDS]
        if (showCardsAction != null) {
            val showCardsButton =
                Button(showCardsAction.getValue(AbstractJSkatAction.NAME) as? String ?: JSkatAction.SHOW_CARDS.name)
            showCardsButton.setOnAction {
                showCardsAction.actionPerformed(JSkatActionEvent(JSkatAction.SHOW_CARDS, it.source))
            }
            additionalActionsPanel.children.add(showCardsButton)
        }

        return additionalActionsPanel
    }

    // TODO: this does similar things like SkatTablePanel.resetTableOn(event: GameStartEvent)
    @Subscribe
    fun resetTableOn(event: IssTableGameStartedEvent) {
        Platform.runLater {
            val gameStart = event.gameStart
            val userLogin = gameStart.loginName
            val playerNames = gameStart.playerNames

            var userPosition: Player? = null
            for ((key, value) in playerNames) {
                if (value == userLogin) {
                    userPosition = key
                    break
                }
            }

            if (userPosition != null) {
                val leftOpponent = userPosition.leftNeighbor
                val rightOpponent = userPosition.rightNeighbor
                clearTable(leftOpponent, rightOpponent, userPosition, gameStart)
            }

            clearTable()
        }
    }

    private fun clearTable(
        leftOpponent: Player,
        rightOpponent: Player,
        player: Player,
        gameStart: GameStartInformation
    ) {
        resetTableOn(GameStartedEvent(gameStart.gameNo, GameVariant.STANDARD, leftOpponent, rightOpponent, player))

        setPlayerName(leftOpponent, gameStart.playerNames[leftOpponent]!!)
        setPlayerTime(leftOpponent, gameStart.playerTimes[leftOpponent]!!)

        setPlayerName(rightOpponent, gameStart.playerNames[rightOpponent]!!)
        setPlayerTime(rightOpponent, gameStart.playerTimes[rightOpponent]!!)

        setPlayerName(player, gameStart.playerNames[player]!!)
        setPlayerTime(player, gameStart.playerTimes[player]!!)
    }

    @Subscribe
    fun updateTableStatusOn(event: IssTableStateChangedEvent) {
        val tableStatus = event.status

        Platform.runLater {
            for ((playerName, status) in tableStatus.playerInformation) {
                if (!status.isPlayerLeft) {
                    addPlayerName(playerName)
                }
                setPlayerReadyToPlay(playerName, status.isReadyToPlay)
                setPlayerChatEnabled(playerName, status.isTalkEnabled)
                if (status.isPlayerLeft) {
                    removePlayerName(playerName)
                }
            }
            lastTableStatus = tableStatus
        }
    }

    private fun addPlayerName(playerName: String) {
        if (!playerNamesAndPositions.containsKey(playerName)) {
            if (userPanel.playerName == null || userPanel.playerName!!.isEmpty()) {
                userPanel.playerName = playerName
                userPanel.position?.let { playerNamesAndPositions[playerName] = it }
            } else if (leftOpponentPanel.playerName == null || leftOpponentPanel.playerName!!.isEmpty()) {
                leftOpponentPanel.playerName = playerName
                leftOpponentPanel.position?.let { playerNamesAndPositions[playerName] = it }
            } else if (rightOpponentPanel.playerName == null || rightOpponentPanel.playerName!!.isEmpty()) {
                rightOpponentPanel.playerName = playerName
                rightOpponentPanel.position?.let { playerNamesAndPositions[playerName] = it }
            } else {
                playerNamesAndPositions[playerName] = null
            }
        }
    }

    private fun removePlayerName(playerName: String) {
        if (playerNamesAndPositions.containsKey(playerName)) {
            playerNamesAndPositions.remove(playerName)
            when (playerName) {
                userPanel.playerName -> userPanel.playerName = ""
                leftOpponentPanel.playerName -> leftOpponentPanel.playerName = ""
                rightOpponentPanel.playerName -> rightOpponentPanel.playerName = ""
            }
        }
    }

    private fun setPlayerName(player: Player, playerName: String) {
        Platform.runLater {
            super.getHandPanel(player).playerName = playerName
        }
    }

    private fun setPlayerReadyToPlay(playerName: String, readyToPlay: Boolean) {
        // Platform.runLater {
        super.getHandPanel(playerName)?.setReadyToPlay(readyToPlay)
        // }
    }

    private fun setPlayerChatEnabled(playerName: String, chatEnabled: Boolean) {
        // Platform.runLater {
        super.getHandPanel(playerName)?.setChatEnabled(chatEnabled)
        // }
    }

    fun getChatPanel(): IssChatPanel {
        return chatPanel
    }
}