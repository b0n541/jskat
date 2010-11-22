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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.miginfocom.swing.MigLayout;
import de.jskat.data.GameAnnouncementWithDiscardedCards;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.CardFace;
import de.jskat.util.GameType;

/**
 * Holds widgets for announcing a game
 */
class GameAnnouncePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	JComboBox gameTypeList = null;
	JCheckBox ouvertBox = null;
	JCheckBox schneiderBox = null;
	JCheckBox schwarzBox = null;

	DiscardPanel discardPanel;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 * @param strings
	 *            i18n strings
	 */
	GameAnnouncePanel(ActionMap actions, ResourceBundle strings,
			DiscardPanel newDiscardPanel) {

		initPanel(actions, strings, newDiscardPanel);
	}

	private void initPanel(final ActionMap actions, ResourceBundle strings,
			DiscardPanel newDiscardPanel) {

		this.setLayout(new MigLayout("fill")); //$NON-NLS-1$

		JPanel panel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		this.gameTypeList = new JComboBox();
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(GameType.CLUBS);
		model.addElement(GameType.SPADES);
		model.addElement(GameType.HEARTS);
		model.addElement(GameType.DIAMONDS);
		model.addElement(GameType.NULL);
		model.addElement(GameType.GRAND);
		this.gameTypeList.setModel(model);
		// FIXME (jan 17.11.2010) make card face adjustable
		gameTypeList.setRenderer(new GameTypeComboBoxRenderer(CardFace.FRENCH,
				strings));
		// FIXME (jan 21.11.2010) add a change listener to trigger sorting of
		// cards
		this.gameTypeList.setSelectedIndex(-1);

		this.ouvertBox = new JCheckBox(strings.getString("ouvert")); //$NON-NLS-1$
		this.schneiderBox = new JCheckBox(strings.getString("schneider")); //$NON-NLS-1$
		this.schwarzBox = new JCheckBox(strings.getString("schwarz")); //$NON-NLS-1$

		panel.add(this.gameTypeList, "grow, wrap"); //$NON-NLS-1$
		panel.add(this.ouvertBox, "wrap"); //$NON-NLS-1$
		panel.add(this.schneiderBox, "wrap"); //$NON-NLS-1$
		panel.add(this.schwarzBox, "wrap"); //$NON-NLS-1$

		if (newDiscardPanel != null) {

			discardPanel = newDiscardPanel;

			final JButton playButton = new JButton(
					actions.get(JSkatAction.ANNOUNCE_GAME));
			playButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					if (gameTypeList.getSelectedItem() != null) {

						GameAnnouncementWithDiscardedCards ann = new GameAnnouncementWithDiscardedCards();
						ann.setGameType(getGameTypeFromSelectedItem());

						ann.setOuvert(GameAnnouncePanel.this.ouvertBox
								.isSelected());
						ann.setSchneider(GameAnnouncePanel.this.schneiderBox
								.isSelected());
						ann.setSchwarz(GameAnnouncePanel.this.schwarzBox
								.isSelected());

						if (discardPanel.isUserLookedIntoSkat()) {
							ann.setHand(false);
							ann.setDiscardedCards(discardPanel
									.getDiscardedCards());
						} else {
							ann.setHand(true);
						}

						e.setSource(ann);
						// fire event again
						playButton.dispatchEvent(e);
					}
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

	public void resetPanel() {

		this.gameTypeList.setSelectedIndex(-1);
		this.ouvertBox.setSelected(false);
		this.schneiderBox.setSelected(false);
		this.schwarzBox.setSelected(false);
	}

	private class GameTypeComboBoxRenderer extends BasicComboBoxRenderer {

		CardFace cardFace;
		ResourceBundle strings;

		GameTypeComboBoxRenderer(CardFace newCardFace, ResourceBundle newStrings) {

			super();
			cardFace = newCardFace;
			strings = newStrings;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			GameType gameType = (GameType) value;

			if (gameType != null) {
				String cellText = null;

				switch (gameType) {
				case CLUBS:
				case SPADES:
				case HEARTS:
				case DIAMONDS:
					cellText = getGameTypeStringForCardFace(gameType);
					break;
				case NULL:
					cellText = strings.getString("null"); //$NON-NLS-1$
					break;
				case GRAND:
					cellText = strings.getString("grand"); //$NON-NLS-1$
					break;
				default:
					// PASSED_IN and RAMSCH not needed here
					break;
				}

				setText(cellText);
				setFont(list.getFont());
			}

			return this;
		}

		private String getGameTypeStringForCardFace(GameType gameType) {

			String result = null;

			switch (cardFace) {
			case FRENCH:
			case TOURNAMENT:
				result = getFrenchGameTypeString(gameType);
				break;
			case GERMAN:
				result = getGermanGameTypeString(gameType);
				break;
			}

			return result;
		}

		private String getGermanGameTypeString(GameType gameType) {

			String result = null;

			switch (gameType) {
			case CLUBS:
				result = strings.getString("clubs_german"); //$NON-NLS-1$
				break;
			case SPADES:
				result = strings.getString("spades_german"); //$NON-NLS-1$
				break;
			case HEARTS:
				result = strings.getString("hearts_german"); //$NON-NLS-1$
				break;
			case DIAMONDS:
				result = strings.getString("diamonds_german"); //$NON-NLS-1$
				break;
			default:
				// other game types not needed here
				break;
			}

			return result;
		}

		private String getFrenchGameTypeString(GameType gameType) {

			String result = null;

			switch (gameType) {
			case CLUBS:
				result = strings.getString("clubs"); //$NON-NLS-1$
				break;
			case SPADES:
				result = strings.getString("spades"); //$NON-NLS-1$
				break;
			case HEARTS:
				result = strings.getString("hearts"); //$NON-NLS-1$
				break;
			case DIAMONDS:
				result = strings.getString("diamonds"); //$NON-NLS-1$
				break;
			default:
				// other game types not needed here
				break;
			}

			return result;
		}
	}
}
