/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.SkatConstants;
import de.jskat.util.Suit;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class Bidding {

	private Log log = LogFactory.getLog(Bidding.class);

    /** default constructor
	 * @param cards hand of the player
	 */
	public Bidding(CardList cards) {
	    log.debug("Checking out what to bid with ["+cards+"]");

		Suit mostFrequentSuitColor;
		int mostFrequentSuitColorValue = 0;
		int multiplier = Helper.getMultiplier(cards);
        
		mostFrequentSuitColor = cards.getMostFrequentSuit();
		int noOfTrumps = cards.getSuitCount(mostFrequentSuitColor, true);
		int noOfJacks = Helper.countJacks(cards);
        
		if (mostFrequentSuitColor == Suit.CLUBS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(GameType.CLUBS, false, false);
		}
		else if (mostFrequentSuitColor == Suit.SPADES) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(GameType.SPADES, false, false);
		}
		else if (mostFrequentSuitColor == Suit.HEARTS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(GameType.HEARTS, false, false);
		}
		else if (mostFrequentSuitColor == Suit.DIAMONDS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(GameType.DIAMONDS, false, false);
		}
		maxBid = mostFrequentSuitColorValue * multiplier;

		// but I will only play, if I have at least 1 jack and 4 color cards or 2 jacks and 3 color cards
		if (noOfJacks < 3 && noOfTrumps < 4) maxBid = 0;
		else if (noOfJacks < 2 && noOfTrumps < 5) maxBid = 0;
		else if (noOfJacks < 1 && noOfTrumps < 6) maxBid = 0;
		else if ((Helper.getJacks(cards)&12)==0 && noOfTrumps < 5) maxBid = 0;
        
		log.debug("I will bid until " + maxBid +" - I have "+noOfJacks+" Jacks and "+noOfTrumps+" Trumps in suit "+mostFrequentSuitColor);
	}

	/** Gets the maximum bid value of the player
	 * @return value
	 */
	public int getMaxBid() {
		return maxBid;
	}

	/**
	 * Maximum value that the player will bid
	 */
	private int maxBid = -1;
}
