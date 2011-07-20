/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

/**
 * Implementation of skat rules for Suit games
 */
public class SuitRules extends SuitGrandRules {

	/* (non-Javadoc)
	 * @see org.jskat.util.rule.BasicSkatRules#getMultiplier(org.jskat.util.CardList, org.jskat.util.GameType)
	 */
	@Override
	public int getMultiplier(CardList cards, GameType gameType) {
		if(gameType==GameType.GRAND || gameType==GameType.RAMSCH || gameType==GameType.NULL) throw new IllegalArgumentException("Wrong ruleset - "+gameType);
		int result = 1;
		if(cards.contains(Card.CJ)) {
			result++;
			if(cards.contains(Card.SJ)) {
				result++;
				if(cards.contains(Card.HJ)) {
					result++;
					if(cards.contains(Card.DJ)) {
						result++;
						for(Rank r: Rank.getRankList()) {
							if(cards.contains(Card.getCard(Suit.valueOf(gameType.toString()), r))) {
								result++;
							}
							else {
								break;
							}
						}
					}
				}
			}
		}
		else {
			result++;
			if(!cards.contains(Card.SJ)) {
				result++;
				if(!cards.contains(Card.HJ)) {
					result++;
					if(!cards.contains(Card.DJ)) {
						result++;
						for(Rank r: Rank.getRankList()) {
							if(!cards.contains(Card.getCard(Suit.valueOf(gameType.toString()), r))) {
								result++;
							}
							else {
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}
}
