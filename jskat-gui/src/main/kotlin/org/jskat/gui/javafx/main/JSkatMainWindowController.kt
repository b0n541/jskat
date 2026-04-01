package org.jskat.gui.javafx.main

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.control.command.general.ShowAboutInformationCommand
import org.jskat.control.command.general.ShowHelpCommand
import org.jskat.control.command.general.ShowLicenseCommand
import org.jskat.control.command.general.ShowWelcomeInformationCommand
import org.jskat.control.command.iss.IssShowLoginCommand
import org.jskat.control.command.table.NextReplayMoveCommand
import org.jskat.control.command.table.ReplayGameCommand
import org.jskat.control.command.table.StartSkatSeriesCommand
import org.jskat.control.event.general.NewJSkatVersionAvailableEvent
import org.jskat.control.event.iss.*
import org.jskat.control.event.table.EmptyTableNameInputEvent
import org.jskat.control.event.table.TableCreatedEvent
import org.jskat.control.event.table.TableRemovedEvent
import org.jskat.data.JSkatApplicationData
import org.jskat.data.JSkatViewType
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.javafx.dialog.firststeps.FirstStepsDialog
import org.jskat.gui.javafx.dialog.options.JSkatOptionsDialog
import org.jskat.gui.javafx.iss.LobbyPanel
import org.jskat.gui.javafx.table.ISSTablePanel
import org.jskat.gui.javafx.table.SkatTableNode
import org.jskat.gui.javafx.table.SkatTablePanel
import org.jskat.util.JSkatResourceBundle
import javax.swing.SwingUtilities

class JSkatMainWindowController {

    private val strings = JSkatResourceBundle.INSTANCE
    private val applicationData = JSkatApplicationData.INSTANCE
    private val VERSION = "0.24.0"

    @FXML
    private lateinit var root: Parent

    @FXML
    private lateinit var tabs: TabPane

    @FXML
    private lateinit var preferencesMenuItem: MenuItem

    @FXML
    private lateinit var preferencesButton: Button

    @FXML
    private lateinit var issLoginMenuItem: MenuItem

    @FXML
    private lateinit var issLoginToolbarButton: Button

    @FXML
    private lateinit var issLoginButton: Button

    @FXML
    private lateinit var localTableMenuItem: MenuItem

    @FXML
    private lateinit var localTableToolbarButton: Button

    @FXML
    private lateinit var localTableButton: Button

    @FXML
    private lateinit var startSkatSeriesMenuItem: MenuItem

    @FXML
    private lateinit var startSkatSeriesToolbarButton: Button

    @FXML
    private lateinit var replayGameMenuItem: MenuItem

    @FXML
    private lateinit var replayGameToolbarButton: Button

    @FXML
    private lateinit var nextReplayMoveMenuItem: MenuItem

    @FXML
    private lateinit var nextReplayMoveButton: Button

    @FXML
    private lateinit var helpButton: Button

    @FXML
    private lateinit var helpMenuItem: MenuItem

    @FXML
    private lateinit var licenseMenuItem: MenuItem

    @FXML
    private lateinit var aboutMenuItem: MenuItem

    @FXML
    private lateinit var exitJSkatMenuItem: MenuItem

    @FXML
    private lateinit var exitJSkatButton: Button

    // TODO remove when Swing has been retired
    private lateinit var issLobby: LobbyPanel

    @FXML
    fun initialize() {
        JSkatEventBus.INSTANCE.register(this)
    }

    @FXML
    fun showIssLoginPanel() {
        JSkatEventBus.INSTANCE.post(IssShowLoginCommand())
    }

    @FXML
    fun showPreferences() {
        JSkatOptionsDialog(root.scene.window).showAndWait()
    }

    @FXML
    fun exitJSkat() {
        // FIXME get rid of that god class
        JSkatMaster.INSTANCE.exitJSkat()
    }

