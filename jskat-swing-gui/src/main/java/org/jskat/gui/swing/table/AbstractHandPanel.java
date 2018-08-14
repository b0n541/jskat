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

	/**
	 * Indicates whether this player is currently the active player
	 */
	boolean isActivePlayer = false;

	/**
	 * Indicates whether this player is an AI player
	 */
	boolean isAIPlayer = false;

	boolean playerPassed = false;

	boolean playerGeschoben = false;

	boolean declarer = false;

	private boolean playerContra;

	private boolean playerRe;

	/**
	 * Constructor
	 *
	 * @param actions               Action map
	 * @param maxCards              the maximum number of cards
	 * @param showIssWidgets        TRUE, if ISS widgets should be shown
	 * @param isNeuralNetworkPlayer TRUE, if this panel is for a neural network
	 *                              opponent
	 */
	AbstractHandPanel(final ActionMap actions, final int maxCards, final boolean showIssWidgets) {

		setActionMap(actions);
		this.bitmaps = JSkatGraphicRepository.INSTANCE;
		this.strings = JSkatResourceBundle.INSTANCE;
		this.maxCardCount = maxCards;
		this.showIssWidgets = showIssWidgets;

		setBackground(new Color(0, 62, 0));

		this.headerLabel = new JLabel(" "); //$NON-NLS-1$
		this.iconPanel = new IconPanel();
		this.clockPanel = new ClockPanel();

		initPanel();
	}

	/**
	 * Initializes the panel
	 */
	void initPanel() {

		setLayout(LayoutFactory.getMigLayout("fill, insets 0", "fill", "[shrink][grow]")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		setBorder(getPanelBorder(this.isActivePlayer));

		String headerInsets = "insets ";
		if (showIssWidgets) {
			headerInsets = headerInsets + "0 5 0 0";
		} else {
			headerInsets = headerInsets + "5";
		}

		this.header = new JPanel(LayoutFactory.getMigLayout("fill, " + headerInsets, "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.header.add(this.headerLabel);
		// blank panel
		this.header.add(new JPanel());
		this.header.add(this.iconPanel);
		if (this.showIssWidgets) {
			iconPanel.setShowIssWidgets(true);
			this.header.add(this.clockPanel);
		}
		add(this.header, "shrinky, wrap"); //$NON-NLS-1$

		this.cardPanel = new ClickableCardPanel(this, 1.0, true);
		add(this.cardPanel, "growy"); //$NON-NLS-1$

		if (JSkatOptions.instance().isCheatDebugMode().booleanValue()) {
			showCards();
		}
	}

	/**
	 * Retrieves the Border for the hand panel for that player. It visually
	 * distinguishes between active and non-active players.
	 *
	 * @return
	 */
	private Border getPanelBorder(final boolean isActivePlayer) {

		Border resultingBorder = null;

		if (isActivePlayer) {
			resultingBorder = BorderFactory.createLineBorder(Color.yellow, 3);
		} else {
			resultingBorder = BorderFactory.createLineBorder(Color.black, 3);
		}

		return resultingBorder;
	}

	/**
	 * Sets the player position
	 *
	 * @param newPosition Position
	 */
	void setPosition(final Player newPosition) {

		this.position = newPosition;
		refreshHeader();
	}

	void setBidValue(final int newBidValue) {

		this.bidValue = newBidValue;
		refreshHeader();
	}

	/**
	 * Gets the player position
	 *
	 * @return Player position
	 */
	Player getPosition() {

		return this.position;
	}

	/**
	 * Refreshes the header of this hand panel, i.e. the text and the thinking icon.
	 */
	private void refreshHeader() {

		final StringBuffer headerText = new StringBuffer();

		headerText.append(this.playerName).append(": "); //$NON-NLS-1$

		if (this.position != null) {
			switch (this.position) {
			case FOREHAND:
				headerText.append(this.strings.getString("forehand")); //$NON-NLS-1$
				break;
			case MIDDLEHAND:
				headerText.append(this.strings.getString("middlehand")); //$NON-NLS-1$
				break;
			case REARHAND:
				headerText.append(this.strings.getString("rearhand")); //$NON-NLS-1$
				break;
			}

			headerText.append(" " + this.strings.getString("bid") + ": "); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			headerText.append(this.bidValue);

			if (playerPassed || playerGeschoben || playerContra || playerRe) {

				headerText.append(" ("); //$NON-NLS-1$

				String passedGeschobenContraRe = "";
				if (playerPassed) {
					passedGeschobenContraRe = strings.getString("passed"); //$NON-NLS-1$
				}
				if (playerGeschoben) {
					passedGeschobenContraRe = strings.getString("geschoben"); //$NON-NLS-1$
				}

				if (passedGeschobenContraRe.length() > 0 && (playerContra || playerRe)) {
					passedGeschobenContraRe += " ";
				}

				if (playerContra) {
					passedGeschobenContraRe += strings.getString("contra");
				}
				if (playerRe) {
					passedGeschobenContraRe += strings.getString("re");
				}

				headerText.append(passedGeschobenContraRe);

				headerText.append(")"); //$NON-NLS-1$
			}

			if (this.declarer) {
				headerText.append(" (" + this.strings.getString("declarer") + ")"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		if (isActivePlayer && isAIPlayer) {
			iconPanel.setThinking(true);
		} else {
			iconPanel.setThinking(false);
		}

		this.headerLabel.setText(headerText.toString());
	}

	/**
	 * Adds a card to the panel
	 *
	 * @param newCard Card
	 */
	void addCard(final Card newCard) {

		this.cardPanel.addCard(newCard);
	}

	/**
	 * Adds a Collection of cards to the panel
	 *
	 * @param newCards card collection
	 */
	void addCards(final CardList newCards) {

		this.cardPanel.addCards(newCards);
	}

	/**
	 * Removes a card from the panel
	 *
	 * @param cardToRemove Card
	 */
	void removeCard(final Card cardToRemove) {

		this.cardPanel.removeCard(cardToRemove);
	}

	/**
	 * Removes all cards from the panel
	 */
	void removeAllCards() {

		this.cardPanel.clearCards();
	}

	/**
	 * Removes all cards from the panel and resets other values
	 */
	void clearHandPanel() {

		this.cardPanel.clearCards();
		this.bidValue = 0;
		this.playerPassed = false;
		this.playerGeschoben = false;
		this.playerContra = false;
		this.playerRe = false;
		this.declarer = false;
		this.iconPanel.reset();
		refreshHeader();
		setActivePlayer(false);
		hideCards();
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

	void setSortGameType(final GameType newGameType) {

		this.cardPanel.setSortType(newGameType);
	}

	boolean isHandFull() {

		return this.cardPanel.getCardCount() == this.maxCardCount;
	}

	public void setPlayerName(final String newName) {

		this.playerName = newName;

		refreshHeader();
	}

	void setPlayerTime(final double newTime) {

		this.clockPanel.setPlayerTime(newTime);
	}

	void setChatEnabled(final boolean isChatEnabled) {

		this.iconPanel.setChatEnabled(isChatEnabled);
	}

	void setReadyToPlay(final boolean isReadyToPlay) {

		this.iconPanel.setReadyToPlay(isReadyToPlay);
	}

	void setResign(final boolean isResign) {

		this.iconPanel.setResign(isResign);
	}

	boolean isActivePlayer() {
		return this.isActivePlayer;
	}

	/**
	 * Sets the flag that indicates whether this player is active (it is the turn of
	 * this player). It also may change the visual indication whether this player is
	 * thinking or not.
	 *
	 * @param isActivePlayer whether this player is the active player
	 */
	void setActivePlayer(final boolean isActivePlayer) {
		this.isActivePlayer = isActivePlayer;
		// a refresh is needed to toggle the "is thinking" visual within the UI
		this.refreshHeader();
		setBorder(getPanelBorder(this.isActivePlayer));
		updateIssWidgets(isActivePlayer);
	}

	/**
	 * Sets the flag that indicates whether this player is an AI player or not.
	 *
	 * @param isAIPlayer TRUE, if the player is an AI player
	 */
	void setAIPlayer(final boolean isAIPlayer) {

		this.isAIPlayer = isAIPlayer;
	}

	private void updateIssWidgets(final boolean isActivePlayer) {
		if (this.showIssWidgets) {
			if (isActivePlayer) {
				this.clockPanel.setActive();
			} else {
				this.clockPanel.setInactive();
			}
		}
	}

	void setPass(final boolean isPassed) {
		this.playerPassed = isPassed;
		refreshHeader();
	}

	void setDeclarer(final boolean isDeclarer) {
		this.declarer = isDeclarer;
		refreshHeader();
	}

	public String getPlayerName() {

		return this.playerName;
	}

	public void setGeschoben() {
		this.playerGeschoben = true;
		refreshHeader();
	}

	public void setContra() {
		this.playerContra = true;
		refreshHeader();
	}

	public void setRe() {
		this.playerRe = true;
		refreshHeader();
	}
}
