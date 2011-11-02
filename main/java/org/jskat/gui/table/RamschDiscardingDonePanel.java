/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-09
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

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.JSkatOptions;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.gui.action.JSkatAction;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Rank;

/**
 * Holds widgets for announcing a game
 */
class RamschDiscardingDonePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	static Log log = LogFactory.getLog(RamschDiscardingDonePanel.class);

	JSkatResourceBundle strings;
	JSkatOptions options;

	DiscardPanel discardPanel;
	JSkatUserPanel userPanel;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 * @param userPanel
	 *            User panel
	 * @param discardPanel
	 *            Discard panel
	 */
	RamschDiscardingDonePanel(ActionMap actions, JSkatUserPanel userPanel,
			DiscardPanel discardPanel) {

		this(actions, userPanel, discardPanel, Boolean.FALSE);
	}

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 * @param userPanel
	 *            User panel
	 * @param discardPanel
	 *            Discard panel
	 * @param showAnnounceButton
	 *            TRUE, if the announce button should be shown
	 */
	RamschDiscardingDonePanel(ActionMap actions, JSkatUserPanel userPanel,
			DiscardPanel discardPanel, Boolean showAnnounceButton) {

		strings = JSkatResourceBundle.instance();
		this.userPanel = userPanel;
		this.discardPanel = discardPanel;

		initPanel(actions, showAnnounceButton);
	}

	private void initPanel(final ActionMap actions,
			final Boolean showAnnounceButton) {

		this.setLayout(new MigLayout("fill")); //$NON-NLS-1$
		setOpaque(false);

		JPanel panel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		panel.setOpaque(false);


		if (showAnnounceButton.booleanValue()) {

			final JButton announceButton = new JButton(actions.get(JSkatAction.SCHIEBEN));
			announceButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						GameAnnouncement ann = getGameAnnouncement();
						if(ann==null) return; // no valid announcement

						e.setSource(ann);
						// fire event again
						announceButton.dispatchEvent(e);
					} catch (IllegalArgumentException except) {
						log.error(except.getMessage());
					}
				}

				private GameAnnouncement getGameAnnouncement() {
					GameAnnouncementFactory factory = GameAnnouncement .getFactory();
					factory.setGameType(GameType.RAMSCH);

					if (discardPanel.isUserLookedIntoSkat()) {

						CardList discardedCards = discardPanel .getDiscardedCards();
						if (discardedCards.size() != 2) {
							JOptionPane.showMessageDialog(
									RamschDiscardingDonePanel.this,
									strings.getString("invalid_number_of_cards_in_skat"), //$NON-NLS-1$
									strings.getString("invalid_number_of_cards_in_skat_title"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							return null;
						}
						if(!JSkatOptions.instance().isSchieberRamschJacksInSkat() && (discardedCards.get(0).getRank()==Rank.JACK || discardedCards.get(1).getRank()==Rank.JACK)) {
							JOptionPane.showMessageDialog(
									RamschDiscardingDonePanel.this,
									// FIXME (markus, 02.11.11) put message text in resource file
									"Jacks are not allowed in schieberamsch skat!",
									"No Jacks allowed",
									JOptionPane.ERROR_MESSAGE);
							return null;
						}

						factory.setDiscardedCards(discardedCards);

					}
					return factory.getAnnouncement();
				}

			});
			panel.add(announceButton);
		}

		this.add(panel, "center"); //$NON-NLS-1$


	}

}
