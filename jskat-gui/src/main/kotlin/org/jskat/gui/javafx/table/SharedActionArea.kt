package org.jskat.gui.javafx.table

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.HBox
import org.jskat.control.gui.action.JSkatAction

/** Owns the one lower-right location in which context commands are displayed. */
internal class SharedActionArea {
    val pane = HBox(10.0).apply {
        id = "shared-action-area"
        alignment = Pos.CENTER_RIGHT
    }
    private val controls = mutableMapOf<Pair<ContextPanelType, JSkatAction>, MutableList<Node>>()
    private val phaseIndependentControls = mutableMapOf<JSkatAction, MutableList<Node>>()

    fun register(phaseContent: ContextPanelType, action: JSkatAction, control: Node) {
        controls.getOrPut(phaseContent to action, ::mutableListOf).add(control)
    }

    /** Registers a control whose placement does not depend on the selected phase panel. */
    fun registerIndependentOfPhase(action: JSkatAction, control: Node) {
        phaseIndependentControls.getOrPut(action, ::mutableListOf).add(control)
    }

    fun render(projection: ContextRenderingProjection) {
        pane.children.setAll(
            projection.actions.flatMap { action ->
                controls[projection.phaseContent to action].orEmpty() + phaseIndependentControls[action].orEmpty()
            }.distinct()
        )
    }
}
