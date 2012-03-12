package org.jskat.ai.nn;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jskat.util.GameType;

/**
 * Holds the results of all game simulations
 */
public class SimulationResults {

	private Map<GameType, Double> wonRates = new HashMap<GameType, Double>();

	Double getWonRate(GameType gameType) {

		return wonRates.get(gameType);
	}

	void setWonRate(GameType gameType, Double wonRate) {

		wonRates.put(gameType, wonRate);
	}

	Collection<Double> getAllWonRates() {
		return wonRates.values();
	}
}
