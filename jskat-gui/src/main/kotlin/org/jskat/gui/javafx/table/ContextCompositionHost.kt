package org.jskat.gui.javafx.table

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

/** Stable centre composition: phase content above two independently owned lower slots. */
internal class ContextCompositionHost {
    val contextPanelStack = ContextPanelStack()
    val lowerLeft = StackPane()
    val lowerRight = StackPane()
    val sharedActionArea = SharedActionArea()
    private val lowerSpacer = StackPane()
    val lowerRow = HBox(lowerLeft, lowerSpacer, lowerRight)
    val pane = VBox(10.0, contextPanelStack.pane, lowerRow)
    var renderedProjection: ContextRenderingProjection? = null
        private set

    init {
        lowerRow.alignment = Pos.CENTER
        lowerRow.minHeight = 75.0
        lowerRow.prefHeight = 75.0
        lowerRow.maxHeight = 75.0
        HBox.setHgrow(lowerLeft, Priority.ALWAYS)
        HBox.setHgrow(lowerSpacer, Priority.ALWAYS)
        HBox.setHgrow(lowerRight, Priority.ALWAYS)
        StackPane.setAlignment(sharedActionArea.pane, Pos.CENTER_RIGHT)
        lowerRight.children.add(sharedActionArea.pane)
        VBox.setVgrow(contextPanelStack.pane, Priority.ALWAYS)
        VBox.setVgrow(lowerRow, Priority.NEVER)
    }

    fun render(projection: ContextRenderingProjection) {
        renderedProjection = projection
        contextPanelStack.show(projection.phaseContent)
        sharedActionArea.render(projection)
    }
}
