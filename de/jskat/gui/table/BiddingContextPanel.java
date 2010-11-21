/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Font;
import java.util.ResourceBundle;

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
class BiddingContextPanel extends JPanel {

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
	 * @param actions
	 *            Action map
	 */
	BiddingContextPanel(ActionMap actions, ResourceBundle strings) {

		initPanel(actions, strings);
	}

	private void initPanel(ActionMap actions, ResourceBundle strings) {

		this.setLayout(new MigLayout("fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		JPanel biddingPanel = getBiddingPanel(actions);
		this.add(biddingPanel, "grow"); //$NON-NLS-1$

		this.add(new GameAnnouncePanel(actions, strings), "width 25%"); //$NON-NLS-1$

		setOpaque(false);
	}

	private JPanel getBiddingPanel(ActionMap actions) {

		JPanel biddingPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		this.leftOpponentBid = new JLabel("0"); //$NON-NLS-1$
		leftOpponentBid.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

		this.rightOpponentBid = new JLabel("0"); //$NON-NLS-1$
		rightOpponentBid.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

		this.userBid = new JLabel("0"); //$NON-NLS-1$
		userBid.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

		biddingPanel.add(this.leftOpponentBid, "center"); //$NON-NLS-1$
		biddingPanel.add(this.rightOpponentBid, "center, wrap"); //$NON-NLS-1$
		biddingPanel.add(this.userBid, "span 2, center, wrap"); //$NON-NLS-1$

		makeBidAction = actions.get(JSkatAction.MAKE_BID);
		holdBidAction = actions.get(JSkatAction.HOLD_BID);
		this.bidButton = new JButton(makeBidAction);
		this.passButton = new JButton(actions.get(JSkatAction.PASS_BID));
		biddingPanel.add(this.bidButton, "center"); //$NON-NLS-1$
		biddingPanel.add(this.passButton, "center"); //$NON-NLS-1$

		return biddingPanel;
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
