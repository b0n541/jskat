/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

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
	private JLabel playerBid;
	private JLabel foreHandBidLabel;
	private JLabel middleHandBidLabel;
	private JLabel hindHandBidLabel;
	private JButton bidButton;
	private JButton passButton;


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
		this.playerBid = new JLabel("0"); //$NON-NLS-1$
		biddingPanel.add(this.leftOpponentBid, "left"); //$NON-NLS-1$
		biddingPanel.add(this.rightOpponentBid, "right, wrap"); //$NON-NLS-1$
		biddingPanel.add(this.playerBid, "span 2, center, wrap"); //$NON-NLS-1$
		this.bidButton = new JButton(newActions.get(JSkatAction.HOLD_BID));
		this.passButton = new JButton(newActions.get(JSkatAction.PASS_BID));
		biddingPanel.add(this.bidButton, "left"); //$NON-NLS-1$
		biddingPanel.add(this.passButton, "right"); //$NON-NLS-1$
		
		this.add(biddingPanel, "center"); //$NON-NLS-1$
	}

	void setPlayerPosition(Player player) {
		
		switch(player) {
		case FORE_HAND:
			this.foreHandBidLabel = this.playerBid;
			this.middleHandBidLabel = this.leftOpponentBid;
			this.hindHandBidLabel = this.rightOpponentBid;
			break;
		case MIDDLE_HAND:
			this.foreHandBidLabel = this.rightOpponentBid;
			this.middleHandBidLabel = this.playerBid;
			this.hindHandBidLabel = this.leftOpponentBid;
			break;
		case HIND_HAND:
			this.foreHandBidLabel = this.leftOpponentBid;
			this.middleHandBidLabel = this.rightOpponentBid;
			this.hindHandBidLabel = this.playerBid;
			break;
		}
	}
	
	void setBid(Player player, int bidValue) {
		
		switch(player) {
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
	
	void clearBids() {
		
		this.foreHandBidLabel.setText("0");
		this.middleHandBidLabel.setText("0");
		this.hindHandBidLabel.setText("0");
	}

	public void setTrickForeHand(Player trickForeHand) {
		// TODO Auto-generated method stub
		
	}
}