    @FXML
    fun createNewLocalTable() {
        val dialog = TextInputDialog(
            "${strings.getString("local.table")} ${applicationData.localTablesCreated + 1}"
        )

        // TODO: set this globally
        dialog.dialogPane.stylesheets.add("/org/jskat/gui/javafx/jskat.css")

        dialog.title = strings.getString("new.table.dialog.title")
        dialog.headerText = strings.getString("new.table.dialog.message")
        dialog.contentText = strings.getString("name")

        val result = dialog.showAndWait()

        result.ifPresent { tableName ->
            when {
                tableName.isEmpty() -> JSkatEventBus.INSTANCE.post(EmptyTableNameInputEvent())
                else -> {
                    val finalTableName = if (tableName.length > 100) tableName.substring(0, 100) else tableName
                    JSkatMaster.INSTANCE.createTable(finalTableName)
                }
            }
        }
    }

    @FXML
    fun startSkatSeries() {
        JSkatEventBus.INSTANCE.post(StartSkatSeriesCommand(JSkatApplicationData.INSTANCE.activeTable))
    }

    @FXML
    fun replayGame() {
        JSkatEventBus.INSTANCE.post(ReplayGameCommand(JSkatApplicationData.INSTANCE.activeTable))
    }

    @FXML
    fun nextReplayMove() {
        JSkatEventBus.INSTANCE.post(NextReplayMoveCommand(JSkatApplicationData.INSTANCE.activeTable))
    }

    @FXML
    fun showHelp() {
        SwingUtilities.invokeLater {
            JSkatEventBus.INSTANCE.post(ShowHelpCommand())
        }
    }

    @FXML
    fun showLicense() {
        SwingUtilities.invokeLater {
            JSkatEventBus.INSTANCE.post(ShowLicenseCommand())
        }
    }

    //    @FXML
    fun showAboutInformation() {
        showAboutDialog()
    }

    @Subscribe
    fun showAboutInformationOn(command: ShowAboutInformationCommand) {
        Platform.runLater { showAboutDialog() }
    }

    private fun showAboutDialog() {
        Alert(Alert.AlertType.INFORMATION).apply {
            initOwner(root.scene.window)
            title = strings.getString("about")
            headerText = "JSkat ${strings.getString("version")} $VERSION"
            graphic = ImageView(JSkatGraphicRepository.INSTANCE.jSkatLogoImageFX)
            contentText = """
                https://www.jskat.org
                https://github.com/b0n541/jskat
                
                ${strings.getString("authors")}: Jan Schäfer (support@jskat.org), Markus J. Luzius, Daniel Loreck, Andrius Vaskys
                
                ${strings.getString("cards")}: International Skat Server, KDE project, OpenClipart.org
                
                ${strings.getString("icons")}: Gnome Desktop Icons, Tango project, Elementary icons, Silvestre Herrera, Alex Roberts and Icojoy
                
                ${strings.getString("background_image")}: webtreats
                
                This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you are welcome to redistribute it under certain conditions. See license dialog for details.
            """.trimIndent()

            dialogPane.minWidth = 600.0

            showAndWait()
        }
    }

    @Subscribe
    fun showWelcomeDialogOn(command: ShowWelcomeInformationCommand) {
        FirstStepsDialog(root.scene.window as Stage).show()
    }

    @Subscribe
    fun showNewVersionInfoOn(event: NewJSkatVersionAvailableEvent) {
        Alert(Alert.AlertType.INFORMATION).apply {
            initOwner(root.scene.window)
            title = JSkatResourceBundle.INSTANCE.getString("new_version_title")
            headerText = JSkatResourceBundle.INSTANCE.getString("new_version_header", event.newVersion)
            contentText = JSkatResourceBundle.INSTANCE.getString("new_version_message", event.newVersion)

            // this is a workaround for a bug under Linux that cuts long texts
            dialogPane.children
                .filterIsInstance<Label>()
                .forEach { it.minHeight = Region.USE_PREF_SIZE }

            showAndWait()
        }
    }

    @Subscribe
    fun showIssLoginOn(command: IssShowLoginCommand) {
        val loader = FXMLLoader().apply {
            location = javaClass.getResource("/org/jskat/gui/javafx/iss/IssLogin.fxml")
            resources = JSkatResourceBundle.INSTANCE.stringResources
        }
        val loginPanel: VBox = loader.load()

        val tab = Tab(strings.getString("iss_login")).apply {
            id = JSkatMainWindowTabType.ISS_LOGIN.name
            isClosable = true
            content = loginPanel
        }

        tabs.tabs.add(tab)
        tabs.selectionModel.select(tab)
    }

