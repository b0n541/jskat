package org.jskat

import com.google.common.eventbus.Subscribe
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.ai.ml.ModelManager
import org.jskat.gui.javafx.model.ModelStartupLifecycle
import org.jskat.control.command.general.ExitCommand
import org.jskat.data.JSkatOptions
import org.jskat.data.WindowGeometry
import org.jskat.gui.human.SwingHumanPlayer
import org.jskat.gui.javafx.JavaFxHostDocumentOpener
import org.jskat.gui.javafx.JSkatTheme
import org.jskat.gui.javafx.JSkatViewFX
import org.jskat.gui.javafx.main.JSkatMainWindowFX
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JSkatFX : Application() {

    private val log: Logger = LoggerFactory.getLogger(JSkatFX::class.java)

    private val eventBus = JSkatEventBus.INSTANCE
    private val jskatMaster: JSkatMaster = JSkatMaster.INSTANCE
    private val jskatOptions: JSkatOptions = JSkatOptions.instance()

    private var stage: Stage? = null
    private var tournamentController: org.jskat.gui.javafx.tournament.TournamentController? = null
    private var exiting = false

    override fun start(stage: Stage) {
        this.stage = stage
        JSkatTheme.install()

        eventBus.register(this)

        // TODO: Replace with better approach
        val human = SwingHumanPlayer()
        val developerMode = parameters.raw.contains("--developer-mode")
        tournamentController = if (developerMode) org.jskat.gui.javafx.tournament.TournamentController() else null
        val mainWindow = JSkatMainWindowFX(developerMode = developerMode, tournamentController = tournamentController,
            openTournamentDirectory = { hostServices.showDocument(it.toUri().toString()) })
        val jskatView = JSkatViewFX(
            mainWindow,
            human,
            JavaFxHostDocumentOpener(hostServices::showDocument),
            applicationVersion
        )
        jskatMaster.view = jskatView

        stage.title = "JSkat"
        JSkatFX::class.java.getResourceAsStream("/org/jskat/gui/img/gui/jskat_logo.png")?.use {
            stage.icons.add(Image(it))
        }
        stage.setOnCloseRequest { event ->
            event.consume()
            eventBus.post(ExitCommand())
        }

        val screen = Screen.getPrimary()
        val bounds = screen.visualBounds

        val restoredGeometry = restoreWindowGeometry(jskatOptions.mainFrameGeometry, bounds)
        stage.x = restoredGeometry.minX
        stage.y = restoredGeometry.minY
        stage.width = restoredGeometry.width
        stage.height = restoredGeometry.height
        stage.isMaximized = jskatOptions.isMainFrameMaximized

        stage.xProperty().addListener { _, _, newValue ->
            if (!stage.isMaximized) {
                jskatOptions.setMainFrameXPosition(newValue.toInt())
            }
        }
        stage.yProperty().addListener { _, _, newValue ->
            if (!stage.isMaximized) {
                jskatOptions.setMainFrameYPosition(newValue.toInt())
            }
        }
        stage.widthProperty().addListener { _, _, newValue ->
            if (!stage.isMaximized) {
                jskatOptions.setMainFrameWidth(newValue.toInt())
            }
        }
        stage.heightProperty().addListener { _, _, newValue ->
            if (!stage.isMaximized) {
                jskatOptions.setMainFrameHeight(newValue.toInt())
            }
        }

        stage.scene = Scene(jskatView.mainWindow, stage.width, stage.height)

        stage.show()

        Platform.runLater {
            ModelStartupLifecycle(ModelManager(), stage, mainWindow::updateModelInspection).start()
        }
    }

    @Subscribe
    fun exitJSkatOn(command: ExitCommand) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater { exitJSkatOn(command) }
            return
        }
        if (exiting) return
        val exit = {
            exiting = true
            stage?.let { jskatOptions.setMainFrameMaximized(it.isMaximized) }
            jskatOptions.saveJSkatProperties()
            Platform.exit()
        }
        tournamentController?.requestExit({
            val cancel = javafx.scene.control.ButtonType("Cancel tournament and wait", javafx.scene.control.ButtonBar.ButtonData.OK_DONE)
            val stay = javafx.scene.control.ButtonType("Stay", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE)
            val dialog = javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING,
                "JSkat will stay open until admitted games, reports and resource cleanup finish. This can take time if a player is stalled.", cancel, stay)
            dialog.initOwner(stage)
            dialog.headerText = "A tournament is active"
            dialog.showAndWait().orElse(stay) == cancel
        }, exit) ?: exit()
    }

    companion object {
        private lateinit var applicationVersion: String

        @JvmStatic
        fun main(args: Array<String>, applicationVersion: String) {
            this.applicationVersion = applicationVersion
            JSkatOptions.instance(DesktopSavePathResolver())
            launch(JSkatFX::class.java, *args)
        }
    }
}

internal fun restoreWindowGeometry(saved: WindowGeometry, screenBounds: Rectangle2D): Rectangle2D {
    val x = if (saved.hasPosition()) saved.x().toDouble() else screenBounds.minX
    val y = if (saved.hasPosition()) saved.y().toDouble() else screenBounds.minY
    val width = if (saved.hasSize()) saved.width().toDouble() else screenBounds.width
    val height = if (saved.hasSize()) saved.height().toDouble() else screenBounds.height
    return Rectangle2D(x, y, width, height)
}
