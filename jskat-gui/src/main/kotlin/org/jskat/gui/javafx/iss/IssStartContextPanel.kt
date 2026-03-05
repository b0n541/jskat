package org.jskat.gui.javafx.iss

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import org.jskat.control.gui.action.JSkatAction
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.ActionMap

class IssStartContextPanel(
    private val actions: ActionMap,
    private val actionList: List<JSkatAction>
) : VBox() {

    init {
        spacing = 10.0
        alignment = Pos.CENTER

        actionList.forEach { jskatAction ->
            val swingAction = actions.get(jskatAction)
            if (swingAction != null) {
                val buttonText = swingAction.getValue(Action.NAME) as? String ?: jskatAction.name
                val button = Button(buttonText)
                button.setOnAction {
                    val awtEvent = ActionEvent(it.source, ActionEvent.ACTION_PERFORMED, null)
                    swingAction.actionPerformed(awtEvent)
                }
                children.add(button)
            }
        }
    }
}
