/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Abstract class for a panel representing a players hand
 */
abstract class HandPanel extends JPanel {

	private static final long serialVersionUID = 1L;

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
	 * Header label
	 */
	JLabel headerLabel;

	CardPanel cardPanel;
	
	/**
	 * Maximum card count
	 */
	int maxCardCount = 0;

	/**
	 * Constructor
	 * 
	 * @param newParent Skat table panel
	 * @param jskatBitmaps Card images
	 */
	HandPanel(SkatTablePanel newParent, JSkatGraphicRepository jskatBitmaps, int maxCards) {
		
		this.parent = newParent;
		setActionMap(this.parent.getActionMap());
		this.bitmaps = jskatBitmaps;
		this.maxCardCount = maxCards;
		
		this.setOpaque(false);

		this.headerLabel = new JLabel(" "); //$NON-NLS-1$
		
		initPanel();
	}

	/**
	 * Initializes the panel
	 */
	abstract void initPanel();

	/**
	 * Sets the player position
	 * 
	 * @param newPosition Position
	 */
	void setPosition(Player newPosition) {
		
		this.position = newPosition;
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
		
		switch(this.position) {
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
		
		this.headerLabel.setText(headerText.toString());
	}
	
	/**
	 * Adds a card to the panel
	 * 
	 * @param newCard Card
	 */
	void addCard(Card newCard) {
		
		this.cardPanel.addCard(newCard);
	}
	
	/**
	 * Removes a card from the panel
	 * 
	 * @param cardToRemove Card
	 */
	void removeCard(Card cardToRemove) {
		
		this.cardPanel.removeCard(cardToRemove);
	}
	
	/**
	 * Removes all cards from the panel
	 */
	void clearHandPanel() {
		
		this.cardPanel.clearCards();
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
}
