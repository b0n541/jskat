/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share;

import jskat.share.SkatConstants;

/**
 * Represents a complete card deck for Skat
 */
public class CardDeck extends CardVector {
    
    /** 
     * Creates a new instance of CardDeck
     */
    public CardDeck() {
        
        // Creates cards for every suit
        for (SkatConstants.Suits currSuit : SkatConstants.Suits.values()) {
            // and every value
            for (SkatConstants.Ranks currRank : SkatConstants.Ranks.values()) {
                
                add(new Card(currSuit, currRank));
            }
        }
    }
    
    /** 
     * Shuffles the CardDeck 
     */
    public void shuffle() {
        
        // Simple random shuffling
        CardVector shuffledCardDeck = new CardVector();
        
        while (size() > 0) {
            
            shuffledCardDeck.add(remove((int)(size() * java.lang.Math.random())));
        }
        
        while (shuffledCardDeck.size() > 0) {
            
            add(shuffledCardDeck.remove(0));
        }
    }
}
