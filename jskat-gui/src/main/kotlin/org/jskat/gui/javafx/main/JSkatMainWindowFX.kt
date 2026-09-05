package org.jskat.gui.javafx.main

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.control.command.general.HideToolbarCommand
import org.jskat.control.command.general.ShowToolbarCommand
import org.jskat.control.command.iss.IssDisconnectCommand
import org.jskat.control.command.iss.IssInvitePlayerCommand
import org.jskat.control.command.iss.IssShowLoginCommand
import org.jskat.control.command.table.RequestCreateTableCommand
import org.jskat.control.command.table.RemoveTableCommand
import org.jskat.control.command.table.StartSkatSeriesCommand
import org.jskat.control.event.iss.*
import org.jskat.control.event.table.SkatGameStateChangedEvent
import org.jskat.control.event.table.TableCreatedEvent
import org.jskat.control.event.table.TableRemovedEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.JSkatApplicationData
import org.jskat.data.JSkatOptions
import org.jskat.data.JSkatViewType
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.IconSize
import org.jskat.gui.javafx.JSkatMenuFactory
import org.jskat.gui.javafx.iss.IssLobbyPanel
import org.jskat.gui.javafx.iss.IssLoginPanel
import org.jskat.gui.javafx.iss.IssTablePanel
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
    private val toolbar = ToolBar()
    private val toolbarVisibility = JavaFxGlobalControls.ToolbarVisibility()
    private lateinit var issLobby: IssLobbyPanel

    init {
        JSkatEventBus.INSTANCE.register(this)

        val menuBar = JSkatMenuFactory.build()

        toolbar.items.addAll(
            createToolbarButton(actions[JSkatAction.CREATE_LOCAL_TABLE]!!),
            createToolbarButtonWithActiveTableContext(actions[JSkatAction.START_LOCAL_SERIES]!!),
            createToolbarButton(actions[JSkatAction.SHOW_ISS_LOGIN]!!),
            createToolbarButtonWithActiveTableContext(actions[JSkatAction.REPLAY_GAME]!!),
            createToolbarButtonWithActiveTableContext(actions[JSkatAction.NEXT_REPLAY_STEP]!!),
            createToolbarButton(actions[JSkatAction.PREFERENCES]!!),
            createToolbarButton(actions[JSkatAction.HELP]!!),
            createToolbarButton(actions[JSkatAction.ABOUT_JSKAT]!!)
        )
        setVgrow(tabs, Priority.ALWAYS)

        children.addAll(menuBar, toolbar, tabs)

        tabs.selectionModel.selectedItemProperty().addListener { _, _, newTab ->
            JavaFxTableLifecycle.selectTable(
                newTab?.userData as? JavaFxTableLifecycle.Table,
                activateTable = { table ->
                jskatMaster.setActiveTable(table.type, table.name)
                },
                requestFocus = { (newTab?.content as? SkatTableNode)?.skatTablePanel?.requestFocus() }
            )
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
                val issTablePanel = IssTablePanel(tableName, actions)
                SkatTableNode(issTablePanel)
            }

            else -> null
        }

        if (panel != null) {
            Platform.runLater {
                val newTab = Tab(tableName).apply {
                    id = tabId
                    content = panel
                    userData = JavaFxTableLifecycle.Table(event.tableType(), tableName)
                    isClosable = true
                    setOnCloseRequest { closeEvent ->
                        closeEvent.consume()
                        closeTable(userData as JavaFxTableLifecycle.Table)
                    }
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
    fun hideToolbarOn(command: HideToolbarCommand) {
        toolbarVisibility.hide(command)
        Platform.runLater {
            renderToolbarVisibility()
        }
    }

    @Subscribe
    fun showToolbarOn(command: ShowToolbarCommand) {
        toolbarVisibility.show(command)
        Platform.runLater {
            renderToolbarVisibility()
        }
    }

    @Subscribe
    fun setGameStateOn(event: SkatGameStateChangedEvent) {
        Platform.runLater {
            JavaFxGlobalControls.applyGameState(
                data.activeTable,
                event,
                JSkatOptions.instance().isPlayContra,
                actions
            )
        }
    }

    @Subscribe
    fun onTableRemoved(event: TableRemovedEvent) {
        Platform.runLater {
            tabs.tabs
                .filter {
                    it.userData == JavaFxTableLifecycle.Table(event.tableType(), event.tableName())
                }
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
            val loginPanel = IssLoginPanel(actions)

            val tab = Tab(strings.getString("issLogin")).apply {
                id = "ISS_LOGIN"
                isClosable = true
                content = loginPanel
            }

            tabs.tabs.add(tab)
            tabs.selectionModel.select(tab)
            loginPanel.setFocus()
        }
    }

    @Subscribe
    fun showIssLobbyOn(event: IssConnectedEvent) {
        Platform.runLater {
            tabs.tabs.removeIf { it.id == "ISS_LOGIN" }

            issLobby = IssLobbyPanel(actions, event.userName)
            val scrollPane = ScrollPane(issLobby).apply {
                isFitToWidth = true
                isFitToHeight = true
            }
            issLobby.prefWidthProperty().bind(scrollPane.widthProperty())
            issLobby.prefHeightProperty().bind(scrollPane.heightProperty())

            val tab = Tab(strings.getString("issLobby")).apply {
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
            val issPlayers = data.availableISSPlayers.filter { it.login != data.issUserName }

            val players = jskatMaster.view.getPlayerForInvitation(issPlayers)
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

    fun tablePanel(tableName: String): SkatTablePanel? =
        tabs.tabs
            .firstOrNull { it.text == tableName }
            ?.content
            ?.let { it as? SkatTableNode }
            ?.skatTablePanel

    private fun createToolbarButtonWithActiveTableContext(action: AbstractJSkatAction): Button {
        return Button(action.getValue(AbstractJSkatAction.NAME).toString()).apply {
            graphic = bitmaps.getImageView(action.icon, IconSize.SMALL)
            tooltip = Tooltip(action.tooltip)
            disableProperty().bind(action.enabledProperty().not())
            onAction = EventHandler { action.actionPerformed(JSkatActionEvent(data.activeTable, it.source)) }
        }
    }

    private fun renderToolbarVisibility() {
        toolbar.isVisible = toolbarVisibility.isVisible
        toolbar.isManaged = toolbarVisibility.isVisible
    }

    private fun createToolbarButton(action: AbstractJSkatAction): Button {
        return Button(action.getValue(AbstractJSkatAction.NAME).toString()).apply {
            graphic = bitmaps.getImageView(action.icon, IconSize.SMALL)
            tooltip = Tooltip(action.tooltip)
            disableProperty().bind(action.enabledProperty().not())
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
            JavaFxTableLifecycle.defaultLocalTableName(
                strings.getString("localTable"),
                data.localTablesCreated
            )
        )
        dialog.title = strings.getString("newTableDialogTitle")
        dialog.headerText = strings.getString("newTableDialogMessage")
        dialog.contentText = strings.getString("name")
        dialog.initOwner(scene?.window)
        dialog.showAndWait().ifPresent { tableName ->
            val submittedTableName = JavaFxTableLifecycle.submittedTableName(tableName)
            if (!JavaFxTableLifecycle.isValidTableName(submittedTableName)) {
                JSkatMaster.showEmptyInputNameMessage()
            } else {
                jskatMaster.createTable(submittedTableName)
            }
        }
    }

    private fun closeTable(table: JavaFxTableLifecycle.Table) {
        JavaFxTableLifecycle.closeTable(
            table,
            removeLocalTable = { tableName ->
                JSkatEventBus.INSTANCE.post(RemoveTableCommand(JSkatViewType.LOCAL_TABLE, tableName))
            },
            leaveIssTable = { tableName ->
                actions[JSkatAction.LEAVE_ISS_TABLE]?.actionPerformed(
                    JSkatActionEvent(JSkatAction.LEAVE_ISS_TABLE, tableName)
                )
            }
        )
    }
}
