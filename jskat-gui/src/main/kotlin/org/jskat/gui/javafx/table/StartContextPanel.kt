package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.main.StartSkatSeriesAction

class StartContextPanel(private val action: StartSkatSeriesAction) : StackPane() {

    private val button: Button

    init {
        style = "-fx-background-color: transparent;"
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        val buttonText = action.getValue(AbstractJSkatAction.NAME) as? String ?: "Start Skat Series"
        button = Button(buttonText)
        button.setOnAction {
            action.actionPerformed(JSkatActionEvent(JSkatAction.START_LOCAL_SERIES, it.source))
        }

        // Initially set the button's disabled state
        button.isDisable = !action.isEnabled

        // Add a listener to the AbstractJSkatAction to observe changes to its "enabled" property
        action.enabledProperty().addListener { _, _, newValue ->
            // Update the JavaFX button on the JavaFX Application Thread
            Platform.runLater {
                button.isDisable = !newValue
            }
        }

        children.add(button)
    }
}
