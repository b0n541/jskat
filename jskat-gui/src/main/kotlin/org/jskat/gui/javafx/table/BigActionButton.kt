package org.jskat.gui.javafx.table

import javafx.beans.value.ObservableBooleanValue
import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository

/** Builds the consistently presented action controls used in a Skat table. */
internal object BigActionButton {
    fun create(
        text: String,
        icon: JSkatGraphicRepository.Icon,
        enabled: ObservableBooleanValue? = null,
        onAction: (ActionEvent) -> Unit
    ): Button = Button(text).apply {
        styleClass.add(STYLE_CLASS)
        graphic = JSkatGraphicRepository.INSTANCE.getImageView(icon, JSkatGraphicRepository.IconSize.BIG)
        enabled?.let { disableProperty().bind(Bindings.not(it)) }
        setOnAction(EventHandler(onAction))
    }

    fun dispatch(action: AbstractJSkatAction?, actionType: JSkatAction, source: Any?) {
        action?.actionPerformed(JSkatActionEvent(actionType, source))
    }

    fun dispatchTableCommand(action: AbstractJSkatAction?, tableName: String, source: Any?) {
        action?.actionPerformed(JSkatActionEvent(tableName, source))
    }

    const val STYLE_CLASS = "big-action-button"
}
