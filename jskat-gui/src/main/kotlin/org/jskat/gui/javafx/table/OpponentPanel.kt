package org.jskat.gui.javafx.table

import javafx.scene.shape.Rectangle

class OpponentPanel(
    maxCards: Int,
    showIssWidgets: Boolean
) : AbstractHandPanel(maxCards, showIssWidgets) {

    init {
        // Allow the OpponentPanel itself to shrink below its content's minimum size.
        minHeight = 0.0
    }

    override fun createCardPanel(): CardPanel {
        val panel = super.createCardPanel()

        // Also allow the inner CardPanel to shrink to zero.
        panel.minHeight = 0.0

        // Create a clip that is bound to the panel's size. This is what
        // visually cuts off the cards as the panel shrinks.
        val clipRect = Rectangle()
        clipRect.widthProperty().bind(panel.widthProperty())
        clipRect.heightProperty().bind(panel.heightProperty())
        panel.clip = clipRect

        return panel
    }
}