    @Subscribe
    fun addNewTableTabOn(event: TableCreatedEvent) {
        val tableName = event.tableName()

        val panel = when (event.tableType()) {
            JSkatViewType.LOCAL_TABLE -> {
                var skatTablePanel: SkatTablePanel? = null
                SwingUtilities.invokeAndWait {
                    skatTablePanel = SkatTablePanel(tableName, JSkatActions.actionMap)
                }
                SkatTableNode(skatTablePanel!!)
            }

            JSkatViewType.ISS_TABLE -> {
                var issTablePanel: ISSTablePanel? = null
                SwingUtilities.invokeAndWait {
                    issTablePanel = ISSTablePanel(tableName, JSkatActions.actionMap)
                }
                SkatTableNode(issTablePanel!!)
            }

            else -> null
        }

        val (tabTitle, tabId) = when (event.tableType()) {
            JSkatViewType.LOCAL_TABLE -> Pair(
                tableName,
                "${JSkatMainWindowTabType.LOCAL_TABLE.name}:$tableName"
            )

            JSkatViewType.ISS_TABLE -> Pair(
                "${strings.getString("iss_table")}: $tableName",
                "${JSkatMainWindowTabType.ISS_TABLE.name}:$tableName"
            )

            else -> Pair("", "")
        }

        val tab = Tab(tabTitle).apply {
            id = tabId
            isClosable = true
            content = panel
        }

        Platform.runLater {
            tabs.tabs.add(tab)
            tabs.selectionModel.select(tab)
        }
    }

    @Subscribe
    fun showIssLobbyOn(event: IssConnectedEvent) {
        tabs.tabs
            .find { it.id == JSkatMainWindowTabType.ISS_LOGIN.name }
            ?.let { loginTab ->
                Platform.runLater { tabs.tabs.remove(loginTab) }
            }

        issLobby = LobbyPanel(JSkatActions.actionMap, event.userName)
        val scrollPane = ScrollPane(issLobby).apply {
            isFitToWidth = true
            isFitToHeight = true
        }
        issLobby.prefWidthProperty().bind(scrollPane.widthProperty())
        issLobby.prefHeightProperty().bind(scrollPane.heightProperty())

        val tab = Tab("ISS Lobby").apply {
            id = JSkatMainWindowTabType.ISS_LOBBY.name
            isClosable = true
            content = scrollPane
        }

        Platform.runLater {
            tabs.tabs.add(tab)
            tabs.selectionModel.select(tab)
        }
    }

    @Subscribe
    fun removeTableOn(event: TableRemovedEvent) {
        tabs.tabs
            .filter { it.id.contains(event.tableName()) }
            .forEach { tab -> Platform.runLater { tabs.tabs.remove(tab) } }
    }

    @Subscribe
    fun closeAllIssTabsOn(event: IssDisconnectedEvent) {
        val issTabTypes = setOf(
            JSkatMainWindowTabType.ISS_LOGIN.name,
            JSkatMainWindowTabType.ISS_LOBBY.name,
            JSkatMainWindowTabType.ISS_TABLE.name
        )

        tabs.tabs
            .filter { it.id in issTabTypes }
            .forEach { tab -> Platform.runLater { tabs.tabs.remove(tab) } }
    }

    @Subscribe
    fun updateIssTableListOn(event: IssTableDataUpdatedEvent) {
        issLobby.updateTable(
            event.tableName(),
            event.maxPlayers(),
            event.gamesPlayed(),
            event.player1(),
            event.player2(),
            event.player3()
        )
    }

    @Subscribe
    fun deleteTableFromIssTableListOn(event: IssTableDeletedEvent) {
        issLobby.removeTable(event.tableName())
    }

    @Subscribe
    fun updateIssPlayerListOn(event: IssPlayerDataUpdatedEvent) {
        issLobby.updatePlayer(
            event.playerName(),
            event.language(),
            event.gamesPlayed(),
            event.strength()
        )
    }

    @Subscribe
    fun deletePlayerFromIssPlayerListOn(event: IssPlayerLeftEvent) {
        issLobby.removePlayer(event.playerName())
    }
}
