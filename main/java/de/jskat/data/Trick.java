/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.data;

import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.Player;

/**
 * Holds data for a single trick
 */
public class Trick {
    
    private Player foreHand;
    private Player trickWinner;
    
    private Card firstCard;
    private Card secondCard;
    private Card thirdCard;

    /** 
     * Creates a new instance of Trick 
     *
     * @param newForeHand Player ID of the fore hand player
     */
    public Trick(Player newForeHand) {
        
        this.foreHand = newForeHand;
    }
    
    /**
     * Gets the fore hand player
     * 
     * @return Fore hand player
     */
    public Player getForeHand() {
        
        return this.foreHand;
    }
    
    /**
     * Gets the first card
     * 
     * @return First card
     */
    public Card getFirstCard() {
    	
        return this.firstCard;
    }
    
    /**
     * Sets the first card
     * 
     * @param newFirstCard First card
     */
    public void setFirstCard(Card newFirstCard) {
    	
        this.firstCard = newFirstCard;
    }
    
    /**
     * Gets the second card
     * 
     * @return Second card
     */
    public Card getSecondCard() {
    	
        return this.secondCard;
    }
    
    /**
     * Sets the second card
     * 
     * @param newSecondCard Second card
     */
    public void setSecondCard(Card newSecondCard) {
    	
        this.secondCard = newSecondCard;
    }
    
    /**
     * Gets the third card
     * 
     * @return Third card
     */
    public Card getThirdCard() {
    	
        return this.thirdCard;
    }
    
    /**
     * Sets the third card
     * 
     * @param newThirdCard Third card
     */
    public void setThirdCard(Card newThirdCard) {
    	
        this.thirdCard = newThirdCard;
    }
    
    /**
     * Gets a card from the trick
     * 
     * @param player Player
     * @return Card played by the player
     */
    public Card getCard(Player player) {
    	
    	Card returnCard = null;
    	
    	switch(player) {
    	
    	case FORE_HAND:
    			returnCard = getFirstCard();
    		break;
    	case MIDDLE_HAND:
    			returnCard = getSecondCard();
    		break;
    	case HIND_HAND:
    			returnCard = getThirdCard();
    		break;
    	}
    	
    	return returnCard;
    }
    
    /**
     * Gets the trick winner
     * 
     * @return Trick winner
     */
    public Player getTrickWinner() {
    	
        return this.trickWinner;
    }
    
    /**
     * Sets the trick winner
     * 
     * @param newTrickWinner Trick winner
     */
    public void setTrickWinner(Player newTrickWinner) {
    	
        this.trickWinner = newTrickWinner;
    }
    
    /**
     * Adds a card to the trick
     * 
     * @param newCard Card to be added
     */
	public void addCard(Card newCard) {

		if (this.firstCard == null) {
			
			this.firstCard = newCard;
		}
		else if (this.secondCard == null) {
			
			this.secondCard = newCard;
		}
		else if (this.thirdCard == null) {
			
			this.thirdCard = newCard;
		}
	}

	/**
     * Returns the cards of the trick as CardList 
     * 
     * @return The cards of the trick
     */
    public CardList getCardList() {
    	
    	CardList returnList = new CardList();
    	
    	if (this.firstCard != null) {
    		
    		returnList.add(this.firstCard);
    	}
    	if (this.secondCard != null) {
    		
    		returnList.add(this.secondCard);
    	}
    	if (this.thirdCard != null) {
    		
    		returnList.add(this.thirdCard);
    	}
    	
    	return returnList;
    }
    
    /**
	 * Gets the sum of all card points in the CardList
	 * 
	 * @return Sum of all card points
     */
    public int getCardValueSum() {
    	
    	return this.getCardList().getCardValueSum();
    }
    
	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		Trick clone = new Trick(this.foreHand);
		
		clone.addCard(this.firstCard);
		clone.addCard(this.secondCard);
		clone.addCard(this.thirdCard);
		
		clone.setTrickWinner(this.trickWinner);
		
		return clone;
	}
}
