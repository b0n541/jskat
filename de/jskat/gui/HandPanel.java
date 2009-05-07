/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.CardList;
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
	 * Players hand
	 */
	CardList cards;
	/**
	 * Card panels
	 */
	List<CardPanel> panels;
	/**
	 * Header label
	 */
	JLabel headerLabel;

	/**
	 * Maximum card count
	 */
	int maxCardCount = 0;

	/**
	 * Holds the game type for the sorting order
	 */
	GameType gameType = GameType.GRAND;

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
		this.cards = new CardList();
		this.panels = new ArrayList<CardPanel>();
		
		this.setOpaque(false);
		
		for (int i = 0; i < this.maxCardCount; i++) {
			
			this.panels.add(new CardPanel(this, this.bitmaps, true));
		}
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
		
		this.cards.add(newCard);
		this.cards.sort(this.gameType);
		refreshCardPanels();
	}
	
	/**
	 * Removes a card from the panel
	 * 
	 * @param cardToRemove Card
	 */
	void removeCard(Card cardToRemove) {
		
		this.cards.remove(cardToRemove);
		refreshCardPanels();
	}
	
	private void refreshCardPanels() {
		
		int i = 0;
		for (Card card : this.cards) {
			
			// FIXME dirty hack
			if (i < 10) {
				
				this.panels.get(i).setCard(card.getSuit(), card.getRank());
			}
			
			i++;
		}
		
		while (i < this.panels.size()) {
			// delete unused panels
			this.panels.get(i).clear();
			i++;
		}
	}
	
	/**
	 * Removes all cards from the panel
	 */
	void clearHandPanel() {
		
		this.cards.clear();
		refreshCardPanels();
	}
	
	/**
	 * Hides all cards on the panel
	 */
	void hideCards() {
		
		for (CardPanel card : this.panels) {
			
			card.hideCard();
		}
	}
	
	/**
	 * Shows all cards on the panel
	 */
	void showCards() {
		
		for (CardPanel card : this.panels) {
			
			card.showCard();
		}
	}
	
	boolean isHandFull() {
		
		return this.cards.size() >= this.maxCardCount;
	}
	
	void setSortGameType(GameType newGameType) {
		
		this.gameType = newGameType;
		this.cards.sort(this.gameType);
		refreshCardPanels();
	}
}
