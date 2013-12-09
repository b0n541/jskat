/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing.table;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jskat.gui.JSkatView;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Player;

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
	 * @param actions
	 *            Action map
	 */
	BiddingContextPanel(JSkatView view, ActionMap actions,
			JSkatGraphicRepository bitmaps, JSkatUserPanel userPanel) {

		initPanel(view, actions, bitmaps, userPanel);
	}

	private void initPanel(JSkatView view, ActionMap actions,
			JSkatGraphicRepository bitmaps, JSkatUserPanel userPanel) {

		setLayout(LayoutFactory.getMigLayout(
				"fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		JPanel biddingPanel = getBiddingPanel(actions, bitmaps);
		biddingPanel.setOpaque(false);
		add(biddingPanel, "grow"); //$NON-NLS-1$

		announcePanel = new GameAnnouncePanel(view, actions, userPanel, null);
		add(announcePanel, "width 25%"); //$NON-NLS-1$

		setOpaque(false);
	}

	private JPanel getBiddingPanel(ActionMap actions,
			JSkatGraphicRepository bitmaps) {

		JPanel biddingPanel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

		leftOpponentBid = new BidBubblePanel(bitmaps.getLeftBidBubble());
		rightOpponentBid = new BidBubblePanel(bitmaps.getRightBidBubble());
		userBid = new BidBubblePanel(bitmaps.getUserBidBubble());

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

	void setBidValueToMake(final int bidValue) {

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
		setBidValueToMake(18);
		announcePanel.resetPanel();
	}
}
