package org.jskat.gui.javafx.main

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.control.command.iss.IssShowLoginCommand
import org.jskat.control.command.table.StartSkatSeriesCommand
import org.jskat.control.event.iss.*
import org.jskat.control.event.table.TableCreatedEvent
import org.jskat.control.event.table.TableRemovedEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.JSkatViewType
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.Icon
import org.jskat.gui.img.JSkatGraphicRepository.IconSize
import org.jskat.gui.javafx.dialog.options.JSkatOptionsDialog
import org.jskat.gui.javafx.iss.LobbyPanel
import org.jskat.gui.javafx.table.ISSTablePanel
import org.jskat.gui.javafx.table.SkatSeriesStartDialog
import org.jskat.gui.javafx.table.SkatTableNode
import org.jskat.gui.javafx.table.SkatTablePanel
import org.jskat.util.JSkatResourceBundle

class JSkatMainWindowFX : VBox() {

    private val jskatMaster: JSkatMaster = JSkatMaster.INSTANCE
    private val resourceBundle: JSkatResourceBundle = JSkatResourceBundle.INSTANCE
    private val graphicRepository: JSkatGraphicRepository = JSkatGraphicRepository.INSTANCE
    private val actions = JSkatActions.createActionMap()

    private val content: TabPane = TabPane()
    private lateinit var issLobby: LobbyPanel

    init {
        JSkatEventBus.INSTANCE.register(this)

        val toolbar = ToolBar()
        toolbar.items.addAll(
            createToolbarButton("new_table", "new_table_tooltip", Icon.NEW) { createNewLocalTable() },
            createToolbarButton("local_table", "play_on_local_table", Icon.TABLE) { createNewLocalTable() },
            createToolbarButton("iss_table", "play_on_iss_tooltip", Icon.CONNECT_ISS) { showIssLogin() },
            createToolbarButton("preferences", "preferences_tooltip", Icon.PREFERENCES) { showPreferences() },
            createToolbarButton("help", "help_tooltip", Icon.HELP) { showAbout() },
            createToolbarButton("about", "about_tooltip", Icon.ABOUT) { showAbout() }
        )
        VBox.setVgrow(content, Priority.ALWAYS)

        children.addAll(toolbar, content)

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
                    actions[JSkatAction.START_LOCAL_SERIES].isEnabled = true
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

        val welcomeActions = HBox(
            createWelcomeAction("local_table", "explain.local.table", Icon.TABLE) { createNewLocalTable() },
            createWelcomeAction("iss_table", "explain.iss.table", Icon.CONNECT_ISS) { showIssLogin() },
            createWelcomeAction("preferences", "explain.preferences", Icon.PREFERENCES) { showPreferences() },
            createWelcomeAction("exit.jskat", "explain.exit", Icon.EXIT) { exitJSkat() }
        ).apply {
            alignment = Pos.CENTER
            spacing = 20.0
        }

        val welcomeHeader = Label(resourceBundle.getString("welcome.to.jskat")).apply {
            style = "-fx-font-size: 24px;"
        }

        val welcomeContent = VBox(welcomeHeader, welcomeActions).apply {
            alignment = Pos.CENTER
            spacing = 40.0
            style = "-fx-padding: 20px;"
        }

        welcomeTab.content = welcomeContent
        content.tabs.add(welcomeTab)
    }

    private fun createWelcomeAction(
        textKey: String,
        descriptionKey: String,
        icon: Icon,
        eventHandler: EventHandler<ActionEvent>
    ): Node {
        val button = Button(resourceBundle.getString(textKey)).apply {
            graphic = graphicRepository.getImageView(icon, IconSize.BIG)
            onAction = eventHandler
            contentDisplay = ContentDisplay.TOP
        }
        val description = Label(resourceBundle.getString(descriptionKey)).apply {
            isWrapText = true
            textAlignment = TextAlignment.CENTER
        }
        return VBox(button, description).apply {
            alignment = Pos.CENTER
            spacing = 10.0
            maxWidth = 200.0
        }
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
