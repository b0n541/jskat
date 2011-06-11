/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
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

package org.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.SkatConstants;
import org.jskat.util.Suit;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
class Bidding {

	private Log log = LogFactory.getLog(Bidding.class);

	/**
	 * Maximum value that the player will bid
	 */
	private int maxBid = -1;
	private GameType suggestedGameType = null;

	/**
	 * default constructor
	 * 
	 * @param cards
	 *            hand of the player
	 */
	Bidding(CardList cards) {
		log.debug("Checking out what to bid with [" + cards + "]");

		Suit mostFrequentSuitColor;
		int mostFrequentSuitColorValue = 0;
		int multiplier = Helper.getMultiplier(cards);

		mostFrequentSuitColor = cards.getMostFrequentSuit();
		int noOfTrumps = cards.getSuitCount(mostFrequentSuitColor, true);
		int noOfJacks = Helper.countJacks(cards);

		if (mostFrequentSuitColor == Suit.CLUBS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(
					GameType.CLUBS, false, false);
		} else if (mostFrequentSuitColor == Suit.SPADES) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(
					GameType.SPADES, false, false);
		} else if (mostFrequentSuitColor == Suit.HEARTS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(
					GameType.HEARTS, false, false);
		} else if (mostFrequentSuitColor == Suit.DIAMONDS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(
					GameType.DIAMONDS, false, false);
		}
		maxBid = mostFrequentSuitColorValue * multiplier;

		// but I will only play, if I have at least 1 jack and 4 color cards or
		// 2 jacks and 3 color cards
		if (noOfJacks < 3 && noOfTrumps < 4)
			maxBid = 0;
		else if (noOfJacks < 2 && noOfTrumps < 5)
			maxBid = 0;
		else if (noOfJacks < 1 && noOfTrumps < 6)
			maxBid = 0;
		else if ((Helper.getJacks(cards) & 12) == 0 && noOfTrumps < 5)
			maxBid = 0;

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

		log.debug("I will bid until " + maxBid + " - I have " + noOfJacks
				+ " Jacks and " + noOfTrumps + " Trumps in suit "
				+ mostFrequentSuitColor);
	}

	/**
	 * Gets the maximum bid value of the player
	 * 
	 * @return maximum bid value
	 */
	int getMaxBid() {
		return maxBid;
	}

	GameType getSuggestedGameType() {
		return suggestedGameType;
	}

}
