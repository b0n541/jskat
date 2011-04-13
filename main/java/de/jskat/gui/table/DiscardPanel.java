/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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


package de.jskat.gui.table;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.JSkatResourceBundle;

/**
 * Holds widgets for deciding of looking into skat or playing hand game
 */
class DiscardPanel extends JPanel {

	private static final String LOOK_INTO_SKAT_BUTTON = "LOOK_INTO_SKAT_BUTTON"; //$NON-NLS-1$

	private static final String CARD_PANEL = "CARD_PANEL"; //$NON-NLS-1$

	private static final long serialVersionUID = 1L;

	JSkatGraphicRepository bitmaps;
	JSkatResourceBundle strings;

	private Action lookIntoSkatAction;
	JButton lookIntoSkatButton;
	boolean userLookedIntoSkat = false;

	int maxCardCount = 0;

	CardPanel cardPanel;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Actions for discarding panel
	 * @param jskatBitmaps
	 *            Bitmaps
	 * @param newStrings
	 *            i18n strings
	 * @param newMaxCardCount
	 *            Maximum number of cards
	 */
	public DiscardPanel(ActionMap actions, JSkatGraphicRepository jskatBitmaps,
			int newMaxCardCount) {

		strings = JSkatResourceBundle.instance();

		setActionMap(actions);
		bitmaps = jskatBitmaps;
		maxCardCount = newMaxCardCount;

		initPanel();
	}

	void initPanel() {

		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.black));

		setLayout(new CardLayout());

		cardPanel = new CardPanel(this, bitmaps, false);
		add(cardPanel, CARD_PANEL);

		lookIntoSkatAction = getActionMap().get(JSkatAction.LOOK_INTO_SKAT);
		lookIntoSkatButton = new JButton(lookIntoSkatAction);
		lookIntoSkatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				userLookedIntoSkat = true;
				DiscardPanel.this.showPanel(CARD_PANEL);

				// fire event again
				lookIntoSkatButton.dispatchEvent(e);
			}
		});
		JPanel lookIntoSkatPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		lookIntoSkatPanel.add(lookIntoSkatButton, "center, shrink"); //$NON-NLS-1$
		lookIntoSkatPanel.setOpaque(false);
		add(lookIntoSkatPanel, LOOK_INTO_SKAT_BUTTON);

		setOpaque(false);
		cardPanel.showCards();
	}

	protected void setSkat(CardList skat) {

		addCard(skat.get(0));
		addCard(skat.get(1));

		userLookedIntoSkat = true;
	}

	void addCard(Card card) {

		cardPanel.addCard(card);
	}

	void removeCard(Card card) {

		cardPanel.removeCard(card);
	}

	public void resetPanel() {

		cardPanel.clearCards();
		userLookedIntoSkat = false;
		showPanel(LOOK_INTO_SKAT_BUTTON);
	}

	protected void showPanel(String panelType) {

		((CardLayout) getLayout()).show(DiscardPanel.this, panelType);
	}

	public CardList getDiscardedCards() {

		return (CardList) cardPanel.cards.clone();
	}

	public boolean isUserLookedIntoSkat() {

		return userLookedIntoSkat;
	}

	public boolean isHandFull() {

		return cardPanel.getCardCount() == maxCardCount;
	}
}
