/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
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

import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

/**
 * Implementation of skat rules for Ramsch games
 */
public class RamschRules extends SuitGrandRamschRules {

	/**
	 * @see BasicSkatRules#calcGameResult(SkatGameData)
	 */
	public int calcGameResult(SkatGameData gameData) {

		int multiplier = 1;

		// TODO two player can be jungfrau
		if (gameData.isJungfrau()) {
			multiplier = multiplier * 2;
		}

		multiplier = multiplier
				* (new Double(Math.pow(2, gameData.getGeschoben()))).intValue();

		if (gameData.isGameLost()) {
			multiplier = multiplier * -1;
		}

		// FIXME Ramsch games have no single player
		return gameData.getScore(gameData.getDeclarer()) * multiplier;
	}

	/**
	 * @see BasicSkatRules#calcGameWon(SkatGameData)
	 */
	public boolean calcGameWon(SkatGameData gameData) {
		throw new IllegalStateException("ramsch game cannot be won");
	}
	
	/**
	 * Checks whether a player did a durchmarsch (walkthrough) in a ramsch game<br>
	 * durchmarsch means one player made all tricks
	 * 
	 * @param player
	 *            player to be checked
	 * @param gameData
	 *            Game data
	 * @return TRUE if the player played a durchmarsch
	 */
	public final boolean isDurchmarsch(Player player, SkatGameData gameData) {
		for(Trick t: gameData.getTricks()) {
			if(t.getTrickWinner()!=player) return false;
		}
		return true;
	}

	/**
	 * Checks whether a player was jungfrau (virgin) in a ramsch game<br>
	 * jungfrau means one player made no trick<br>
	 * two players who played jungfrau means a durchmarsch for the third player
	 * 
	 * @param player
	 *            player to be checked
	 * @param gameData
	 *            Game data
	 * @return TRUE if the player was jungfrau
	 */
	public final boolean isJungfrau(Player player, SkatGameData gameData) {
		for(Trick t: gameData.getTricks()) {
			if(t.getTrickWinner()==player) return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.jskat.util.rule.BasicSkatRules#getMultiplier(org.jskat.util.CardList, org.jskat.util.GameType)
	 */
	@Override
	public int getMultiplier(CardList cards, GameType gameType) {
		if(gameType==GameType.RAMSCH) return 0;
		if(gameType!=GameType.GRAND) throw new IllegalArgumentException("Wrong ruleset - "+gameType);
		int result = 1;
		if(cards.contains(Card.CJ)) {
			result++;
			if(cards.contains(Card.SJ)) {
				result++;
				if(cards.contains(Card.HJ)) {
					result++;
					if(cards.contains(Card.DJ)) {
						result++;
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
					}
				}
			}
		}
		return result;
	}
}
