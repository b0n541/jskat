package org.jskat.gui.javafx.table

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
        // CardPanel now handles its own clipping and minHeight in its init.
        return panel
    }
}
