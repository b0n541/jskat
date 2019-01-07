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
package org.jskat.util.rule;

import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatTableOptions.RamschSkatOwner;
import org.jskat.data.Trick;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of skat rules for Ramsch games
 */
public class RamschRule extends SuitGrandRamschRule {

	private static final Logger log = LoggerFactory.getLogger(RamschRule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getGameValueForWonGame(final SkatGameData gameData) {
		int highestPlayerPoints = getGetHighestPlayerPoints(gameData);

		return highestPlayerPoints * getMultiplier(gameData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int calcGameResult(final SkatGameData gameData) {

		return getGameValueForWonGame(gameData);
	}

	private static int getGetHighestPlayerPoints(final SkatGameData gameData) {

		int result = 0;

		int foreHandPoints = gameData.getPlayerPoints(Player.FOREHAND);
		int middleHandPoints = gameData.getPlayerPoints(Player.MIDDLEHAND);
		int rearHandPoints = gameData.getPlayerPoints(Player.REARHAND);
		int skatPoints = JSkatOptions.instance().getRamschSkatOwner() == RamschSkatOwner.LOSER ? gameData
				.getSkat().getTotalValue() : 0;

		// FIXME (jan 18.11.2011) make this simpler
		// FIXME (markus 22.02.2012) consider skat points - seems to be missing
		if (foreHandPoints > middleHandPoints
				&& foreHandPoints > rearHandPoints) {
			result = (foreHandPoints + skatPoints) * -1;
		} else if (middleHandPoints > foreHandPoints
				&& middleHandPoints > rearHandPoints) {
			result = (middleHandPoints + skatPoints) * -1;
		} else if (rearHandPoints > foreHandPoints
				&& rearHandPoints > middleHandPoints) {
			result = (rearHandPoints + skatPoints) * -1;
		} else if (middleHandPoints > foreHandPoints
				&& middleHandPoints == rearHandPoints) {
			result = middleHandPoints * -1;
		} else if (foreHandPoints > middleHandPoints
				&& foreHandPoints == rearHandPoints) {
			result = foreHandPoints * -1;
		} else if (foreHandPoints > rearHandPoints
				&& foreHandPoints == middleHandPoints) {
			result = foreHandPoints * -1;
		} else {
			// all player have 40 points
			result = -40;
		}

		return result;
	}

	/**
	 * @see SkatRule#isGameWon(SkatGameData)
	 */
	@Override
	public boolean isGameWon(final SkatGameData gameData) {
		return false;
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
	public static final boolean isDurchmarsch(final Player player,
			final SkatGameData gameData) {
		if (gameData.getTricks().size() == 10) {
			for (Trick t : gameData.getTricks()) {
				if (t.getTrickWinner() != player) {
					return false;
				}
			}
			return true;
		}
		return false;
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
	public static final boolean isJungfrau(final Player player,
			final SkatGameData gameData) {
		if (gameData.getTricks().size() == 10) {
			for (Trick t : gameData.getTricks()) {
				if (t.getTrickWinner() == player) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMultiplier(final SkatGameData gameData) {

		int multiplier = 1;

		if (gameData.isJungfrau()) {
			log.debug("One player is jungfrau"); //$NON-NLS-1$
			multiplier = 2;
		}

		log.debug(gameData.getGeschoben() + " player did schieben"); //$NON-NLS-1$
		multiplier = (int) (multiplier * Math.pow(2, gameData.getGeschoben()));

		return multiplier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPlayWithJacks(final SkatGameData gameData) {
		return false;
	}
}
