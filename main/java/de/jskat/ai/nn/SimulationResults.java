/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

 */

package de.jskat.ai.nn;

import java.util.HashMap;
import java.util.Map;

import de.jskat.util.GameType;

/**
 * Holds the results of all game simulations
 */
public class SimulationResults {

	private Map<GameType, Integer> wonGames = new HashMap<GameType, Integer>();

	public Integer getWonGames(GameType gameType) {

		return wonGames.get(gameType);
	}

	public void setWonGames(GameType gameType, Integer numberOfWonGames) {

		wonGames.put(gameType, numberOfWonGames);
	}
}
