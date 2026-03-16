package org.jskat

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Screen
import javafx.stage.Stage
import org.jskat.control.JSkatMaster
import org.jskat.data.JSkatOptions
import org.jskat.gui.human.SwingHumanPlayer
import org.jskat.gui.javafx.JSkatViewFX
import org.jskat.gui.javafx.main.JSkatMainWindowFX
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JSkatFX : Application() {

    private val log: Logger = LoggerFactory.getLogger(JSkatFX::class.java)

    private val jskatMaster: JSkatMaster = JSkatMaster.INSTANCE
    private val jskatOptions: JSkatOptions = JSkatOptions.instance()

    private var stage: Stage? = null

    override fun start(stage: Stage) {
        this.stage = stage

        val human = SwingHumanPlayer()
        val jskatView = JSkatViewFX(JSkatMainWindowFX(), human)
        jskatMaster.view = jskatView

        stage.title = "JSkat"
        stage.setOnCloseRequest {
            jskatMaster.exitJSkat()
        }

        val screen = Screen.getPrimary()
        val bounds = screen.visualBounds

        stage.x = bounds.minX
        stage.y = bounds.minY
        stage.width = bounds.width
        stage.height = bounds.height

        val scene = Scene(jskatView.mainWindow, bounds.width, bounds.height)
        scene.stylesheets.add(JSkatFX::class.java.getResource("/org/jskat/gui/javafx/jskat.css").toExternalForm())
        stage.scene = scene

        stage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            JSkatOptions.instance(DesktopSavePathResolver())
            launch(JSkatFX::class.java)
        }
    }
}
