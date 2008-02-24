/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import jskat.share.Card;
import jskat.share.CardVector;

/**
 * Holds data for a single trick
 *
 * @author  Jan Schäfer
 */
public class Trick {
    
    /** 
     * Creates a new instance of Trick 
     *
     * @param foreHand Player ID of the fore hand player
     */
    public Trick(int foreHand) {
        
        this.foreHand = foreHand;
    }
    
    /**
     * Gets the fore hand player
     * 
     * @return Player ID of the fore hand player
     */
    public int getForeHand() {
        
        return foreHand;
    }
    
    /**
     * Getter for property firstCard.
     * @return Value of property firstCard.
     */
    public Card getFirstCard() {
        return firstCard;
    }
    
    /**
     * Setter for property firstCard.
     * @param firstCard New value of property firstCard.
     */
    public void setFirstCard(Card firstCard) {
        this.firstCard = firstCard;
    }
    
    /**
     * Getter for property secondCard.
     * @return Value of property secondCard.
     */
    public Card getSecondCard() {
        return secondCard;
    }
    
    /**
     * Setter for property secondCard.
     * @param secondCard New value of property secondCard.
     */
    public void setSecondCard(Card secondCard) {
        this.secondCard = secondCard;
    }
    
    /**
     * Getter for property thirdCard.
     * @return Value of property thirdCard.
     */
    public Card getThirdCard() {
        return thirdCard;
    }
    
    /**
     * Gets a card from the trick
     * 
     * @param player The player
     * @return The card played by the player
     */
    public Card getCard(int player) {
    	
    	Card returnCard = null;
    	
    	switch(player) {
    	
    	case 0:
    			returnCard = getFirstCard();
    		break;
    	case 1:
    			returnCard = getSecondCard();
    		break;
    	case 2:
    			returnCard = getThirdCard();
    		break;
    	}
    	
    	return returnCard;
    }
    
    /**
     * Setter for property thirdCard.
     * @param thirdCard New value of property thirdCard.
     */
    public void setThirdCard(Card thirdCard) {
        this.thirdCard = thirdCard;
    }
    
    /**
     * Getter for property trickWinner.
     * @return Value of property trickWinner.
     */
    public int getTrickWinner() {
        return trickWinner;
    }
    
    /**
     * Setter for property trickWinner.
     * @param trickWinner New value of property trickWinner.
     */
    public void setTrickWinner(int trickWinner) {
        this.trickWinner = trickWinner;
    }
    
    /**
     * Returns the cards of the trick as CardVector 
     * 
     * @return The cards of the trick
     */
    public CardVector getCardVector() {
    	
    	CardVector returnVector = new CardVector();
    	
    	if (firstCard != null) {
    		returnVector.add(firstCard);
    	}
    	if (secondCard != null) {
    		returnVector.add(secondCard);
    	}
    	if (thirdCard != null) {
    		returnVector.add(thirdCard);
    	}
    	
    	return returnVector;
    }
    
    private int foreHand;
    private int trickWinner;
    
    private Card firstCard;
    private Card secondCard;
    private Card thirdCard;
}
