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

import java.awt.Color;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;

/**
 * Abstract class for a panel representing a players hand
 */
abstract class AbstractHandPanel extends JPanel {

	private static final long serialVersionUID = 1L;

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
	JSkatResourceBundle strings;
	/**
	 * Header panel
	 */
	JPanel header;
	/**
	 * Header label
	 */
	JLabel headerLabel;
	/**
	 * Icon panel
	 */
	IconPanel iconPanel;
	/**
	 * Clock panel
	 */
	ClockPanel clockPanel;
	/**
	 * Player name
	 */
	String playerName;
	/**
	 * Player bid
	 */
	int bidValue;

	boolean showIssWidgets;

	ClickableCardPanel cardPanel;

	/**
	 * Maximum card count
	 */
	int maxCardCount = 0;

	boolean activePlayer = false;

	boolean playerPassed = false;

	boolean playerGeschoben = false;

	boolean declarer = false;

	private boolean playerContra;

	private boolean playerRe;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Action map
	 * @param maxCards
	 *            the maximum number of cards
	 * @param showIssWidgets
	 *            TRUE, if ISS widgets should be shown
	 */
	AbstractHandPanel(final ActionMap actions, final int maxCards,
			final boolean showIssWidgets) {

		setActionMap(actions);
		bitmaps = JSkatGraphicRepository.instance();
		strings = JSkatResourceBundle.instance();
		maxCardCount = maxCards;
		this.showIssWidgets = showIssWidgets;

		setOpaque(false);

		headerLabel = new JLabel(" "); //$NON-NLS-1$
		iconPanel = new IconPanel();
		clockPanel = new ClockPanel();

		initPanel();
	}

	/**
	 * Initializes the panel
	 */
	void initPanel() {

		setLayout(LayoutFactory.getMigLayout("fill", "fill", "[shrink][grow]")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		setBorder(getPanelBorder());

		header = new JPanel(LayoutFactory.getMigLayout(
				"fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		header.add(headerLabel);
		// blank panel
		header.add(new JPanel());
		if (showIssWidgets) {
			header.add(iconPanel);
			header.add(clockPanel);
		}
		add(header, "shrinky, wrap"); //$NON-NLS-1$

		cardPanel = new ClickableCardPanel(this, 1.0, true);
		add(cardPanel, "growy"); //$NON-NLS-1$

		if (JSkatOptions.instance().isCheatDebugMode().booleanValue()) {
			showCards();
		}
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
	void setPosition(final Player newPosition) {

		position = newPosition;
		refreshHeaderText();
	}

	void setBidValue(final int newBidValue) {

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

		headerText.append(playerName).append(": "); //$NON-NLS-1$

		if (position != null) {
			switch (position) {
			case FOREHAND:
				headerText.append(strings.getString("forehand")); //$NON-NLS-1$
				break;
			case MIDDLEHAND:
				headerText.append(strings.getString("middlehand")); //$NON-NLS-1$
				break;
			case REARHAND:
				headerText.append(strings.getString("rearhand")); //$NON-NLS-1$
				break;
			}

			headerText.append(" " + strings.getString("bid") + ": "); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			headerText.append(bidValue);

			if (playerPassed || playerGeschoben || playerContra || playerRe) {

				headerText.append(" ("); //$NON-NLS-1$

				String passedGeschobenContraRe = "";
				if (playerPassed) {
					passedGeschobenContraRe = strings.getString("passed"); //$NON-NLS-1$
				}
				if (playerGeschoben) {
					passedGeschobenContraRe = strings.getString("geschoben"); //$NON-NLS-1$
				}
				if (playerContra) {
					passedGeschobenContraRe += " "
							+ strings.getString("contra");
				}
				if (playerRe) {
					passedGeschobenContraRe += strings.getString("re");
				}
				headerText.append(passedGeschobenContraRe);

				headerText.append(")"); //$NON-NLS-1$
			}

			if (declarer) {
				headerText.append(" (" + strings.getString("declarer") + ")"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		headerLabel.setText(headerText.toString());
	}

	/**
	 * Adds a card to the panel
	 * 
	 * @param newCard
	 *            Card
	 */
	void addCard(final Card newCard) {

		cardPanel.addCard(newCard);
	}

	/**
	 * Adds a Collection of cards to the panel
	 * 
	 * @param newCards
	 *            card collection
	 */
	void addCards(final CardList newCards) {

		cardPanel.addCards(newCards);
	}

	/**
	 * Removes a card from the panel
	 * 
	 * @param cardToRemove
	 *            Card
	 */
	void removeCard(final Card cardToRemove) {

		cardPanel.removeCard(cardToRemove);
	}

	/**
	 * Removes all cards from the panel
	 */
	void removeAllCards() {

		cardPanel.clearCards();
	}

	/**
	 * Removes all cards from the panel and resets other values
	 */
	void clearHandPanel() {

		cardPanel.clearCards();
		bidValue = 0;
		playerPassed = false;
		playerGeschoben = false;
		playerContra = false;
		playerRe = false;
		declarer = false;
		iconPanel.reset();
		refreshHeaderText();
		setActivePlayer(false);
		hideCards();
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

	void setSortGameType(final GameType newGameType) {

		cardPanel.setSortType(newGameType);
	}

	boolean isHandFull() {

		return cardPanel.getCardCount() == maxCardCount;
	}

	public void setPlayerName(final String newName) {

		playerName = newName;

		refreshHeaderText();
	}

	void setPlayerTime(final double newTime) {

		clockPanel.setPlayerTime(newTime);
	}

	void setChatEnabled(final boolean isChatEnabled) {

		iconPanel.setChatEnabled(isChatEnabled);
	}

	void setReadyToPlay(final boolean isReadyToPlay) {

		iconPanel.setReadyToPlay(isReadyToPlay);
	}

	void setResign(final boolean isResign) {

		iconPanel.setResign(isResign);
	}

	boolean isActivePlayer() {
		return activePlayer;
	}

	void setActivePlayer(final boolean isActivePlayer) {
		activePlayer = isActivePlayer;
		setBorder(getPanelBorder());

		updateIssWidgets(isActivePlayer);
	}

	private void updateIssWidgets(final boolean isActivePlayer) {
		if (showIssWidgets) {
			if (isActivePlayer) {
				clockPanel.setActive();
			} else {
				clockPanel.setInactive();
			}
		}
	}

	void setPass(final boolean isPassed) {
		playerPassed = isPassed;
		refreshHeaderText();
	}

	void setDeclarer(final boolean isDeclarer) {
		declarer = isDeclarer;
		refreshHeaderText();
	}

	public String getPlayerName() {

		return playerName;
	}

	public void setGeschoben() {
		playerGeschoben = true;
		refreshHeaderText();
	}

	public void setContra() {
		playerContra = true;
		refreshHeaderText();
	}

	public void setRe() {
		playerRe = true;
		refreshHeaderText();
	}
}
