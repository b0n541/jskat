package org.jskat

import com.google.common.eventbus.Subscribe
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.stage.Screen
import javafx.stage.Stage
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.control.command.general.ExitCommand
import org.jskat.data.JSkatOptions
import org.jskat.data.WindowGeometry
import org.jskat.gui.human.SwingHumanPlayer
import org.jskat.gui.javafx.JavaFxHostDocumentOpener
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

    override fun start(stage: Stage) {
        this.stage = stage

        eventBus.register(this)

        // TODO: Replace with better approach
        val human = SwingHumanPlayer()
        val jskatView = JSkatViewFX(JSkatMainWindowFX(), human, JavaFxHostDocumentOpener(hostServices::showDocument))
        jskatMaster.view = jskatView

        stage.title = "JSkat"
        stage.setOnCloseRequest {
            eventBus.post(ExitCommand())
        }

        val screen = Screen.getPrimary()
        val bounds = screen.visualBounds

        val restoredGeometry = restoreWindowGeometry(jskatOptions.mainFrameGeometry, bounds)
        stage.x = restoredGeometry.minX
        stage.y = restoredGeometry.minY
        stage.width = restoredGeometry.width
        stage.height = restoredGeometry.height

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

        val scene = Scene(jskatView.mainWindow, stage.width, stage.height)
        scene.stylesheets.add(JSkatFX::class.java.getResource("/org/jskat/gui/javafx/jskat.css").toExternalForm())
        stage.scene = scene

        stage.show()
    }

    @Subscribe
    fun exitJSkatOn(command: ExitCommand) {
        jskatOptions.saveJSkatProperties()
        Platform.exit()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            JSkatOptions.instance(DesktopSavePathResolver())
            launch(JSkatFX::class.java)
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
