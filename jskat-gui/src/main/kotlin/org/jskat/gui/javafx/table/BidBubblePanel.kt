package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

class BidBubblePanel(bubbleImage: Image) : StackPane() {
    private val bidLabel = Label()

    init {
        val bubbleView = ImageView(bubbleImage)
        children.addAll(bubbleView, bidLabel)

        bidLabel.font = Font.font("Dialog", FontWeight.BOLD, 16.0)
        setBidValue(0)
    }

    fun setBidValue(bidValue: Int) {
        Platform.runLater {
            if (bidValue > -1) {
                bidLabel.text = bidValue.toString()
            } else {
                bidLabel.text = "X"
            }
        }
    }
}
