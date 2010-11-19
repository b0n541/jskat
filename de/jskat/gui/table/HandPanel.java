/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Abstract class for a panel representing a players hand
 */
abstract class HandPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat format = ((DecimalFormat) DecimalFormat
			.getInstance());

	/**
	 * Parent panel
	 */
	SkatTablePanel parent = null;
	/**
	 * Player position
	 */
	Player position;
	/**
	 * Card images
	 */
	JSkatGraphicRepository bitmaps;
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
	 * @param newParent
	 *            Skat table panel
	 * @param jskatBitmaps
	 *            Card images
	 */
	HandPanel(SkatTablePanel newParent, JSkatGraphicRepository jskatBitmaps,
			int maxCards) {

		this.parent = newParent;
		setActionMap(this.parent.getActionMap());
		this.bitmaps = jskatBitmaps;
		this.maxCardCount = maxCards;

		this.setOpaque(false);

		this.headerLabel = new JLabel(" "); //$NON-NLS-1$

		format.applyPattern("00");

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
		add(header, "shrinky, wrap"); //$NON-NLS-1$

		this.cardPanel = new CardPanel(this, this.bitmaps, true);
		add(this.cardPanel, "growy"); //$NON-NLS-1$
	}

	private Border getPanelBorder() {

		Border result = null;

		if (activePlayer) {
			result = BorderFactory.createLineBorder(Color.red);
		} else {
			result = BorderFactory.createLineBorder(Color.black);
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

		this.position = newPosition;
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

		return this.position;
	}

	private void refreshHeaderText() {

		StringBuffer headerText = new StringBuffer();

		headerText.append(this.playerName).append(": ");

		if (position != null) {
			switch (this.position) {
			case FORE_HAND:
				headerText.append("Fore hand");
				break;
			case MIDDLE_HAND:
				headerText.append("Middle hand");
				break;
			case HIND_HAND:
				headerText.append("Hind hand");
				break;
			}
			headerText.append(" Time: ");
			headerText.append(getPlayerTimeString(this.playerTime));
			headerText.append(" Bid: ");
			headerText.append(bidValue);

			if (playerPassed) {
				headerText.append(" (passed)");
			}

			if (declarer) {
				headerText.append(" (Declarer)");
			}
		}

		this.headerLabel.setText(headerText.toString());
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

		this.cardPanel.addCard(newCard);
	}

	/**
	 * Adds a card to the panel
	 * 
	 * @param newCard
	 *            Card
	 */
	void addCards(Collection<Card> newCards) {

		this.cardPanel.addCards(newCards);
	}

	/**
	 * Removes a card from the panel
	 * 
	 * @param cardToRemove
	 *            Card
	 */
	void removeCard(Card cardToRemove) {

		this.cardPanel.removeCard(cardToRemove);
	}

	/**
	 * Removes all cards from the panel and resets other values
	 */
	void clearHandPanel() {

		this.cardPanel.clearCards();
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

		this.cardPanel.hideCards();
	}

	/**
	 * Shows all cards on the panel
	 */
	void showCards() {

		this.cardPanel.showCards();
	}

	void setSortGameType(GameType newGameType) {

		this.cardPanel.setSortType(newGameType);
	}

	boolean isHandFull() {

		return this.cardPanel.getCardCount() == this.maxCardCount;
	}

	void setPlayerName(String newName) {

		this.playerName = newName;

		this.refreshHeaderText();
	}

	void setPlayerTime(double newTime) {

		this.playerTime = newTime;

		this.refreshHeaderText();
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
