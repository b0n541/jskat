package org.jskat.gui.javafx.dialog.firststeps

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.Window
import org.jskat.JSkatFX
import org.jskat.data.JSkatOptions
import org.jskat.gui.javafx.JavaFxHostDocumentOpener
import org.jskat.util.JSkatResourceBundle
import org.slf4j.LoggerFactory

class FirstStepsDialog(owner: Window, documentOpener: JavaFxHostDocumentOpener) : Stage() {

    private val LOG = LoggerFactory.getLogger(FirstStepsDialog::class.java)
    private val strings = JSkatResourceBundle.INSTANCE

    private lateinit var root: VBox
    private lateinit var firstStepsContent: WebView
    private lateinit var showTipsOnStartUp: CheckBox
    private lateinit var closeButton: Button

    private val showTipsAtStartUpProperty = SimpleBooleanProperty(JSkatOptions.instance().isShowTipsAtStartUp)

    init {
        LOG.debug("Initializing FirstStepsDialog")

        title = strings.getString("showTips")

        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)

        root = VBox().apply {
            padding = Insets(10.0)
            spacing = 10.0
        }

        firstStepsContent = WebView()
        firstStepsContent.engine.locationProperty().addListener { _, _, location ->
            documentOpener.openIfExternal(location)
        }
        VBox.setVgrow(firstStepsContent, Priority.ALWAYS)

        showTipsOnStartUp = CheckBox(strings.getString("showTipsAtStartup")).apply {
            selectedProperty().bindBidirectional(showTipsAtStartUpProperty)
        }
        showTipsAtStartUpProperty.addListener { _, _, newValue ->
            JSkatOptions.instance().isShowTipsAtStartUp = newValue
        }

        closeButton = Button().apply {
            text = strings.getString("close")
            setOnAction { close() }
        }

        root.children.addAll(
            firstStepsContent,
            HBox().apply {
                alignment = Pos.BASELINE_CENTER
                children.addAll(
                    showTipsOnStartUp,
                    Pane().apply { HBox.setHgrow(this, Priority.ALWAYS) },
                    closeButton
                )
            }
        )

        loadFirstStepsText()

        this.scene = Scene(root, 600.0, 400.0)
    }

    private fun loadFirstStepsText() {
        val engine = firstStepsContent.engine
        val contentUrl = JSkatFX::class.java
            .getResource("/org/jskat/gui/help/${JSkatOptions.instance().i18NCode}/gettingStarted.html")
            ?.toExternalForm()
            ?: throw IllegalStateException("Could not find getting started content")
        engine.load(contentUrl)
    }
}
