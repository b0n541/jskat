package org.jskat.gui.javafx.table

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jskat.control.JSkatEventBus
import org.jskat.control.event.skatgame.GameFinishEvent
import org.jskat.control.event.skatgame.GameStartedEvent
import org.jskat.control.event.table.PlayerNamesChangedEvent
import org.jskat.control.event.table.SkatGameReplayFinishedEvent
import org.jskat.control.event.table.SkatGameReplayStartedEvent
import org.jskat.control.event.table.SkatSeriesStartedEvent
import org.jskat.gui.javafx.iss.IssTablePanel
import org.jskat.util.JSkatResourceBundle

class SkatTableNode(val skatTablePanel: SkatTablePanel) : SplitPane() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val scoreHistory = ScoreHistoryProjection(listOf("1", "2", "3"))
    private val scoreListTableView = ScoreListTableView(scoreHistory.playerNames)
    private var replay = false
    private lateinit var playerOrder: ScoreHistoryPlayerOrder

    init {
        items.addAll(getLeftPanel(), skatTablePanel)
        Platform.runLater {
            setDividerPositions(0.2)
        }

        JSkatEventBus.TABLE_EVENT_BUSSES[skatTablePanel.tableName]?.register(this)
    }

    private fun getLeftPanel(): VBox {
        scoreListTableView.items = FXCollections.observableArrayList(FXCollections.emptyObservableList())
        VBox.setVgrow(scoreListTableView, Priority.ALWAYS)

        val vbox = VBox()
        vbox.children.add(scoreListTableView)
        vbox.padding = javafx.geometry.Insets(5.0)

        val scoreListTab = Tab(strings.getString("score_sheet"), vbox)
        val tabPane = TabPane()
        tabPane.tabs.add(scoreListTab)

        if (skatTablePanel is IssTablePanel) {
            val chatTab = Tab(strings.getString("chat"), skatTablePanel.getChatPanel())
            tabPane.tabs.add(chatTab)
        }

        tabPane.stylesheets.add("/org/jskat/gui/javafx/jskat.css")

        val result = VBox()
        VBox.setVgrow(tabPane, Priority.ALWAYS)
        result.children.add(tabPane)
        return result
    }

    @Subscribe
    fun addGameResultOn(event: GameFinishEvent) {
        if (!replay) {
            val completedGamePlayerOrder = playerOrder
            Platform.runLater {
                scoreHistory.addResult(completedGamePlayerOrder, event.gameSummary)
                scoreListTableView.items.setAll(scoreHistory.rows)
                scoreListTableView.scrollTo(scoreListTableView.items.size - 1)
            }
        }
    }

    @Subscribe
    fun setPlayerOrderOn(event: GameStartedEvent) {
        playerOrder = ScoreHistoryPlayerOrder(
            event.leftPlayerPosition(),
            event.rightPlayerPosition(),
            event.userPosition(),
        )
    }

    @Subscribe
    fun clearSkatListOn(event: SkatSeriesStartedEvent) {
        Platform.runLater {
            scoreHistory.clear()
            scoreListTableView.items.clear()
        }
    }

    @Subscribe
    fun setPlayerNamesOn(event: PlayerNamesChangedEvent) {
        Platform.runLater {
            scoreHistory.setPlayerNames(
                event.upperLeftPlayerName,
                event.upperRightPlayerName,
                event.lowerPlayerName
            )
            scoreListTableView.setPlayerNames(
                event.upperLeftPlayerName,
                event.upperRightPlayerName,
                event.lowerPlayerName
            )
        }
    }

    @Subscribe
    fun setReplayModeOn(event: SkatGameReplayStartedEvent) {
        replay = true
    }

    @Subscribe
    fun setReplayModeOn(event: SkatGameReplayFinishedEvent) {
        replay = false
    }
}
