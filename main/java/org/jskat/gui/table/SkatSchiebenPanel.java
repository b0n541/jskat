/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.LayoutFactory;
import org.jskat.gui.action.JSkatAction;
import org.jskat.util.CardList;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Rank;

/**
 * Holds widgets for announcing a game
 */
class SkatSchiebenPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	static Log log = LogFactory.getLog(SkatSchiebenPanel.class);

	JSkatResourceBundle strings;
	JSkatOptions options;

	DiscardPanel discardPanel;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 * @param discardPanel
	 *            Discard panel
	 * @param showAnnounceButton
	 *            TRUE, if the announce button should be shown
	 */
	SkatSchiebenPanel(ActionMap actions, DiscardPanel discardPanel) {

		strings = JSkatResourceBundle.instance();
		this.discardPanel = discardPanel;

		initPanel(actions);
	}

	private void initPanel(final ActionMap actions) {

		this.setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		setOpaque(false);

		JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		panel.setOpaque(false);

		final JButton schiebenButton = new JButton(actions.get(JSkatAction.SCHIEBEN));
		schiebenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					CardList discardedCards = getDiscardedCards();
					if (discardedCards == null)
						return; // no valid announcement

					e.setSource(discardedCards);
					// fire event again
					schiebenButton.dispatchEvent(e);
				} catch (IllegalArgumentException except) {
					log.error(except.getMessage());
				}
			}

			private CardList getDiscardedCards() {
				if (discardPanel.isUserLookedIntoSkat()) {
					CardList discardedCards = discardPanel.getDiscardedCards();
					if (discardedCards.size() != 2) {
						JOptionPane.showMessageDialog(SkatSchiebenPanel.this,
								strings.getString("invalid_number_of_cards_in_skat"), //$NON-NLS-1$
								strings.getString("invalid_number_of_cards_in_skat_title"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
						return null;
					}
					if (!JSkatOptions.instance().isSchieberRamschJacksInSkat()
							&& (discardedCards.get(0).getRank() == Rank.JACK || discardedCards.get(1).getRank() == Rank.JACK)) {
						JOptionPane.showMessageDialog(
								SkatSchiebenPanel.this,
								// FIXME (markus, 02.11.11) put message text
								// in resource file
								"Jacks are not allowed in schieberamsch skat!", "No Jacks allowed",
								JOptionPane.ERROR_MESSAGE);
						return null;
					}

					return discardedCards;
				}
				return new CardList();
			}

		});
		panel.add(schiebenButton, "center");

		this.add(panel, "center"); //$NON-NLS-1$
	}
}
