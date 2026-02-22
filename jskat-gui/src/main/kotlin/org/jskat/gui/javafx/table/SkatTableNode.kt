package org.jskat.gui.javafx.table

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.embed.swing.SwingNode
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jskat.control.JSkatEventBus
import org.jskat.control.event.skatgame.GameFinishEvent
import org.jskat.control.event.table.PlayerNamesChangedEvent
import org.jskat.control.event.table.SkatGameReplayFinishedEvent
import org.jskat.control.event.table.SkatGameReplayStartedEvent
import org.jskat.control.event.table.SkatSeriesStartedEvent
import org.jskat.gui.swing.iss.ISSTablePanel
import org.jskat.gui.swing.table.SkatTablePanel
import org.jskat.util.JSkatResourceBundle

class SkatTableNode(val skatTablePanel: SkatTablePanel) : SplitPane() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val scoreListTableView = ScoreListTableView(listOf("1", "2", "3"))
    private var replay = false

    init {
        val swingNode = SwingNode()
        swingNode.content = skatTablePanel

        items.addAll(getLeftPanel(), swingNode)
        Platform.runLater {
            setDividerPositions(0.2)
        }

        JSkatEventBus.TABLE_EVENT_BUSSES[skatTablePanel.name]?.register(this)
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

        if (skatTablePanel is ISSTablePanel) {
            val chatSwingNode = SwingNode()
            chatSwingNode.content = skatTablePanel.chatPanel
            val chatTab = Tab(strings.getString("chat"), chatSwingNode)
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
            Platform.runLater {
                scoreListTableView.items.add(
                    if (event.declarerName != null) {
                        ScoreListEntry(mapOf(event.declarerName to event.gameSummary.gameValue))
                    } else {
                        ScoreListEntry(mapOf("" to 0))
                    }
                )
                scoreListTableView.scrollTo(scoreListTableView.items.size - 1)
            }
        }
    }

    @Subscribe
    fun clearSkatListOn(event: SkatSeriesStartedEvent) {
        Platform.runLater { scoreListTableView.items.clear() }
    }

    @Subscribe
    fun setPlayerNamesOn(event: PlayerNamesChangedEvent) {
        Platform.runLater {
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
