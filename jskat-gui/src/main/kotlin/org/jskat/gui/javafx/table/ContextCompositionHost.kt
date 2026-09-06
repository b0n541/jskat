package org.jskat.gui.javafx.table

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
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
    private val lowerLeftContent = mutableMapOf<LowerLeftContent, LowerLeftRegistration>()
    var renderedProjection: ContextRenderingProjection? = null
        private set

    init {
        lowerLeft.id = "shared-lower-left"
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

    fun registerLowerLeftContent(
        content: LowerLeftContent,
        node: Node,
        onPresentationChanged: (Boolean) -> Unit = {}
    ) {
        val home = node.parent as? Pane ?: error("Lower-left content must have a pane parent")
        lowerLeftContent[content] = LowerLeftRegistration(node, home, home.children.indexOf(node), onPresentationChanged)
    }

    fun render(projection: ContextRenderingProjection) {
        renderedProjection = projection
        contextPanelStack.show(projection.phaseContent)
        renderLowerLeft(projection.lowerLeftContent)
        sharedActionArea.render(projection)
    }

    fun clearLowerLeftContent() = renderLowerLeft(LowerLeftContent.NONE)

    private fun renderLowerLeft(content: LowerLeftContent) {
        lowerLeftContent.forEach { (registeredContent, registration) ->
            if (registeredContent == content) {
                moveTo(registration.node, lowerLeft)
                registration.onPresentationChanged(true)
            } else {
                moveTo(registration.node, registration.home, registration.homeIndex)
                registration.onPresentationChanged(false)
            }
        }
    }

    private fun moveTo(node: Node, destination: Pane, index: Int = destination.children.size) {
        if (node.parent === destination) return
        (node.parent as? Pane)?.children?.remove(node)
        destination.children.add(index.coerceAtMost(destination.children.size), node)
    }

    private data class LowerLeftRegistration(
        val node: Node,
        val home: Pane,
        val homeIndex: Int,
        val onPresentationChanged: (Boolean) -> Unit
    )
}
