/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util.rule;

import de.jskat.data.SkatGameData;

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
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Checks whether a player did a durchmarsch (walkthrough) in a ramsch game<br>
	 * durchmarsch means one player made all tricks
	 * 
	 * @param playerID
	 *            Player ID of the player to be checked
	 * @param gameData
	 *            Game data
	 * @return TRUE if the player played a durchmarsch
	 */
	public final boolean isDurchmarsch(int playerID, SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Checks whether a player was jungfrau (virgin) in a ramsch game<br>
	 * jungfrau means one player made no trick<br>
	 * two players who played jungfrau means a durchmarsch for the third player
	 * 
	 * @param playerID
	 *            Player ID of the player to be checked
	 * @param gameData
	 *            Game data
	 * @return TRUE if the player was jungfrau
	 */
	public final boolean isJungfrau(int playerID, SkatGameData gameData) {
		// TODO Auto-generated method stub
		return false;
	}
}
