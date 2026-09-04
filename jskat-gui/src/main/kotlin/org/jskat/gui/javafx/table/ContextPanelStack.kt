package org.jskat.gui.javafx.table

import javafx.scene.Node
import javafx.scene.layout.StackPane

/** Holds the mutually exclusive panels displayed in the centre of a skat table. */
internal class ContextPanelStack {
    val pane = StackPane()
    private val panels = mutableMapOf<ContextPanelType, Node>()

    fun add(panelType: ContextPanelType, panel: Node) {
        panels.put(panelType, panel)?.let { previousPanel ->
            previousPanel.isVisible = false
            pane.children.remove(previousPanel)
        }
        pane.children.add(panel)
        panel.isVisible = false
    }

    fun show(panelType: ContextPanelType) {
        panels.forEach { (type, panel) -> panel.isVisible = (type == panelType) }
    }
}
