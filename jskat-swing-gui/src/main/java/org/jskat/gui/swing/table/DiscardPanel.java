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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds widgets for deciding of looking into skat or playing hand game
 */
class DiscardPanel extends JPanel {

	private static Logger log = LoggerFactory.getLogger(DiscardPanel.class);
	private static final String PICK_UP_SKAT_BUTTON = "PICK_UP_SKAT_BUTTON"; //$NON-NLS-1$

	private static final String CARD_PANEL = "CARD_PANEL"; //$NON-NLS-1$

	private static final long serialVersionUID = 1L;

	private Action pickUpSkatAction;
	private JButton pickUpSkatButton;
	private boolean userPickedUpSkat = false;

	private int maxCardCount = 0;

	private ClickableCardPanel cardPanel;

	private GameAnnouncePanel announcePanel;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Actions for discarding panel
	 * @param newMaxCardCount
	 *            Maximum number of cards
	 */
	public DiscardPanel(final ActionMap actions, final int newMaxCardCount) {

		setActionMap(actions);
		maxCardCount = newMaxCardCount;

		initPanel();
	}

	void initPanel() {

		setBackground(Color.WHITE);

		setLayout(new CardLayout());

		cardPanel = new ClickableCardPanel(this, 0.75, false);
		add(cardPanel, CARD_PANEL);

		pickUpSkatAction = getActionMap().get(JSkatAction.PICK_UP_SKAT);
		pickUpSkatButton = new JButton(pickUpSkatAction);
		pickUpSkatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {

				log.debug("user picked up skat");
				userPickedUpSkat = true;
				if (announcePanel != null) {
					announcePanel.setUserPickedUpSkat(true);
				}
				DiscardPanel.this.showPanel(CARD_PANEL);

				// fire event again
				pickUpSkatButton.dispatchEvent(e);
			}
		});

		JPanel lookIntoSkatPanel = new JPanel(
				LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		lookIntoSkatPanel.add(pickUpSkatButton, "center"); //$NON-NLS-1$
		lookIntoSkatPanel.setOpaque(false);
		add(lookIntoSkatPanel, PICK_UP_SKAT_BUTTON);

		setOpaque(false);
		cardPanel.showCards();
	}

	protected void setSkat(final CardList skat) {

		clearCards();
		addCard(skat.get(0));
		addCard(skat.get(1));

		userPickedUpSkat = true;
	}

	void clearCards() {
		cardPanel.clearCards();
	}

	void addCard(final Card card) {
		cardPanel.addCard(card);
	}

	void removeCard(final Card card) {
		cardPanel.removeCard(card);
	}

	public void resetPanel() {
		cardPanel.clearCards();
		userPickedUpSkat = false;
		showPanel(PICK_UP_SKAT_BUTTON);
	}

	protected void showPanel(final String panelType) {
		((CardLayout) getLayout()).show(DiscardPanel.this, panelType);
	}

	public CardList getDiscardedCards() {
		return cardPanel.getCards();
	}

	public boolean isUserLookedIntoSkat() {
		return userPickedUpSkat;
	}

	public boolean isHandFull() {
		return cardPanel.getCardCount() == maxCardCount;
	}

	boolean isUserPickedUpSkat() {
		return userPickedUpSkat;
	}

	void setAnnouncePanel(final GameAnnouncePanel announcePanel) {
		this.announcePanel = announcePanel;
	}

}
