/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0
 * Build date: 2011-05-23 18:13:47
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


package org.jskat.util.rule;

import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;

/**
 * Implementation of skat rules for Suit games
 */
public class SuitRules extends SuitGrandRules {

	/**
	 * @see SuitGrandRules#getBaseMultiplier(SkatGameData)
	 */
	@Override
	protected int getBaseMultiplier(SkatGameData gameData) {

		int multiplier = getJackMultiplier(gameData);
		
		if (multiplier == 4 && gameData.getClubJack()) {
			// consider all trump cards too
			CardList playerCards = gameData.getPlayerCards(gameData.getDeclarer());
			playerCards.addAll(gameData.getSkat());

			for (Card currTrumpCard : getTrumpCards(gameData.getGameType())) {
				
				if (playerCards.contains(currTrumpCard)) {
					
					multiplier++;
				}
				else {
					// trump is not continous anymore
					break;
				}
			}
		}
		
		return multiplier;
	}
	
	private CardList getTrumpCards(GameType gameType) {
		
		CardList trumpCards = new CardList();
		
		for (Rank currRank : Rank.values()) {
			
			trumpCards.add(Card.getCardFromString(gameType.getTrumpSuit().shortString().concat(currRank.shortString())));
		}
		trumpCards.sort(gameType);
		
		return trumpCards;
	}
}
