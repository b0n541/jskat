/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;
import de.jskat.gui.img.JSkatGraphicRepository.IconSize;
import de.jskat.util.Card;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Abstract class for a panel representing a players hand
 */
abstract class HandPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat format = ((DecimalFormat) NumberFormat
			.getInstance());

	/**
	 * Player position
	 */
	Player position;
	/**
	 * Card images
	 */
	JSkatGraphicRepository bitmaps;
	/**
	 * i18n strings
	 */
	ResourceBundle strings;
	/**
	 * Header panel
	 */
	JPanel header;
	/**
	 * Header label
	 */
	JLabel headerLabel;
	/**
	 * Player name
	 */
	String playerName;
	/**
	 * Player time
	 */
	double playerTime;
	/**
	 * Player bid
	 */
	int bidValue;

	CardPanel cardPanel;

	/**
	 * Maximum card count
	 */
	int maxCardCount = 0;

	boolean activePlayer = false;

	boolean playerPassed = false;

	boolean declarer = false;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 * @param jskatBitmaps
	 *            Card images
	 */
	HandPanel(ActionMap actions, JSkatGraphicRepository jskatBitmaps,
			ResourceBundle jskatStrings, int maxCards) {

		setActionMap(actions);
		bitmaps = jskatBitmaps;
		strings = jskatStrings;
		maxCardCount = maxCards;

		setOpaque(false);

		headerLabel = new JLabel(" "); //$NON-NLS-1$

		format.applyPattern("00"); //$NON-NLS-1$

		initPanel();
	}

	/**
	 * Initializes the panel
	 */
	void initPanel() {

		setLayout(new MigLayout("fill", "fill", "[shrink][grow]")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		setBorder(getPanelBorder());

		header = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		header.add(headerLabel);
		ImageIcon clock = new ImageIcon(bitmaps.getIconImage(Icon.CLOCK,
				IconSize.SMALL));
		JLabel clockLabel = new JLabel(clock);
		header.add(clockLabel);
		add(header, "shrinky, wrap"); //$NON-NLS-1$

		cardPanel = new CardPanel(this, bitmaps, true);
		add(cardPanel, "growy"); //$NON-NLS-1$
	}

	private Border getPanelBorder() {

		Border result = null;

		if (activePlayer) {
			result = BorderFactory.createLineBorder(Color.yellow, 2);
		} else {
			result = BorderFactory.createLineBorder(Color.black, 2);
		}

		return result;
	}

	/**
	 * Sets the player position
	 * 
	 * @param newPosition
	 *            Position
	 */
	void setPosition(Player newPosition) {

		position = newPosition;
		refreshHeaderText();
	}

	void setBidValue(int newBidValue) {

		bidValue = newBidValue;
		refreshHeaderText();
	}

	/**
	 * Gets the player position
	 * 
	 * @return Player position
	 */
	Player getPosition() {

		return position;
	}

	private void refreshHeaderText() {

		StringBuffer headerText = new StringBuffer();

		headerText.append(playerName).append(": ");

		if (position != null) {
			switch (position) {
			case FORE_HAND:
				headerText.append(strings.getObject("fore_hand")); //$NON-NLS-1$
				break;
			case MIDDLE_HAND:
				headerText.append(strings.getObject("middle_hand")); //$NON-NLS-1$
				break;
			case HIND_HAND:
				headerText.append(strings.getObject("hind_hand")); //$NON-NLS-1$
				break;
			}

			headerText.append(getPlayerTimeString(playerTime));
			headerText.append(" Bid: ");
			headerText.append(bidValue);

			if (playerPassed) {
				headerText.append(" (passed)");
			}

			if (declarer) {
				headerText.append(" (Declarer)");
			}
		}

		headerLabel.setText(headerText.toString());
	}

	private String getPlayerTimeString(double playerTimeInSeconds) {

		int minutes = (int) Math.floor(playerTimeInSeconds / 60);
		int seconds = (int) (playerTimeInSeconds - (minutes * 60));

		return format.format(minutes) + ":" + format.format(seconds);
	}

	/**
	 * Adds a card to the panel
	 * 
	 * @param newCard
	 *            Card
	 */
	void addCard(Card newCard) {

		cardPanel.addCard(newCard);
	}

	/**
	 * Adds a card to the panel
	 * 
	 * @param newCard
	 *            Card
	 */
	void addCards(Collection<Card> newCards) {

		cardPanel.addCards(newCards);
	}

	/**
	 * Removes a card from the panel
	 * 
	 * @param cardToRemove
	 *            Card
	 */
	void removeCard(Card cardToRemove) {

		cardPanel.removeCard(cardToRemove);
	}

	/**
	 * Removes all cards from the panel and resets other values
	 */
	void clearHandPanel() {

		cardPanel.clearCards();
		bidValue = 0;
		playerPassed = false;
		declarer = false;
		refreshHeaderText();
		setActivePlayer(false);
	}

	/**
	 * Hides all cards on the panel
	 */
	void hideCards() {

		cardPanel.hideCards();
	}

	/**
	 * Shows all cards on the panel
	 */
	void showCards() {

		cardPanel.showCards();
	}

	void setSortGameType(GameType newGameType) {

		cardPanel.setSortType(newGameType);
	}

	boolean isHandFull() {

		return cardPanel.getCardCount() == maxCardCount;
	}

	void setPlayerName(String newName) {

		playerName = newName;

		refreshHeaderText();
	}

	void setPlayerTime(double newTime) {

		playerTime = newTime;

		refreshHeaderText();
	}

	boolean isActivePlayer() {
		return activePlayer;
	}

	void setActivePlayer(boolean isActivePlayer) {

		activePlayer = isActivePlayer;
		setBorder(getPanelBorder());
	}

	void setPass() {
		playerPassed = true;
		refreshHeaderText();
	}

	void setDeclarer() {
		declarer = true;
		refreshHeaderText();
	}
}
