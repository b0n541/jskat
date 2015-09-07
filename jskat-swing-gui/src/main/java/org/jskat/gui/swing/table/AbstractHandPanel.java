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
		this.bitmaps = JSkatGraphicRepository.INSTANCE;
		this.strings = JSkatResourceBundle.INSTANCE;
        this.maxCardCount = maxCards;
        this.showIssWidgets = showIssWidgets;

        setOpaque(false);

        this.headerLabel = new JLabel(" "); //$NON-NLS-1$
        this.iconPanel = new IconPanel();
        this.clockPanel = new ClockPanel();

        initPanel(showIssWidgets);
    }

    /**
     * Initializes the panel
     */
    void initPanel(boolean isIss) {

        setLayout(LayoutFactory.getMigLayout(
                "fill, insets 0", "fill", "[shrink][grow]")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        setBorder(getPanelBorder());

        String headerInsets = "insets ";
        if (isIss) {
            headerInsets = headerInsets + "0 5 0 0";
        } else {
            headerInsets = headerInsets + "5";
        }

        this.header = new JPanel(LayoutFactory.getMigLayout(
                "fill, " + headerInsets, "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        this.header.add(this.headerLabel);
        // blank panel
        this.header.add(new JPanel());
        if (this.showIssWidgets) {
            this.header.add(this.iconPanel);
            this.header.add(this.clockPanel);
        }
        add(this.header, "shrinky, wrap"); //$NON-NLS-1$

        this.cardPanel = new ClickableCardPanel(this, 1.0, true);
        add(this.cardPanel, "growy"); //$NON-NLS-1$

        if (JSkatOptions.instance().isCheatDebugMode().booleanValue()) {
            showCards();
        }
    }

    private Border getPanelBorder() {

        Border result = null;

        if (this.activePlayer) {
            result = BorderFactory.createLineBorder(Color.yellow, 3);
        } else {
            result = BorderFactory.createLineBorder(Color.black, 3);
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

        this.position = newPosition;
        refreshHeaderText();
    }

    void setBidValue(final int newBidValue) {

        this.bidValue = newBidValue;
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
				
				if (passedGeschobenContraRe.length() > 0
						&& (playerContra || playerRe)) {
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

        this.headerLabel.setText(headerText.toString());
    }

    /**
     * Adds a card to the panel
     *
     * @param newCard
     *            Card
     */
    void addCard(final Card newCard) {

        this.cardPanel.addCard(newCard);
    }

    /**
     * Adds a Collection of cards to the panel
     *
     * @param newCards
     *            card collection
     */
    void addCards(final CardList newCards) {

        this.cardPanel.addCards(newCards);
    }

    /**
     * Removes a card from the panel
     *
     * @param cardToRemove
     *            Card
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
        refreshHeaderText();
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

        refreshHeaderText();
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
        return this.activePlayer;
    }

    void setActivePlayer(final boolean isActivePlayer) {
        this.activePlayer = isActivePlayer;
        setBorder(getPanelBorder());

        updateIssWidgets(isActivePlayer);
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
        refreshHeaderText();
    }

    void setDeclarer(final boolean isDeclarer) {
        this.declarer = isDeclarer;
        refreshHeaderText();
    }

    public String getPlayerName() {

        return this.playerName;
    }

    public void setGeschoben() {
        this.playerGeschoben = true;
        refreshHeaderText();
    }

    public void setContra() {
        this.playerContra = true;
        refreshHeaderText();
    }

    public void setRe() {
        this.playerRe = true;
        refreshHeaderText();
    }
}
