/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.GameAnnouncementWithDiscardedCards;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.AbstractI18NComboBoxRenderer;
import org.jskat.gui.action.JSkatAction;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;


/**
 * Holds widgets for announcing a game
 */
class GameAnnouncePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	static Log log = LogFactory.getLog(GameAnnouncePanel.class);

	JSkatResourceBundle strings;
	JSkatOptions options;

	JComboBox gameTypeList = null;
	JCheckBox ouvertBox = null;
	JCheckBox schneiderBox = null;
	JCheckBox schwarzBox = null;

	DiscardPanel discardPanel;
	JSkatUserPanel userPanel;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 */
	GameAnnouncePanel(ActionMap actions, JSkatUserPanel newUserPanel) {

		this(actions, newUserPanel, null);
	}

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 */
	GameAnnouncePanel(ActionMap actions, JSkatUserPanel newUserPanel,
			DiscardPanel newDiscardPanel) {

		strings = JSkatResourceBundle.instance();
		userPanel = newUserPanel;

		if (newDiscardPanel != null) {
			discardPanel = newDiscardPanel;
		}

		initPanel(actions);
	}

	private void initPanel(final ActionMap actions) {

		this.setLayout(new MigLayout("fill")); //$NON-NLS-1$

		JPanel panel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		this.gameTypeList = new JComboBox();
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(GameType.GRAND);
		model.addElement(GameType.CLUBS);
		model.addElement(GameType.SPADES);
		model.addElement(GameType.HEARTS);
		model.addElement(GameType.DIAMONDS);
		model.addElement(GameType.NULL);
		this.gameTypeList.setModel(model);
		gameTypeList.setRenderer(new GameTypeComboBoxRenderer());
		gameTypeList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// FIXME (jan 28.11.2010) send sorting game type to JSkatMaster
				// --> more view components can benefit from this
				GameType gameType = (GameType) gameTypeList.getSelectedItem();

				if (gameType != null) {
					userPanel.setSortGameType(gameType);
				}
			}
		});
		this.gameTypeList.setSelectedIndex(-1);

		this.ouvertBox = new JCheckBox(strings.getString("ouvert")); //$NON-NLS-1$
		this.schneiderBox = new JCheckBox(strings.getString("schneider")); //$NON-NLS-1$
		this.schwarzBox = new JCheckBox(strings.getString("schwarz")); //$NON-NLS-1$

		panel.add(this.gameTypeList, "grow, wrap"); //$NON-NLS-1$
		panel.add(this.ouvertBox, "wrap"); //$NON-NLS-1$
		panel.add(this.schneiderBox, "wrap"); //$NON-NLS-1$
		panel.add(this.schwarzBox, "wrap"); //$NON-NLS-1$

		if (discardPanel != null) {

			final JButton playButton = new JButton(
					actions.get(JSkatAction.ANNOUNCE_GAME));
			playButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					if (gameTypeList.getSelectedItem() != null) {

						try {
							GameAnnouncementWithDiscardedCards ann = getGameAnnouncement();

							e.setSource(ann);
							// fire event again
							playButton.dispatchEvent(e);
						} catch (IllegalArgumentException except) {
							log.error(except.getMessage());
						}
					}
				}

				private GameAnnouncementWithDiscardedCards getGameAnnouncement() {
					GameAnnouncementWithDiscardedCards ann = new GameAnnouncementWithDiscardedCards();
					ann.setGameType(getGameTypeFromSelectedItem());

					if (discardPanel.isUserLookedIntoSkat()) {
						ann.setHand(false);
						CardList discardedCards = discardPanel
								.getDiscardedCards();
						if (discardedCards.size() != 2) {
							JOptionPane.showMessageDialog(
									GameAnnouncePanel.this,
									strings.getString("invalid_number_of_cards_in_skat"), //$NON-NLS-1$
									strings.getString("invalid_number_of_cards_in_skat_title"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
						}
						ann.setDiscardedCards(discardedCards);
					} else {
						ann.setHand(true);
						ann.setOuvert(GameAnnouncePanel.this.ouvertBox
								.isSelected());
						ann.setSchneider(GameAnnouncePanel.this.schneiderBox
								.isSelected());
						ann.setSchwarz(GameAnnouncePanel.this.schwarzBox
								.isSelected());
					}
					return ann;
				}

				private GameType getGameTypeFromSelectedItem() {
					Object selectedItem = gameTypeList.getSelectedItem();

					return (GameType) selectedItem;
				}
			});
			panel.add(playButton);
		}

		this.add(panel, "center"); //$NON-NLS-1$

		setOpaque(false);

		resetPanel();
	}

	void resetPanel() {

		this.gameTypeList.setSelectedIndex(-1);
		this.ouvertBox.setSelected(false);
		this.schneiderBox.setSelected(false);
		this.schwarzBox.setSelected(false);
	}

	private class GameTypeComboBoxRenderer extends AbstractI18NComboBoxRenderer {

		private static final long serialVersionUID = 1L;

		GameTypeComboBoxRenderer() {
			super();
		}

		@Override
		public String getValueText(Object value) {

			String result = " "; //$NON-NLS-1$

			GameType gameType = (GameType) value;

			if (gameType != null) {
				result = strings.getGameType(gameType);
			}

			return result;
		}
	}
}
