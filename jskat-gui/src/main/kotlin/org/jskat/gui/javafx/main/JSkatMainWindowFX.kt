package org.jskat.gui.javafx.main

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
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
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.Icon
import org.jskat.gui.img.JSkatGraphicRepository.IconSize
import org.jskat.gui.javafx.JSkatMenuFactory
import org.jskat.gui.javafx.dialog.options.JSkatOptionsDialog
import org.jskat.gui.javafx.iss.LobbyPanel
import org.jskat.gui.javafx.table.ISSTablePanel
import org.jskat.gui.javafx.table.SkatSeriesStartDialog
import org.jskat.gui.javafx.table.SkatTableNode
import org.jskat.gui.javafx.table.SkatTablePanel
import org.jskat.util.JSkatResourceBundle

class JSkatMainWindowFX : VBox() {

    private val jskatMaster: JSkatMaster = JSkatMaster.INSTANCE
    private val data: JSkatApplicationData = JSkatApplicationData.INSTANCE
    private val resourceBundle: JSkatResourceBundle = JSkatResourceBundle.INSTANCE
    private val graphicRepository: JSkatGraphicRepository = JSkatGraphicRepository.INSTANCE
    private val actions = JSkatActions.actionMap

    private val content: TabPane = TabPane()
    private lateinit var issLobby: LobbyPanel

    init {
        JSkatEventBus.INSTANCE.register(this)

        val menuBar = JSkatMenuFactory.build()

        val toolbar = ToolBar()
        toolbar.items.addAll(
            createToolbarButton("new_table", "new_table_tooltip", Icon.NEW) { createNewLocalTable() },
            createToolbarButton("local_table", "play_on_local_table", Icon.TABLE) { createNewLocalTable() },
            createToolbarButton("iss_table", "play_on_iss_tooltip", Icon.CONNECT_ISS) { showIssLogin() },
            createToolbarButton("preferences", "preferences_tooltip", Icon.PREFERENCES) { showPreferences() },
            createToolbarButton("help", "help_tooltip", Icon.HELP) { showAbout() },
            createToolbarButton("about", "about_tooltip", Icon.ABOUT) { showAbout() }
        )
        setVgrow(content, Priority.ALWAYS)

        children.addAll(menuBar, toolbar, content)

        content.selectionModel.selectedItemProperty().addListener { _, _, newTab ->
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
        val panel = when (event.tableType()) {
            JSkatViewType.LOCAL_TABLE -> {
                val skatTablePanel = SkatTablePanel(tableName, actions)
                SkatTableNode(skatTablePanel)
            }

            JSkatViewType.ISS_TABLE -> {
                val issTablePanel = ISSTablePanel(tableName, actions)
                SkatTableNode(issTablePanel)
            }

            else -> null
        }

        if (panel != null) {
            Platform.runLater {
                val newTab = Tab(tableName).apply {
                    content = panel
                    isClosable = true
                }
                content.tabs.add(newTab)
                content.selectionModel.select(newTab)

                if (event.tableType() == JSkatViewType.LOCAL_TABLE) {
                    actions[JSkatAction.START_LOCAL_SERIES]?.setEnabled(true)
                }
            }
        }
    }

    @Subscribe
    fun onTableRemoved(event: TableRemovedEvent) {
        Platform.runLater {
            content.tabs
                .filter { it.text == event.tableName() }
                .forEach { content.tabs.remove(it) }
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
            val loader = FXMLLoader()
            loader.location = JSkatMainWindowFX::class.java.getResource("/org/jskat/gui/javafx/iss/IssLogin.fxml")
            loader.resources = resourceBundle.stringResources
            val loginPanel: VBox = loader.load()

            val tab = Tab(resourceBundle.getString("iss_login")).apply {
                id = "ISS_LOGIN"
                isClosable = true
                content = loginPanel
            }

            content.tabs.add(tab)
            content.selectionModel.select(tab)
        }
    }

    @Subscribe
    fun showIssLobbyOn(event: IssConnectedEvent) {
        Platform.runLater {
            content.tabs
                .find { it.id == "ISS_LOGIN" }
                ?.let { loginTab ->
                    content.tabs.remove(loginTab)
                }

            issLobby = LobbyPanel(actions, event.userName)
            val scrollPane = ScrollPane(issLobby).apply {
                isFitToWidth = true
                isFitToHeight = true
            }
            issLobby.prefWidthProperty().bind(scrollPane.widthProperty())
            issLobby.prefHeightProperty().bind(scrollPane.heightProperty())

            val tab = Tab("ISS Lobby").apply {
                id = "ISS_LOBBY"
                isClosable = true
                content = scrollPane
            }

            content.tabs.add(tab)
            content.selectionModel.select(tab)
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

    private fun createToolbarButton(
        textKey: String,
        tooltipKey: String,
        icon: Icon,
        eventHandler: EventHandler<ActionEvent>
    ): Button {
        val button = Button(resourceBundle.getString(textKey))
        button.graphic = graphicRepository.getImageView(icon, IconSize.SMALL)
        button.tooltip = Tooltip(resourceBundle.getString(tooltipKey))
        button.onAction = eventHandler
        return button
    }

    private fun addWelcomeTab() {
        val welcomeTab = Tab(resourceBundle.getString("welcome"))
        welcomeTab.isClosable = false
        welcomeTab.content = WelcomePanel(actions)
        content.tabs.add(welcomeTab)
    }

    private fun createNewLocalTable() {
        val dialog = TextInputDialog(
            "${resourceBundle.getString("local.table")} ${jskatMaster.view.getNewTableName(0)}"
        )
        dialog.title = resourceBundle.getString("new.table.dialog.title")
        dialog.headerText = resourceBundle.getString("new.table.dialog.message")
        dialog.contentText = resourceBundle.getString("name")
        dialog.dialogPane.stylesheets.add("/org/jskat/gui/javafx/jskat.css")

        dialog.showAndWait().ifPresent { tableName ->
            if (tableName.isNotEmpty()) {
                jskatMaster.createTable(tableName)
            }
        }
    }

    private fun showIssLogin() {
        JSkatEventBus.INSTANCE.post(IssShowLoginCommand())
    }

    private fun showPreferences() {
        JSkatOptionsDialog(scene.window).showAndWait()
    }

    private fun showAbout() {
        Alert(Alert.AlertType.INFORMATION).apply {
            initOwner(scene.window)
            title = resourceBundle.getString("about")
            headerText = "JSkat ${resourceBundle.getString("version")} 0.24.0"
            graphic = ImageView(graphicRepository.jSkatLogoImageFX)
            contentText = """
                https://www.jskat.org
                https://github.com/b0n541/jskat
                
                ${resourceBundle.getString("authors")}: Jan Schäfer (support@jskat.org), Markus J. Luzius, Daniel Loreck, Andrius Vaskys
                
                ${resourceBundle.getString("cards")}: International Skat Server, KDE project, OpenClipart.org
                
                ${resourceBundle.getString("icons")}: Gnome Desktop Icons, Tango project, Elementary icons, Silvestre Herrera, Alex Roberts and Icojoy
                
                ${resourceBundle.getString("background_image")}: webtreats
                
                This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you are welcome to redistribute it under certain conditions. See license dialog for details.
            """.trimIndent()

            dialogPane.minWidth = 600.0
            dialogPane.stylesheets.add("/org/jskat/gui/javafx/jskat.css")

            showAndWait()
        }
    }

    private fun exitJSkat() {
        jskatMaster.exitJSkat()
    }

    fun setActiveView(tableName: String) {
        // FIXME: implement
    }
}
