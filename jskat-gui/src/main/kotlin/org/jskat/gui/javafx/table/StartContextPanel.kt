package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.main.StartSkatSeriesAction
import org.jskat.gui.img.JSkatGraphicRepository

class StartContextPanel(private val action: StartSkatSeriesAction) : StackPane() {

    val actionControl: Button

    init {
        style = "-fx-background-color: transparent;"
        alignment = Pos.CENTER
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        actionControl = Button(action.getValue(AbstractJSkatAction.NAME) as? String ?: "Start Skat Series").apply {
            setOnAction {
                action.actionPerformed(JSkatActionEvent(JSkatAction.START_LOCAL_SERIES, it.source))
            }

            graphic = JSkatGraphicRepository.INSTANCE.getImageView(
                action.icon, JSkatGraphicRepository.IconSize.BIG
            )

            // Initially set the button's disabled state
            isDisable = !action.isEnabled

            // Add a listener to the AbstractJSkatAction to observe changes to its "enabled" property
            action.enabledProperty().addListener { _, _, newValue ->
                // Update the JavaFX button on the JavaFX Application Thread
                Platform.runLater {
                    actionControl.isDisable = !newValue
                }
            }
        }

        children.add(actionControl)
    }
}
