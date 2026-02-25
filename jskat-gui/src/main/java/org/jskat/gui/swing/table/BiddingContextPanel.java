package org.jskat.gui.swing.table;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Player;

import javax.swing.*;

/**
 * Holds all widgets for bidding
 */
class BiddingContextPanel extends JPanel {

    private BidBubblePanel leftOpponentBid;
    private BidBubblePanel rightOpponentBid;
    private BidBubblePanel userBid;
    private BidBubblePanel foreHandBidLabel;
    private BidBubblePanel middleHandBidLabel;
    private BidBubblePanel rearHandBidLabel;
    private JButton bidButton;
    private JButton passButton;
    
    private JFXPanel announcePanelContainer;
    private org.jskat.gui.javafx.table.GameAnnouncePanel announcePanel;

    Action makeBidAction;
    Action holdBidAction;

    /**
     * Bidding panel
     *
     * @param actions Action map
     */
    BiddingContextPanel(final ActionMap actions,
                        final JSkatGraphicRepository bitmaps,
                        final JSkatUserPanel userPanel) {

        initPanel(actions, bitmaps, userPanel);
    }

    private void initPanel(final ActionMap actions,
                           final JSkatGraphicRepository bitmaps,
                           final JSkatUserPanel userPanel) {

        setLayout(LayoutFactory.getMigLayout(
                "fill", "[shrink][grow][shrink]", "fill"));

        announcePanelContainer = new JFXPanel();
        announcePanelContainer.setOpaque(false);
        add(announcePanelContainer, "width 25%");
        
        Platform.runLater(() -> {
            announcePanel = new org.jskat.gui.javafx.table.GameAnnouncePanel(actions, userPanel, null);
            announcePanelContainer.setScene(new Scene(announcePanel, javafx.scene.paint.Color.TRANSPARENT));
        });

        final JPanel biddingPanel = getBiddingPanel(actions, bitmaps);
        biddingPanel.setOpaque(false);
        add(biddingPanel, "grow");

        final JPanel blankPanel = new JPanel();
        blankPanel.setOpaque(false);
        add(blankPanel, "width 25%");

        setOpaque(false);
    }

    private JPanel getBiddingPanel(final ActionMap actions,
                                   final JSkatGraphicRepository bitmaps) {

        final JPanel biddingPanel = new JPanel(LayoutFactory.getMigLayout("fill"));

        leftOpponentBid = new BidBubblePanel(bitmaps.getLeftBidBubble());
        rightOpponentBid = new BidBubblePanel(bitmaps.getRightBidBubble());
        userBid = new BidBubblePanel(bitmaps.getUserBidBubble());

        biddingPanel.add(leftOpponentBid, "center");
        biddingPanel.add(rightOpponentBid, "center, wrap");
        biddingPanel.add(userBid, "span 2, center, wrap");

        makeBidAction = actions.get(JSkatAction.MAKE_BID);
        holdBidAction = actions.get(JSkatAction.HOLD_BID);
        bidButton = new JButton(makeBidAction);
        passButton = new JButton(actions.get(JSkatAction.PASS_BID));
        biddingPanel.add(bidButton, "center");
        biddingPanel.add(passButton, "center");

        return biddingPanel;
    }

    void setUserPosition(final Player player) {
        // FIXME (jansch 09.11.2010) code duplication with
        // SkatTablePanel.setPositions()
        switch (player) {
            case FOREHAND:
                foreHandBidLabel = userBid;
                middleHandBidLabel = leftOpponentBid;
                rearHandBidLabel = rightOpponentBid;
                break;
            case MIDDLEHAND:
                foreHandBidLabel = rightOpponentBid;
                middleHandBidLabel = userBid;
                rearHandBidLabel = leftOpponentBid;
                break;
            case REARHAND:
                foreHandBidLabel = leftOpponentBid;
                middleHandBidLabel = rightOpponentBid;
                rearHandBidLabel = userBid;
                break;
        }
    }

    void setBid(final Player player, final int bidValue) {

        switch (player) {
            case FOREHAND:
                foreHandBidLabel.setBidValue(bidValue);
                break;
            case MIDDLEHAND:
                middleHandBidLabel.setBidValue(bidValue);
                break;
            case REARHAND:
                rearHandBidLabel.setBidValue(bidValue);
                break;
        }
    }

    void setPass(final Player player) {

        switch (player) {
            case FOREHAND:
                foreHandBidLabel.setBidValue(-1);
                break;
            case MIDDLEHAND:
                middleHandBidLabel.setBidValue(-1);
                break;
            case REARHAND:
                rearHandBidLabel.setBidValue(-1);
                break;
        }
    }

    void setNextBidValue(final int bidValue) {

        bidButton.setAction(makeBidAction);
        bidButton.setText(String.valueOf(bidValue));
    }

    void setBidValueToHold(final int bidValue) {

        bidButton.setAction(holdBidAction);
        bidButton.setText(String.valueOf(bidValue));
    }

    void resetPanel() {
        foreHandBidLabel.setBidValue(0);
        middleHandBidLabel.setBidValue(0);
        rearHandBidLabel.setBidValue(0);
        setNextBidValue(18);
        if (announcePanel != null) {
            announcePanel.resetPanel();
        }
    }
}
