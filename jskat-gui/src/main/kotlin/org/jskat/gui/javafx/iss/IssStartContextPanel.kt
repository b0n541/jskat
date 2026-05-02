package org.jskat.gui.javafx.iss

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.IconSize

class IssStartContextPanel(
    tableName: String,
    actions: Map<JSkatAction, AbstractJSkatAction>,
    actionList: List<JSkatAction>
) : VBox() {

    init {
        spacing = 10.0
        alignment = Pos.CENTER

        val bitmaps = JSkatGraphicRepository.INSTANCE

        actionList.forEach { jskatAction ->
            val action = actions[jskatAction]
            if (action != null) {
                val buttonText = action.getValue(AbstractJSkatAction.NAME) as? String ?: jskatAction.name
                val button = Button(buttonText)
                button.graphic = bitmaps.getImageView(action.icon, IconSize.BIG)
                button.setOnAction {
                    action.actionPerformed(JSkatActionEvent(jskatAction, tableName))
                }
                children.add(button)
            }
        }
    }
}
