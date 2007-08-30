/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share;

import jskat.share.SkatConstants;

/**
 * A carddeck for skat
 *
 * @author  Jan Schäfer <jan.schaefer@b0n541.net>
 */
public class CardDeck extends CardVector {
    
    /** Creates a new instance of CardDeck */
    public CardDeck() {
        
        // Creates cards for every suit
        for (int i = SkatConstants.CLUBS; i <= SkatConstants.DIAMONDS; i++) {
            // and every value
            for (int j = SkatConstants.SEVEN; j <= SkatConstants.JACK; j++) {
                
                add(new Card(i, j));
            }
        }
    }
    
    /** Shuffles the CardDeck */
    public void shuffle() {
        
        // XXX Implementation of some more human like shuffling methods
        // Simple random shuffling
        CardVector shuffledCardDeck = new CardVector();
        
        while (size() > 0) {
            
            shuffledCardDeck.add(remove((int)(size() * java.lang.Math.random())));
        }
        
        while (shuffledCardDeck.size() > 0) {
            
            add(shuffledCardDeck.remove(0));
        }

        // XXX after shuffling, let one player cut the card deck
    }
}
