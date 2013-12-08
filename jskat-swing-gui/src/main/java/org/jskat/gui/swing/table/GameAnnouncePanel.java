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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.JSkatView;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.swing.AbstractI18NComboBoxRenderer;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds widgets for announcing a game
 */
class GameAnnouncePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory
			.getLogger(GameAnnouncePanel.class);

	JSkatResourceBundle strings;
	JSkatOptions options;

	JComboBox gameTypeList = null;
	JCheckBox handBox = null;
	JCheckBox ouvertBox = null;
	JCheckBox schneiderBox = null;
	JCheckBox schwarzBox = null;

	DiscardPanel discardPanel;
	JSkatUserPanel userPanel;

	JSkatView view;

	boolean userPickedUpSkat = false;

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
	GameAnnouncePanel(JSkatView view, final ActionMap actions,
			final JSkatUserPanel userPanel, final DiscardPanel discardPanel) {

		strings = JSkatResourceBundle.instance();
		this.view = view;
		this.userPanel = userPanel;
		this.discardPanel = discardPanel;

		initPanel(actions);
	}

	private void initPanel(final ActionMap actions) {

		this.setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

		JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

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
			public void actionPerformed(final ActionEvent e) {
				// FIXME (jan 28.11.2010) send sorting game type to JSkatMaster
				// --> more view components can benefit from this
				GameType gameType = getSelectedGameType();

				if (gameType != null) {
					userPanel.setSortGameType(gameType);

					if (userPickedUpSkat) {
						if (gameType == GameType.NULL) {
							ouvertBox.setEnabled(true);
						} else {
							ouvertBox.setEnabled(false);
						}
					} else {
						ouvertBox.setEnabled(true);
						if (gameType != GameType.NULL) {
							schneiderBox.setEnabled(true);
							schwarzBox.setEnabled(true);
							if (ouvertBox.isSelected()) {
								schneiderBox.setSelected(true);
								schwarzBox.setSelected(true);
							}
						} else {
							schneiderBox.setEnabled(false);
							schneiderBox.setSelected(false);
							schwarzBox.setEnabled(false);
							schwarzBox.setSelected(false);
						}
					}
				}
			}
		});
		gameTypeList.setSelectedIndex(-1);

		handBox = new JCheckBox(strings.getString("hand")); //$NON-NLS-1$
		handBox.setEnabled(false);
		ouvertBox = createOuvertBox();
		schneiderBox = new JCheckBox(strings.getString("schneider")); //$NON-NLS-1$
		schwarzBox = createSchwarzBox();

		panel.add(this.gameTypeList, "grow, wrap"); //$NON-NLS-1$
		panel.add(handBox, "wrap"); //$NON-NLS-1$
		panel.add(this.ouvertBox, "wrap"); //$NON-NLS-1$
		panel.add(this.schneiderBox, "wrap"); //$NON-NLS-1$
		panel.add(this.schwarzBox, "wrap"); //$NON-NLS-1$

		final JButton announceButton = new JButton(
				actions.get(JSkatAction.ANNOUNCE_GAME));
		announceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {

				if (getSelectedGameType() != null) {

					try {
						GameAnnouncement ann = getGameAnnouncement();
						if (ann == null) {
							return;
						}
						e.setSource(ann);
					} catch (IllegalArgumentException except) {
						log.error(except.getMessage());
					}
				} else {
					e.setSource(null);
				}

				// fire event again
				announceButton.dispatchEvent(e);
			}

			private GameAnnouncement getGameAnnouncement() {
				GameAnnouncementFactory factory = GameAnnouncement.getFactory();
				GameType gameType = getSelectedGameType();
				factory.setGameType(gameType);

				if (discardPanel.isUserLookedIntoSkat()) {

					CardList discardedCards = discardPanel.getDiscardedCards();
					if (discardedCards.size() != 2) {

						view.showErrorMessage(
								strings.getString("invalid_number_of_cards_in_skat_title"), //$NON-NLS-1$
								strings.getString("invalid_number_of_cards_in_skat_message")); //$NON-NLS-1$
						return null;
					}
					factory.setDiscardedCards(discardedCards);
					if (GameType.NULL.equals(gameType)
							&& ouvertBox.isSelected()) {
						factory.setOuvert(true);
					}
				} else {

					if (handBox.isSelected()) {
						factory.setHand(Boolean.TRUE);
					}
					if (ouvertBox.isSelected()) {
						factory.setOuvert(Boolean.TRUE);
					}
					if (schneiderBox.isSelected()) {
						factory.setSchneider(Boolean.TRUE);
					}
					if (schwarzBox.isSelected()) {
						factory.setSchneider(Boolean.TRUE);
					}
				}
				return factory.getAnnouncement();
			}

		});
		panel.add(announceButton);

		add(panel, "center"); //$NON-NLS-1$

		setOpaque(false);

		resetPanel();
	}

	GameType getSelectedGameType() {
		Object selectedItem = gameTypeList.getSelectedItem();
		return (GameType) selectedItem;
	}

	private JCheckBox createOuvertBox() {
		final JCheckBox result = new JCheckBox(strings.getString("ouvert")); //$NON-NLS-1$

		result.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (result.isSelected() && handBox.isSelected()
						&& getSelectedGameType() != null) {
					// hand ouvert
					if (GameType.NULL != getSelectedGameType()) {
						schneiderBox.setSelected(true);
						schwarzBox.setSelected(true);
					}
				}
			}
		});

		return result;
	}

	private JCheckBox createSchwarzBox() {
		final JCheckBox result = new JCheckBox(strings.getString("schwarz")); //$NON-NLS-1$

		result.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (result.isSelected()) {
					schneiderBox.setSelected(true);
				}
			}
		});

		return result;
	}

	void resetPanel() {

		this.gameTypeList.setSelectedIndex(-1);
		handBox.setSelected(true);
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
		public String getValueText(final Object value) {

			String result = " "; //$NON-NLS-1$

			GameType gameType = (GameType) value;

			if (gameType != null) {
				result = strings.getGameType(gameType);
			}

			return result;
		}
	}

	void setUserPickedUpSkat(final boolean isUserPickedUpSkat) {

		userPickedUpSkat = isUserPickedUpSkat;

		if (isUserPickedUpSkat) {
			handBox.setSelected(false);
			if (GameType.NULL.equals(getSelectedGameType())) {
				ouvertBox.setEnabled(true);
			} else {
				ouvertBox.setEnabled(false);
			}
			schneiderBox.setEnabled(false);
			schwarzBox.setEnabled(false);
		} else {
			handBox.setSelected(true);
			ouvertBox.setEnabled(true);
			schneiderBox.setEnabled(true);
			schwarzBox.setEnabled(true);
		}
	}
}
