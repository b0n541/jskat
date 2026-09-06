package org.jskat.gui.javafx.table

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.HBox
import org.jskat.control.gui.action.JSkatAction

/** Owns the one lower-right location in which context commands are displayed. */
internal class SharedActionArea {
    val pane = HBox(10.0).apply { alignment = Pos.CENTER_RIGHT }
    private val controls = mutableMapOf<Pair<ContextPanelType, JSkatAction>, MutableList<Node>>()

    fun register(phaseContent: ContextPanelType, action: JSkatAction, control: Node) {
        controls.getOrPut(phaseContent to action, ::mutableListOf).add(control)
    }

    fun render(projection: ContextRenderingProjection) {
        pane.children.setAll(
            projection.actions.flatMap { controls[projection.phaseContent to it].orEmpty() }.distinct()
        )
    }
}
