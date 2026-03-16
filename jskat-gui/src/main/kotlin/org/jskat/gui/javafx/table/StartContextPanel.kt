package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.jskat.gui.action.main.StartSkatSeriesAction
import java.beans.PropertyChangeListener
import javax.swing.Action
import java.awt.event.ActionEvent as AwtActionEvent

class StartContextPanel(private val action: StartSkatSeriesAction) : StackPane() {

    private val button: Button

    init {
        style = "-fx-background-color: transparent;"
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        val buttonText = action.getValue(Action.NAME) as? String ?: "Start Skat Series"
        button = Button(buttonText)
        button.onAction = javafx.event.EventHandler { event: ActionEvent ->
            val awtEvent = AwtActionEvent(button, AwtActionEvent.ACTION_PERFORMED, null)
            action.actionPerformed(awtEvent)
        }

        // Initially set the button's disabled state
        button.isDisable = !action.isEnabled

        // Add a listener to the Swing Action to observe changes to its "enabled" property
        action.addPropertyChangeListener(PropertyChangeListener { evt ->
            if (evt.propertyName == "enabled") {
                // Update the JavaFX button on the JavaFX Application Thread
                Platform.runLater {
                    button.isDisable = !(evt.newValue as Boolean)
                }
            }
        })

        children.add(button)
    }
}
