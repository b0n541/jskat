/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.util;

import java.util.EnumSet;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Represents a complete card deck for Skat
 */
public class CardDeck extends CardList {
    
	private static final long serialVersionUID = 1L;

	private Random rand;

	private static final int MAX_CARDS = 32;
	
	/** 
     * Creates a new instance of CardDeck
     */
    public CardDeck() {
        
    	super();
    	
        // Creates cards for every suit and value
        for (Card card : Card.values()) {

       		add(card);
        }
        
        this.rand = new Random();
    }
    
    /**
     * Constructor
     * 
     * @param cards Card distribution
     */
    public CardDeck(String cards) {
    	
    	StringTokenizer token = new StringTokenizer(cards);
    	
    	while (token.hasMoreTokens()) {
    		
    		add(Card.getCardFromString(token.nextToken()));
    	}
    }

    /**
     * @see CardList#add(Card)
     */
    @Override
	public boolean add(Card card) {
    	
    	boolean result = false;
    	
    	if (size() < this.MAX_CARDS) {
    		
    		result = super.add(card); 
    	}
    	
    	return result;
    }
    
    /**
     * Gets a complete card deck
     * 
     * @return A complete card deck
     */
    public static EnumSet<Card> getAllCards() {
    	
    	EnumSet<Card> allCards = EnumSet.allOf(Card.class);
    	
    	return allCards;
    }
    
    /** 
     * Shuffles the CardDeck 
     */
    public void shuffle() {
        
        // Simple random shuffling
        CardList shuffledCardDeck = new CardList();
        
        while (size() > 0) {
            
            shuffledCardDeck.add(remove(this.rand.nextInt(size())));
        }
        
        while (shuffledCardDeck.size() > 0) {
            
            add(shuffledCardDeck.remove(0));
        }
    }
}
