package org.jskat.gui.javafx.main

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.control.command.iss.IssDisconnectCommand
import org.jskat.control.command.iss.IssInvitePlayerCommand
import org.jskat.control.command.iss.IssShowLoginCommand
import org.jskat.control.command.table.RequestCreateTableCommand
import org.jskat.control.command.table.StartSkatSeriesCommand
import org.jskat.control.event.iss.*
import org.jskat.control.event.table.TableCreatedEvent
import org.jskat.control.event.table.TableRemovedEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.JSkatApplicationData
import org.jskat.data.JSkatViewType
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.IconSize
import org.jskat.gui.javafx.JSkatMenuFactory
import org.jskat.gui.javafx.iss.LobbyPanel
import org.jskat.gui.javafx.iss.LoginPanel
import org.jskat.gui.javafx.table.ISSTablePanel
import org.jskat.gui.javafx.table.SkatSeriesStartDialog
import org.jskat.gui.javafx.table.SkatTableNode
import org.jskat.gui.javafx.table.SkatTablePanel
import org.jskat.util.JSkatResourceBundle

class JSkatMainWindowFX : VBox() {

    private val jskatMaster: JSkatMaster = JSkatMaster.INSTANCE
    private val data: JSkatApplicationData = JSkatApplicationData.INSTANCE
    private val strings: JSkatResourceBundle = JSkatResourceBundle.INSTANCE
    private val bitmaps: JSkatGraphicRepository = JSkatGraphicRepository.INSTANCE
    private val actions = JSkatActions.actionMap

    private val tabs: TabPane = TabPane()
    private lateinit var issLobby: LobbyPanel

    init {
        JSkatEventBus.INSTANCE.register(this)

        val menuBar = JSkatMenuFactory.build()

        val toolbar = ToolBar()
        toolbar.items.addAll(
            createToolbarButton(actions[JSkatAction.CREATE_LOCAL_TABLE]!!),
            createToolbarButton(actions[JSkatAction.SHOW_ISS_LOGIN]!!),
            createToolbarButton(actions[JSkatAction.PREFERENCES]!!),
            createToolbarButton(actions[JSkatAction.HELP]!!),
            createToolbarButton(actions[JSkatAction.ABOUT_JSKAT]!!)
        )
        setVgrow(tabs, Priority.ALWAYS)

        children.addAll(menuBar, toolbar, tabs)

        tabs.selectionModel.selectedItemProperty().addListener { _, _, newTab ->
            if (newTab != null) {
                // assume that the title of the tab is the table name
                jskatMaster.setActiveTable(newTab.text)
            }
        }

        addWelcomeTab()
    }

    @Subscribe
    fun onTableCreated(event: TableCreatedEvent) {
        val tableName = event.tableName()
        var tabId: String = ""
        val panel = when (event.tableType()) {
            JSkatViewType.LOCAL_TABLE -> {
                tabId = "LOCAL_TABLE{$tableName}"
                val skatTablePanel = SkatTablePanel(tableName, actions)
                SkatTableNode(skatTablePanel)
            }

            JSkatViewType.ISS_TABLE -> {
                tabId = "ISS_TABLE{$tableName}"
                val issTablePanel = ISSTablePanel(tableName, actions)
                SkatTableNode(issTablePanel)
            }

            else -> null
        }

        if (panel != null) {
            Platform.runLater {
                val newTab = Tab(tableName).apply {
                    id = tabId
                    content = panel
                    isClosable = true
                }
                tabs.tabs.add(newTab)
                tabs.selectionModel.select(newTab)

                if (event.tableType() == JSkatViewType.LOCAL_TABLE) {
                    actions[JSkatAction.START_LOCAL_SERIES]?.isEnabled = true
                }
            }
        }
    }

    @Subscribe
    fun onTableRemoved(event: TableRemovedEvent) {
        Platform.runLater {
            tabs.tabs
                .filter { it.text == event.tableName() }
                .forEach { tabs.tabs.remove(it) }
        }
    }

    @Subscribe
    fun onStartSkatSeries(command: StartSkatSeriesCommand) {
        Platform.runLater {
            SkatSeriesStartDialog(this).showAndWaitAndStartSeries()
        }
    }

