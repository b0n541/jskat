package org.jskat.gui.javafx.table

import javafx.scene.control.Button
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.jskat.control.gui.action.JSkatAction
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.main.StartSkatSeriesAction

class StartContextPanel(private val action: StartSkatSeriesAction) : StackPane() {

    val actionControl: Button

    init {
        style = "-fx-background-color: transparent;"
        alignment = Pos.CENTER
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        actionControl = BigActionButton.create(
            action.getValue(AbstractJSkatAction.NAME) as? String ?: "Start Skat Series",
            action.icon,
            action.enabledProperty()
        ) { BigActionButton.dispatch(action, JSkatAction.START_LOCAL_SERIES, it.source) }

        children.add(actionControl)
    }
}
