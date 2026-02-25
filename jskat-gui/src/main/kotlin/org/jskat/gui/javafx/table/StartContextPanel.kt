package org.jskat.gui.javafx.table

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.jskat.gui.action.main.StartSkatSeriesAction // Still a Java Swing Action

// Import necessary AWT/Swing classes for interoperability
import javax.swing.Action
import java.awt.event.ActionEvent as AwtActionEvent // Alias to avoid conflict with JavaFX ActionEvent

class StartContextPanel(private val action: StartSkatSeriesAction) : StackPane() {

    init {
        style = "-fx-background-color: transparent;"
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        // Get the text for the button from the Swing Action
        val buttonText = action.getValue(Action.NAME) as? String ?: "Start Skat Series"

        val button = Button(buttonText)
        button.onAction = javafx.event.EventHandler { event: ActionEvent ->
            // Create a dummy AWT ActionEvent to trigger the Swing action
            val awtEvent = AwtActionEvent(button, AwtActionEvent.ACTION_PERFORMED, null)
            action.actionPerformed(awtEvent)
        }

        // Add the button to the StackPane. StackPane centers its children by default.
        children.add(button)
    }
}
