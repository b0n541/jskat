/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.GameAnnouncementWithDiscardedCards;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.CardFace;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.SkatResourceBundle;

/**
 * Holds widgets for announcing a game
 */
class GameAnnouncePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(GameAnnouncePanel.class);

	ResourceBundle strings;

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
	 * @param jskatStrings
	 *            i18n strings
	 */
	GameAnnouncePanel(ActionMap actions, ResourceBundle jskatStrings,
			JSkatUserPanel newUserPanel) {

		strings = jskatStrings;
		userPanel = newUserPanel;

		initPanel(actions, strings);
	}

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 * @param strings
	 *            i18n strings
	 */
	GameAnnouncePanel(ActionMap actions, ResourceBundle strings,
			DiscardPanel newDiscardPanel, JSkatUserPanel newUserPanel) {

		discardPanel = newDiscardPanel;
		userPanel = newUserPanel;

		initPanel(actions, strings);
	}

	private void initPanel(final ActionMap actions, final ResourceBundle strings) {

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
		// FIXME (jan 17.11.2010) make card face adjustable
		gameTypeList.setRenderer(new GameTypeComboBoxRenderer(CardFace.FRENCH,
				strings));
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

					ann.setOuvert(GameAnnouncePanel.this.ouvertBox.isSelected());
					ann.setSchneider(GameAnnouncePanel.this.schneiderBox
							.isSelected());
					ann.setSchwarz(GameAnnouncePanel.this.schwarzBox
							.isSelected());

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

	private class GameTypeComboBoxRenderer extends JPanel implements
			ListCellRenderer {

		private static final long serialVersionUID = 1L;

		CardFace cardFace;
		ResourceBundle strings;
		JLabel cellItemLabel;

		GameTypeComboBoxRenderer(CardFace newCardFace, ResourceBundle newStrings) {

			super();
			cardFace = newCardFace;
			strings = newStrings;

			setLayout(new MigLayout("fill")); //$NON-NLS-1$
			cellItemLabel = new JLabel(" "); //$NON-NLS-1$
			add(cellItemLabel);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			cellItemLabel.setFont(list.getFont());

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			GameType gameType = (GameType) value;

			if (gameType != null) {
				cellItemLabel.setText(SkatResourceBundle.getGameType(gameType,
						strings, cardFace));
			} else {
				cellItemLabel.setText(" "); //$NON-NLS-1$
			}

			return this;
		}
	}
}
