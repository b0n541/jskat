package org.jskat.gui.swing.table;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Player;

import javax.swing.*;

/**
 * Holds all widgets for bidding
 */
class BiddingContextPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private BidBubblePanel leftOpponentBid;
    private BidBubblePanel rightOpponentBid;
    private BidBubblePanel userBid;
    private BidBubblePanel foreHandBidLabel;
    private BidBubblePanel middleHandBidLabel;
    private BidBubblePanel rearHandBidLabel;
    private JButton bidButton;
    private JButton passButton;
    private GameAnnouncePanel announcePanel;

    Action makeBidAction;
    Action holdBidAction;

    /**
     * Bidding panel
     *
     * @param actions Action map
     */
    BiddingContextPanel(ActionMap actions, JSkatGraphicRepository bitmaps,
                        JSkatUserPanel userPanel) {

        initPanel(actions, bitmaps, userPanel);
    }

    private void initPanel(ActionMap actions, JSkatGraphicRepository bitmaps,
                           JSkatUserPanel userPanel) {

        setLayout(LayoutFactory.getMigLayout(
                "fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        JPanel blankPanel = new JPanel();
        blankPanel.setOpaque(false);
        add(blankPanel, "width 25%"); //$NON-NLS-1$

        JPanel biddingPanel = getBiddingPanel(actions, bitmaps);
        biddingPanel.setOpaque(false);
        add(biddingPanel, "grow"); //$NON-NLS-1$

        this.announcePanel = new GameAnnouncePanel(actions, userPanel, null);
        add(this.announcePanel, "width 25%"); //$NON-NLS-1$

        setOpaque(false);
    }

    private JPanel getBiddingPanel(ActionMap actions,
                                   JSkatGraphicRepository bitmaps) {

        JPanel biddingPanel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

        this.leftOpponentBid = new BidBubblePanel(bitmaps.getLeftBidBubble());
        this.rightOpponentBid = new BidBubblePanel(bitmaps.getRightBidBubble());
        this.userBid = new BidBubblePanel(bitmaps.getUserBidBubble());

        biddingPanel.add(this.leftOpponentBid, "center"); //$NON-NLS-1$
        biddingPanel.add(this.rightOpponentBid, "center, wrap"); //$NON-NLS-1$
        biddingPanel.add(this.userBid, "span 2, center, wrap"); //$NON-NLS-1$

        this.makeBidAction = actions.get(JSkatAction.MAKE_BID);
        this.holdBidAction = actions.get(JSkatAction.HOLD_BID);
        this.bidButton = new JButton(this.makeBidAction);
        this.passButton = new JButton(actions.get(JSkatAction.PASS_BID));
        biddingPanel.add(this.bidButton, "center"); //$NON-NLS-1$
        biddingPanel.add(this.passButton, "center"); //$NON-NLS-1$

        return biddingPanel;
    }

    void setUserPosition(final Player player) {
        // FIXME (jansch 09.11.2010) code duplication with
        // SkatTablePanel.setPositions()
        switch (player) {
            case FOREHAND:
                this.foreHandBidLabel = this.userBid;
                this.middleHandBidLabel = this.leftOpponentBid;
                this.rearHandBidLabel = this.rightOpponentBid;
                break;
            case MIDDLEHAND:
                this.foreHandBidLabel = this.rightOpponentBid;
                this.middleHandBidLabel = this.userBid;
                this.rearHandBidLabel = this.leftOpponentBid;
                break;
            case REARHAND:
                this.foreHandBidLabel = this.leftOpponentBid;
                this.middleHandBidLabel = this.rightOpponentBid;
                this.rearHandBidLabel = this.userBid;
                break;
        }
    }

    void setBid(final Player player, final int bidValue) {

        switch (player) {
            case FOREHAND:
                this.foreHandBidLabel.setBidValue(bidValue);
                break;
            case MIDDLEHAND:
                this.middleHandBidLabel.setBidValue(bidValue);
                break;
            case REARHAND:
                this.rearHandBidLabel.setBidValue(bidValue);
                break;
        }
    }

    void setPass(final Player player) {

        switch (player) {
            case FOREHAND:
                this.foreHandBidLabel.setBidValue(-1);
                break;
            case MIDDLEHAND:
                this.middleHandBidLabel.setBidValue(-1);
                break;
            case REARHAND:
                this.rearHandBidLabel.setBidValue(-1);
                break;
        }
    }

    void setBidValueToMake(final int bidValue) {

        this.bidButton.setAction(this.makeBidAction);
        this.bidButton.setText(String.valueOf(bidValue));
    }

    void setBidValueToHold(final int bidValue) {

        this.bidButton.setAction(this.holdBidAction);
        this.bidButton.setText(String.valueOf(bidValue));
    }

    void resetPanel() {
        this.foreHandBidLabel.setBidValue(0);
        this.middleHandBidLabel.setBidValue(0);
        this.rearHandBidLabel.setBidValue(0);
        setBidValueToMake(18);
        this.announcePanel.resetPanel();
    }
}
