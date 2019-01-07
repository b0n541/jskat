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
package org.jskat.ai.algorithmic;

import org.jskat.ai.mjl.Helper;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.SkatConstants;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BidEvaluator is the class to generate the acceptable bid value and to
 * decide, which game to play after discarding the Skat.
 * 
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
class BidEvaluator {

	private static Logger log = LoggerFactory.getLogger(BidEvaluator.class);

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
	BidEvaluator(final CardList cards) {
		log.debug("Checking out what to bid with [" + cards + "]" + cards.dumpFlag());
		if (cards.size() < 10) {
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
		} else if ((Helper.getJacks(cards) & 3) == 0 && noOfTrumps < 5) {
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

		log.debug("I will bid until " + maxBid + " (m=" + multiplier + "x) - I have " + noOfJacks
				+ " Jacks and an additional " + noOfTrumps + " trump cards in suit " + mostFrequentSuitColor);
	}

	/**
	 * Gets the maximum bid value of the player
	 * 
	 * @return maximum bid value
	 */
	int getMaxBid() {
		return maxBid;
	}

	/**
	 * tells the AI player whether to pick up the skat or not
	 * 
	 * @return true, if the skat should be picked up;<br>
	 *         false, for a hand game
	 */
	boolean pickUpSkat() {
		return true;
	}

	GameType getSuggestedGameType() {
		return suggestedGameType;
	}

}
