/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Player;

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
	private BidBubblePanel hindHandBidLabel;
	private JButton bidButton;
	private JButton passButton;

	Action makeBidAction;
	Action holdBidAction;

	/**
	 * Bidding panel
	 * 
	 * @param actions
	 *            Action map
	 */
	BiddingContextPanel(ActionMap actions, JSkatGraphicRepository bitmaps,
			JSkatUserPanel userPanel) {

		initPanel(actions, bitmaps, userPanel);
	}

	private void initPanel(ActionMap actions, JSkatGraphicRepository bitmaps,
			JSkatUserPanel userPanel) {

		setLayout(new MigLayout("fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		JPanel biddingPanel = getBiddingPanel(actions, bitmaps);
		biddingPanel.setOpaque(false);
		add(biddingPanel, "grow"); //$NON-NLS-1$

		add(new GameAnnouncePanel(actions, userPanel), "width 25%"); //$NON-NLS-1$

		setOpaque(false);
	}

	private JPanel getBiddingPanel(ActionMap actions,
			JSkatGraphicRepository bitmaps) {

		JPanel biddingPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		leftOpponentBid = new BidBubblePanel(bitmaps.getLeftBidBubbleImage());
		rightOpponentBid = new BidBubblePanel(bitmaps.getRightBidBubbleImage());
		userBid = new BidBubblePanel(bitmaps.getUserBidBubbleImage());

		biddingPanel.add(leftOpponentBid, "center"); //$NON-NLS-1$
		biddingPanel.add(rightOpponentBid, "center, wrap"); //$NON-NLS-1$
		biddingPanel.add(userBid, "span 2, center, wrap"); //$NON-NLS-1$

		makeBidAction = actions.get(JSkatAction.MAKE_BID);
		holdBidAction = actions.get(JSkatAction.HOLD_BID);
		bidButton = new JButton(makeBidAction);
		passButton = new JButton(actions.get(JSkatAction.PASS_BID));
		biddingPanel.add(bidButton, "center"); //$NON-NLS-1$
		biddingPanel.add(passButton, "center"); //$NON-NLS-1$

		return biddingPanel;
	}

	void setUserPosition(Player player) {
		// FIXME (jansch 09.11.2010) code duplication with
		// SkatTablePanel.setPositions()
		switch (player) {
		case FORE_HAND:
			foreHandBidLabel = userBid;
			middleHandBidLabel = leftOpponentBid;
			hindHandBidLabel = rightOpponentBid;
			break;
		case MIDDLE_HAND:
			foreHandBidLabel = rightOpponentBid;
			middleHandBidLabel = userBid;
			hindHandBidLabel = leftOpponentBid;
			break;
		case HIND_HAND:
			foreHandBidLabel = leftOpponentBid;
			middleHandBidLabel = rightOpponentBid;
			hindHandBidLabel = userBid;
			break;
		}
	}

	void setBid(Player player, int bidValue) {

		switch (player) {
		case FORE_HAND:
			foreHandBidLabel.setBidValue(bidValue);
			break;
		case MIDDLE_HAND:
			middleHandBidLabel.setBidValue(bidValue);
			break;
		case HIND_HAND:
			hindHandBidLabel.setBidValue(bidValue);
			break;
		}
	}

	void setPass(Player player) {

		switch (player) {
		case FORE_HAND:
			foreHandBidLabel.setBidValue(-1);
			break;
		case MIDDLE_HAND:
			middleHandBidLabel.setBidValue(-1);
			break;
		case HIND_HAND:
			hindHandBidLabel.setBidValue(-1);
			break;
		}
	}

	void setBidValueToMake(int bidValue) {

		bidButton.setAction(makeBidAction);
		bidButton.setText(String.valueOf(bidValue));
	}

	void setBidValueToHold(int bidValue) {

		bidButton.setAction(holdBidAction);
		bidButton.setText(String.valueOf(bidValue));
	}

	void resetPanel() {

		foreHandBidLabel.setBidValue(0);
		middleHandBidLabel.setBidValue(0);
		hindHandBidLabel.setBidValue(0);
		bidButton.setText("18"); //$NON-NLS-1$
	}
}
