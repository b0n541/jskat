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
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.action.JSkatAction;
import de.jskat.util.Player;

/**
 * Holds all widgets for bidding
 */
class BiddingPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel leftOpponentBid;
	private JLabel rightOpponentBid;
	private JLabel userBid;
	private JLabel foreHandBidLabel;
	private JLabel middleHandBidLabel;
	private JLabel hindHandBidLabel;
	private JButton bidButton;
	private JButton passButton;

	Action makeBidAction;
	Action holdBidAction;

	/**
	 * Bidding panel
	 * 
	 * @param newActions
	 */
	BiddingPanel(ActionMap newActions) {

		initPanel(newActions);
	}

	private void initPanel(ActionMap newActions) {

		this.setLayout(new MigLayout("fill")); //$NON-NLS-1$

		JPanel biddingPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		this.leftOpponentBid = new JLabel("0"); //$NON-NLS-1$
		this.rightOpponentBid = new JLabel("0"); //$NON-NLS-1$
		this.userBid = new JLabel("0"); //$NON-NLS-1$
		biddingPanel.add(this.leftOpponentBid, "left"); //$NON-NLS-1$
		biddingPanel.add(this.rightOpponentBid, "right, wrap"); //$NON-NLS-1$
		biddingPanel.add(this.userBid, "span 2, center, wrap"); //$NON-NLS-1$
		makeBidAction = newActions.get(JSkatAction.MAKE_BID);
		holdBidAction = newActions.get(JSkatAction.HOLD_BID);
		this.bidButton = new JButton(makeBidAction);
		this.passButton = new JButton(newActions.get(JSkatAction.PASS_BID));
		biddingPanel.add(this.bidButton, "left"); //$NON-NLS-1$
		biddingPanel.add(this.passButton, "right"); //$NON-NLS-1$

		this.add(biddingPanel, "center"); //$NON-NLS-1$

		setOpaque(false);
	}

	void setUserPosition(Player player) {
		// FIXME (jansch 09.11.2010) code duplication with
		// SkatTablePanel.setPositions()
		switch (player) {
		case FORE_HAND:
			this.foreHandBidLabel = this.userBid;
			this.middleHandBidLabel = this.leftOpponentBid;
			this.hindHandBidLabel = this.rightOpponentBid;
			break;
		case MIDDLE_HAND:
			this.foreHandBidLabel = this.rightOpponentBid;
			this.middleHandBidLabel = this.userBid;
			this.hindHandBidLabel = this.leftOpponentBid;
			break;
		case HIND_HAND:
			this.foreHandBidLabel = this.leftOpponentBid;
			this.middleHandBidLabel = this.rightOpponentBid;
			this.hindHandBidLabel = this.userBid;
			break;
		}
	}

	void setBid(Player player, int bidValue) {

		switch (player) {
		case FORE_HAND:
			this.foreHandBidLabel.setText(Integer.toString(bidValue));
			break;
		case MIDDLE_HAND:
			this.middleHandBidLabel.setText(Integer.toString(bidValue));
			break;
		case HIND_HAND:
			this.hindHandBidLabel.setText(Integer.toString(bidValue));
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

		foreHandBidLabel.setText("0"); //$NON-NLS-1$
		middleHandBidLabel.setText("0"); //$NON-NLS-1$
		hindHandBidLabel.setText("0"); //$NON-NLS-1$
		bidButton.setText("18"); //$NON-NLS-1$
	}
}
