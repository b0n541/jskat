/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.jskat.util.Card;
import de.jskat.util.CardDeck;

/**
 * Simulates possible card decks according to the player knowledge
 */
public class CardDeckSimulator {

	private static Random rand = new Random();

    /**
     * Simulates all unknown cards
     */
    public static void simulateUnknownCards(CardDeck knownCards) {
    	
    	EnumSet<Card> unknownCards = CardDeck.getAllCards();
    	
    	for (Card card : knownCards) {
    		
    		if (card != null) {
    			// remove all known cards first
    			unknownCards.remove(card);
    		}
    	}

    	List<Object> cardList = new LinkedList<Object>(Arrays.asList(unknownCards.toArray()));
    	for (int i = 0; i < knownCards.size(); i++) {
    		
    		if (knownCards.get(i) == null) {
    			// set a random card
    			knownCards.set(i, (Card) cardList.remove(rand.nextInt(cardList.size())));
    		}
    	}
    }
}
