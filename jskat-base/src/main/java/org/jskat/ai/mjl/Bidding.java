/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.mjl;

import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.SkatConstants;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
class Bidding {

	private static Logger log = LoggerFactory.getLogger(Bidding.class);

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
	Bidding(final CardList cards) {
		log.debug("Checking out what to bid with [" + cards + "]");

		Suit mostFrequentSuitColor;
		int mostFrequentSuitColorValue = 0;
		int multiplier = Helper.getMultiplier(cards);

		mostFrequentSuitColor = cards.getMostFrequentSuit();
		int noOfTrumps = cards.getSuitCount(mostFrequentSuitColor, true);
		int noOfJacks = Helper.countJacks(cards);

		if (mostFrequentSuitColor == Suit.CLUBS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(GameType.CLUBS, false, false);
		} else if (mostFrequentSuitColor == Suit.SPADES) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(GameType.SPADES, false, false);
		} else if (mostFrequentSuitColor == Suit.HEARTS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(GameType.HEARTS, false, false);
		} else if (mostFrequentSuitColor == Suit.DIAMONDS) {
			mostFrequentSuitColorValue = SkatConstants.getGameBaseValue(GameType.DIAMONDS, false, false);
		}
		maxBid = mostFrequentSuitColorValue * multiplier;

		// but I will only play, if I have at least 1 jack and 4 color cards or
		// 2 jacks and 3 color cards
		if (noOfJacks < 3 && noOfTrumps < 4) {
			maxBid = 0;
		} else if (noOfJacks < 2 && noOfTrumps < 5) {
			maxBid = 0;
		} else if (noOfJacks < 1 && noOfTrumps < 6) {
			maxBid = 0;
		} else if ((Helper.getJacks(cards) & 12) == 0 && noOfTrumps < 5) {
			maxBid = 0;
		}

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

		log.debug("I will bid until " + maxBid + " - I have " + noOfJacks + " Jacks and " + noOfTrumps
				+ " Trumps in suit " + mostFrequentSuitColor);
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
