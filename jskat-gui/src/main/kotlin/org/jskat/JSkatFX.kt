package org.jskat

import com.google.common.eventbus.Subscribe
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Screen
import javafx.stage.Stage
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.control.command.general.ExitCommand
import org.jskat.data.JSkatOptions
import org.jskat.gui.human.SwingHumanPlayer
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
        val jskatView = JSkatViewFX(JSkatMainWindowFX(), human)
        jskatMaster.view = jskatView

        stage.title = "JSkat"
        stage.setOnCloseRequest {
            eventBus.post(ExitCommand())
        }

        val screen = Screen.getPrimary()
        val bounds = screen.visualBounds

        val position = jskatOptions.mainFramePosition
        val size = jskatOptions.mainFrameSize

        if (position.x != Int.MIN_VALUE && position.y != Int.MIN_VALUE) {
            stage.x = position.x.toDouble()
            stage.y = position.y.toDouble()
        } else {
            stage.x = bounds.minX
            stage.y = bounds.minY
        }

        if (size.width != Int.MIN_VALUE && size.height != Int.MIN_VALUE) {
            stage.width = size.width.toDouble()
            stage.height = size.height.toDouble()
        } else {
            stage.width = bounds.width
            stage.height = bounds.height
        }

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
