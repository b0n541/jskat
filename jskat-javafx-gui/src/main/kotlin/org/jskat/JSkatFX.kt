package org.jskat

import javafx.animation.FadeTransition
import javafx.application.Application
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.fxml.FXMLLoader
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.MenuBar
import javafx.scene.control.ProgressBar
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.control.command.general.ShowWelcomeInformationCommand
import org.jskat.data.DesktopSavePathResolver
import org.jskat.data.JSkatOptions
import org.jskat.data.JSkatOptions.Option
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.javafx.JSkatMenuFactory
import org.jskat.gui.swing.JSkatViewImpl
import org.jskat.gui.swing.LookAndFeelSetter
import org.jskat.util.JSkatResourceBundle
import org.jskat.util.version.VersionChecker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Point
import java.io.IOException
import javax.swing.SwingUtilities

class JSkatFX : Application() {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(JSkatFX::class.java)
        private const val VERSION = "0.24.0"
        private const val SPLASH_WIDTH = 500
        private const val SPLASH_HEIGHT = 300

        @JvmStatic
        fun main(args: Array<String>) {
            JSkatOptions.instance(DesktopSavePathResolver())
            launch(JSkatFX::class.java, *args)
        }

        private fun showJSkatMainWindow(
            targetScreen: Screen,
            screenPosition: Point2D,
            menu: MenuBar,
            jskatView: JSkatViewImpl
        ) {
            try {
                showNewMainWindow(targetScreen, screenPosition, VERSION)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            if (JSkatOptions.instance().getBoolean(Option.SHOW_TIPS_AT_START_UP)) {
                JSkatEventBus.INSTANCE.post(ShowWelcomeInformationCommand())
            }

            if (JSkatOptions.instance().getBoolean(Option.CHECK_FOR_NEW_VERSION_AT_START_UP)) {
                JSkatMaster.checkJSkatVersion(VERSION, VersionChecker.getLatestVersion())
            }
        }

        private fun showNewMainWindow(targetScreen: Screen, screenPosition: Point2D, version: String) {
            val loader = FXMLLoader().apply {
                location = JSkatFX::class.java.getResource("/org/jskat/gui/javafx/main/JSkatMainWindow.fxml")
                resources = JSkatResourceBundle.INSTANCE.stringResources
            }
            val rootLayout: VBox = loader.load()

            val dimension = getMainWindowDimension(targetScreen)
            LOG.info("Main window size {} x {}", dimension.width, dimension.height)
            val scene = Scene(rootLayout, dimension.width, dimension.height).apply {
                // TODO: set this globally
                stylesheets.add("/org/jskat/gui/javafx/jskat.css")

                widthProperty().addListener { _, _, newValue ->
                    JSkatOptions.instance().setMainFrameWidth(newValue.toInt())
                }
                heightProperty().addListener { _, _, newValue ->
                    JSkatOptions.instance().setMainFrameHeight(newValue.toInt())
                }
            }

            val stage = Stage().apply {
                title = "JSkat $version"
                setOnCloseRequest { JSkatMaster.INSTANCE.exitJSkat() }
                this.scene = scene
                width = dimension.width
                height = dimension.height

                xProperty().addListener { _, _, newValue ->
                    JSkatOptions.instance().setMainFrameXPosition(newValue.toInt())
                }
                yProperty().addListener { _, _, newValue ->
                    JSkatOptions.instance().setMainFrameYPosition(newValue.toInt())
                }
            }

            placeMainWindow(targetScreen, stage, screenPosition)
            stage.show()
        }

        private fun getTargetScreen(savedScreenPosition: Point2D): Screen {
            return Screen.getScreens().find { screen ->
                screen.visualBounds.contains(savedScreenPosition)
            } ?: Screen.getPrimary()
        }

        private fun getSavedScreenPosition(): Point2D {
            val mainFramePosition: Point = JSkatOptions.instance().mainFramePosition
            return Point2D(mainFramePosition.x.toDouble(), mainFramePosition.y.toDouble())
        }

        private fun getMainWindowDimension(targetScreen: Screen): Dimension2D {
            val width = JSkatOptions.instance().mainFrameSize.width.toDouble()
            val height = JSkatOptions.instance().mainFrameSize.height.toDouble()

            // on first startup the default values for width and height are 2/3 of screen size
            return Dimension2D(
                if (width > 0) width else targetScreen.bounds.width * 2 / 3,
                if (height > 0) height else targetScreen.bounds.height * 2 / 3
            )
        }

        private fun placeMainWindow(screen: Screen, mainWindow: Stage, position: Point2D) {
            if (screen.visualBounds.contains(position)) {
                mainWindow.x = position.x
                mainWindow.y = position.y
            } else {
                mainWindow.centerOnScreen()
            }
        }
    }

