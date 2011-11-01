/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-09
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.ai.algorithmic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.mjl.Helper;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.SkatConstants;
import org.jskat.util.Suit;


/**
 * The BidEvaluator is the class to generate the acceptable bid value and to decide, which game to play after
 * discarding the Skat.
 *  
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
class BidEvaluator {

	private Log log = LogFactory.getLog(BidEvaluator.class);

	/**
	 * Maximum value that the player will bid
	 */
	private int maxBid = -1;
	private GameType suggestedGameType = null;

    /** default constructor
	 * @param cards hand of the player
	 */
	BidEvaluator(CardList cards) {
	    log.debug("Checking out what to bid with ["+cards+"]"+cards.dumpFlag());
	    if(cards.size()<10) {
	    	log.warn("Not enough cards for bid evaluation!");
	    	return;
	    }

		Suit mostFrequentSuitColor;
		int mostFrequentSuitColorValue = 0;
		int multiplier = Helper.getMultiplier(cards);
        
		mostFrequentSuitColor = cards.getMostFrequentSuit();
		int noOfTrumps = cards.getSuitCount(mostFrequentSuitColor, false);
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
		else if ((Helper.getJacks(cards)&3)==0 && noOfTrumps < 5) maxBid = 0;
        
		if (maxBid > 0) {
			switch (mostFrequentSuitColor) {
			case CLUBS:
				suggestedGameType = GameType.CLUBS;
				break;
			case SPADES:
				suggestedGameType = GameType.SPADES;
				break;
			case HEARTS:
				suggestedGameType = GameType.HEARTS;
				break;
			case DIAMONDS:
				suggestedGameType = GameType.DIAMONDS;
				break;
			}
		}
		
		
		// FIXME (markus, 01.11.11) remove - only added for ramsch testing
		maxBid = 0;
		
		log.debug("I will bid until " + maxBid +" (m="+multiplier+"x) - I have "+noOfJacks+" Jacks and an additional "+noOfTrumps+" trump cards in suit "+mostFrequentSuitColor);
	}

	/** Gets the maximum bid value of the player
	 * @return maximum bid value
	 */
	int getMaxBid() {
		return maxBid;
	}

	/** tells the AI player whether to pick up the skat or not
	 * @return true, if the skat should be picked up;<br>false, for a hand game
	 */
	boolean pickUpSkat() {
		return true;
	}

	GameType getSuggestedGameType() {
		return suggestedGameType;
	}
	
}
