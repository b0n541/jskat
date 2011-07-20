/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.table;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Player;

import net.miginfocom.swing.MigLayout;

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

		announcePanel = new GameAnnouncePanel(actions, userPanel);
		add(announcePanel, "width 25%"); //$NON-NLS-1$

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

	void setBid(Player player, int bidValue) {

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

	void setPass(Player player) {

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
		rearHandBidLabel.setBidValue(0);
		bidButton.setText("18"); //$NON-NLS-1$
		announcePanel.resetPanel();
	}
}
