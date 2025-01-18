package org.jskat.gui.javafx.dialog.firststeps

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.web.WebView
import javafx.stage.Stage
import org.jskat.JSkatFX
import org.jskat.data.JSkatOptions
import org.jskat.gui.javafx.dialog.firststeps.model.FirstStepsDialogModel

class FirstStepsDialogController {

    @FXML
    private lateinit var root: Parent

    @FXML
    private lateinit var firstStepsContent: WebView

    @FXML
    private lateinit var showTipsOnStartUp: CheckBox

    private val model = FirstStepsDialogModel()

    @FXML
    fun initialize() {
        loadFirstStepsText()
        showTipsOnStartUp.selectedProperty().bindBidirectional(model.isShowTipsOnStartUp)
        showTipsOnStartUp.selectedProperty().addListener { _, _, newValue ->
            JSkatOptions.instance().setShowTipsAtStartUp(newValue)
        }
    }

    @FXML
    fun closeFirstStepsDialog() {
        val stage = root.scene.window as Stage
        stage.close()
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