    override fun init() {
        // Empty implementation
    }

    override fun start(primaryStage: Stage) {
        // TODO: set JSkat style here globally
        // Application.setUserAgentStylesheet("/org/jskat/gui/javafx/jskat.css")

        val savedScreenPosition = getSavedScreenPosition()
        val targetScreen = getTargetScreen(savedScreenPosition)

        val startupTasks = object : Task<InitializedGuiElements>() {
            override fun call(): InitializedGuiElements {
                updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_init_application"))

                SwingUtilities.invokeLater { LookAndFeelSetter.setLookAndFeel(targetScreen) }

                updateProgress(1.0, 3.0)
                updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_load_card_sets"))

                JSkatGraphicRepository.INSTANCE.toString()

                updateProgress(2.0, 3.0)
                updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_look_for_ai_players"))

                val menu = JSkatMenuFactory.build()
                val jskatView = JSkatViewImpl(targetScreen, menu, VERSION)
                JSkatMaster.INSTANCE.setView(jskatView)

                return InitializedGuiElements(menu, jskatView)
            }
        }

        showSplashScreen(targetScreen, primaryStage, startupTasks) {
            showJSkatMainWindow(
                targetScreen,
                savedScreenPosition,
                startupTasks.valueProperty().get().menu,
                startupTasks.valueProperty().get().jskatView
            )
        }

        Thread(startupTasks).start()
    }

    private fun showSplashScreen(
        targetScreen: Screen,
        splashStage: Stage,
        startupTask: Task<*>,
        initializationCompleteHandler: () -> Unit
    ) {
        val splashScreenImage = ImageView(
            Image(ClassLoader.getSystemResourceAsStream("org/jskat/gui/img/gui/splash.png"))
        )
        val splashScreenProgressBar = ProgressBar().apply {
            prefWidth = SPLASH_WIDTH.toDouble()
        }
        val splashScreenProgressText = Label("Loading JSkat...").apply {
            alignment = Pos.CENTER
        }
        val splashScreenLayout = VBox().apply {
            children.addAll(splashScreenImage, splashScreenProgressBar, splashScreenProgressText)
            style = "-fx-padding: 5; -fx-spacing: 5; -fx-border-width:2;"
            effect = DropShadow()
        }

        splashScreenProgressText.textProperty().bind(startupTask.messageProperty())
        splashScreenProgressBar.progressProperty().bind(startupTask.progressProperty())

        startupTask.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                splashScreenProgressBar.progressProperty().unbind()
                splashScreenProgressBar.progress = 1.0
                splashStage.toFront()

                FadeTransition(Duration.seconds(1.0), splashScreenLayout).apply {
                    fromValue = 1.0
                    toValue = 0.0
                    setOnFinished { splashStage.hide() }
                    play()
                }

                initializationCompleteHandler()
            }
        }

        val splashScene = Scene(splashScreenLayout).apply {
            // TODO: set this globally
            stylesheets.add("/org/jskat/gui/javafx/jskat.css")
        }

        splashStage.apply {
            initStyle(StageStyle.UNDECORATED)
            scene = splashScene
            val bounds: Rectangle2D = targetScreen.bounds
            x = bounds.minX + bounds.width / 2 - SPLASH_WIDTH / 2
            y = bounds.minY + bounds.height / 2 - SPLASH_HEIGHT / 2
        }

        splashStage.show()
    }

    private data class InitializedGuiElements(
        val menu: MenuBar,
        val jskatView: JSkatViewImpl
    )
} 