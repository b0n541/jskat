package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.Player
import java.awt.image.BufferedImage

class BiddingContextPanel(
    private val actions: Map<JSkatAction, AbstractJSkatAction>,
    bitmaps: JSkatGraphicRepository,
    userPanel: JSkatUserPanel
) : GridPane() {

    private val announcePanel = GameAnnouncePanel(actions, userPanel, null)

    private val leftOpponentBid: BidBubblePanel
    private val rightOpponentBid: BidBubblePanel
    private val userBid: BidBubblePanel

    private var foreHandBidLabel: BidBubblePanel? = null
    private var middleHandBidLabel: BidBubblePanel? = null
    private var rearHandBidLabel: BidBubblePanel? = null

    private val bidButton: Button
    private val passButton: Button

    private val makeBidAction = actions[JSkatAction.MAKE_BID]
    private val holdBidAction = actions[JSkatAction.HOLD_BID]
    private var currentBidAction = makeBidAction

    init {
        style = "-fx-background-color: transparent;"
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        val col1 = ColumnConstraints()
        col1.percentWidth = 25.0
        val col2 = ColumnConstraints()
        col2.hgrow = Priority.ALWAYS
        val col3 = ColumnConstraints()
        col3.percentWidth = 25.0
        columnConstraints.addAll(col1, col2, col3)

        add(announcePanel, 0, 0)

        // Bidding Panel (Center)
        val biddingGrid = GridPane()
        biddingGrid.alignment = Pos.CENTER
        biddingGrid.hgap = 10.0
        biddingGrid.vgap = 10.0

        // Convert AWT images to FX images
        val leftBubble = SwingFXUtils.toFXImage(toBufferedImage(bitmaps.leftBidBubble), null)
        val rightBubble = SwingFXUtils.toFXImage(toBufferedImage(bitmaps.rightBidBubble), null)
        val userBubble = SwingFXUtils.toFXImage(toBufferedImage(bitmaps.userBidBubble), null)

        leftOpponentBid = BidBubblePanel(leftBubble)
        rightOpponentBid = BidBubblePanel(rightBubble)
        userBid = BidBubblePanel(userBubble)

        biddingGrid.add(leftOpponentBid, 0, 0)
        biddingGrid.add(rightOpponentBid, 1, 0)
        biddingGrid.add(userBid, 0, 1, 2, 1)
        setHalignment(userBid, HPos.CENTER)

        bidButton = Button(currentBidAction?.getValue(AbstractJSkatAction.NAME) as? String ?: "").apply {
            graphic = bitmaps.getImageView(JSkatGraphicRepository.Icon.OK, JSkatGraphicRepository.IconSize.BIG)
            setOnAction {
                currentBidAction?.let { action ->
                    Platform.runLater {
                        action.actionPerformed(JSkatActionEvent(JSkatAction.MAKE_BID, it.source))
                    }
                }
            }
        }

        passButton = Button(actions[JSkatAction.PASS_BID]?.getValue(AbstractJSkatAction.NAME) as? String ?: "").apply {
            graphic = bitmaps.getImageView(JSkatGraphicRepository.Icon.STOP, JSkatGraphicRepository.IconSize.BIG)
            setOnAction {
                actions[JSkatAction.PASS_BID]?.let { action ->
                    Platform.runLater {
                        action.actionPerformed(JSkatActionEvent(JSkatAction.PASS_BID, it.source))
                    }
                }
            }
        }

        biddingGrid.add(bidButton, 0, 2)
        biddingGrid.add(passButton, 1, 2)

        add(biddingGrid, 1, 0)

        val blankRegion = Region()
        add(blankRegion, 2, 0)
    }

    private fun toBufferedImage(img: java.awt.Image): BufferedImage {
        if (img is BufferedImage) {
            return img
        }

        // Create a buffered image with transparency
        val bimage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)

        // Draw the image on to the buffered image
        val bGr = bimage.createGraphics()
        bGr.drawImage(img, 0, 0, null)
        bGr.dispose()

        return bimage
    }

    fun setUserPosition(player: Player) {
        when (player) {
            Player.FOREHAND -> {
                foreHandBidLabel = userBid
                middleHandBidLabel = leftOpponentBid
                rearHandBidLabel = rightOpponentBid
            }

            Player.MIDDLEHAND -> {
                foreHandBidLabel = rightOpponentBid
                middleHandBidLabel = userBid
                rearHandBidLabel = leftOpponentBid
            }

            Player.REARHAND -> {
                foreHandBidLabel = leftOpponentBid
                middleHandBidLabel = rightOpponentBid
                rearHandBidLabel = userBid
            }
        }
    }

    fun setBid(player: Player, bidValue: Int) {
        when (player) {
            Player.FOREHAND -> foreHandBidLabel?.setBidValue(bidValue)
            Player.MIDDLEHAND -> middleHandBidLabel?.setBidValue(bidValue)
            Player.REARHAND -> rearHandBidLabel?.setBidValue(bidValue)
        }
    }

    fun setPass(player: Player) {
        when (player) {
            Player.FOREHAND -> foreHandBidLabel?.setBidValue(-1)
            Player.MIDDLEHAND -> middleHandBidLabel?.setBidValue(-1)
            Player.REARHAND -> rearHandBidLabel?.setBidValue(-1)
        }
    }

    fun setNextBidValue(bidValue: Int) {
        currentBidAction = makeBidAction
        Platform.runLater {
            bidButton.text = bidValue.toString()
        }
    }

    fun setBidValueToHold(bidValue: Int) {
        currentBidAction = holdBidAction
        Platform.runLater {
            bidButton.text = bidValue.toString()
        }
    }

    fun resetPanel() {
        Platform.runLater {
            foreHandBidLabel?.setBidValue(0)
            middleHandBidLabel?.setBidValue(0)
            rearHandBidLabel?.setBidValue(0)
            setNextBidValue(18)
            announcePanel.resetPanel()
        }
    }
}