    @Subscribe
    fun showIssLoginOn(command: IssShowLoginCommand) {
        Platform.runLater {
            val loginPanel: VBox = LoginPanel(actions)

            val tab = Tab(strings.getString("iss_login")).apply {
                id = "ISS_LOGIN"
                isClosable = true
                content = loginPanel
            }

            tabs.tabs.add(tab)
            tabs.selectionModel.select(tab)
        }
    }

    @Subscribe
    fun showIssLobbyOn(event: IssConnectedEvent) {
        Platform.runLater {
            tabs.tabs.removeIf { it.id == "ISS_LOGIN" }

            issLobby = LobbyPanel(actions, event.userName)
            val scrollPane = ScrollPane(issLobby).apply {
                isFitToWidth = true
                isFitToHeight = true
            }
            issLobby.prefWidthProperty().bind(scrollPane.widthProperty())
            issLobby.prefHeightProperty().bind(scrollPane.heightProperty())

            val tab = Tab(strings.getString("iss_lobby")).apply {
                id = "ISS_LOBBY"
                isClosable = true
                content = scrollPane
            }

            tabs.tabs.add(tab)
            tabs.selectionModel.select(tab)
        }
    }

    @Subscribe
    fun updateIssTableListOn(event: IssTableDataUpdatedEvent) {
        if (::issLobby.isInitialized) {
            Platform.runLater {
                issLobby.updateTable(
                    event.tableName(),
                    event.maxPlayers(),
                    event.gamesPlayed(),
                    event.player1(),
                    event.player2(),
                    event.player3()
                )
            }
        }
    }

    @Subscribe
    fun deleteTableFromIssTableListOn(event: IssTableDeletedEvent) {
        if (::issLobby.isInitialized) {
            Platform.runLater {
                issLobby.removeTable(event.tableName())
            }
        }
    }

    @Subscribe
    fun updateIssPlayerListOn(event: IssPlayerDataUpdatedEvent) {
        if (::issLobby.isInitialized) {
            Platform.runLater {
                issLobby.updatePlayer(
                    event.playerName(),
                    event.language(),
                    event.gamesPlayed(),
                    event.strength()
                )
            }
        }
    }

    @Subscribe
    fun deletePlayerFromIssPlayerListOn(event: IssPlayerLeftEvent) {
        if (::issLobby.isInitialized) {
            Platform.runLater {
                issLobby.removePlayer(event.playerName())
            }
        }
    }

    @Subscribe
    fun onRequestCreateTable(command: RequestCreateTableCommand) {
        Platform.runLater {
            createNewLocalTable()
        }
    }

    @Subscribe
    fun onIssInvitePlayer(command: IssInvitePlayerCommand) {
        Platform.runLater {
            val issPlayerNames = data.availableISSPlayer.toMutableSet()
            issPlayerNames.remove(data.issUserName)

            val players = jskatMaster.view.getPlayerForInvitation(issPlayerNames)
            for (player in players) {
                jskatMaster.issController.invitePlayer(data.activeTable, player)
            }
        }
    }

    @Subscribe
    fun closeAllIssTabsOn(event: IssDisconnectCommand) {
        tabs.tabs.removeIf {
            it.id != null && (it.id == "ISS_LOBBY" || it.id.startsWith("ISS_TABLE"))
        }
    }

    private fun createToolbarButton(action: AbstractJSkatAction): Button {
        return Button(action.getValue(AbstractJSkatAction.NAME).toString()).apply {
            graphic = bitmaps.getImageView(action.icon, IconSize.SMALL)
            tooltip = Tooltip(action.tooltip)
            onAction = EventHandler { action.actionPerformed(null) }
        }
    }

    private fun addWelcomeTab() {
        val welcomeTab = Tab(strings.getString("welcome"))
        welcomeTab.isClosable = false
        welcomeTab.content = WelcomePanel(actions)
        tabs.tabs.add(welcomeTab)
    }

    private fun createNewLocalTable() {
        val dialog = TextInputDialog(
            // TODO: set number of local tables created correctly
            "${strings.getString("local.table")} ${jskatMaster.view.getNewTableName(0)}"
        )
        dialog.title = strings.getString("new.table.dialog.title")
        dialog.headerText = strings.getString("new.table.dialog.message")
        dialog.contentText = strings.getString("name")
        dialog.dialogPane.stylesheets.add("/org/jskat/gui/javafx/jskat.css")

        dialog.showAndWait().ifPresent { tableName ->
            if (tableName.isNotEmpty()) {
                jskatMaster.createTable(tableName)
            }
        }
    }
}
