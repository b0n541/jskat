package org.jskat.gui.javafx.iss

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import org.jskat.control.gui.action.JSkatAction
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.javafx.table.BigActionButton

class IssStartContextPanel(
    tableName: String,
    actions: Map<JSkatAction, AbstractJSkatAction>,
    actionList: List<JSkatAction>
) : VBox() {

    init {
        spacing = 10.0
        alignment = Pos.CENTER

        val actionButtons = mutableListOf<Button>()

        actionList.forEach { jskatAction ->
            val action = actions[jskatAction]
            if (action != null) {
                val buttonText = action.getValue(AbstractJSkatAction.NAME) as? String ?: jskatAction.name
                val button = BigActionButton.create(buttonText, action.icon) {
                    BigActionButton.dispatch(action, jskatAction, tableName)
                }
                children.add(button)
                actionButtons.add(button)
            }
        }

        sceneProperty().addListener { _, _, scene ->
            if (scene != null) {
                Platform.runLater {
                    actionButtons.forEach(Button::applyCss)
                    val widestButton = actionButtons.maxOfOrNull { it.prefWidth(-1.0) } ?: return@runLater
                    actionButtons.forEach { button ->
                        button.minWidth = widestButton
                        button.prefWidth = widestButton
                        button.maxWidth = widestButton
                    }
                }
            }
        }
    }
}
