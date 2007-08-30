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
    
    /** Creates a new instance of Trick */
    public Trick(int foreHand) {
        
        this.foreHand = foreHand;
    }
    
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
    	
    	Card returnCard = new Card(-1, -1);
    	
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
    	
    	returnVector.add(firstCard);
    	returnVector.add(secondCard);
    	returnVector.add(thirdCard);
    	
    	return returnVector;
    }
    
    private int foreHand;
    private int trickWinner;
    
    private Card firstCard = new Card(-1, -1);
    private Card secondCard = new Card(-1, -1);;
    private Card thirdCard = new Card(-1, -1);;
}
