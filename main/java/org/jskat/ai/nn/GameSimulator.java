package org.jskat.ai.nn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

/**
 * Simulates games for {@link AIPlayerNN}
 */
class GameSimulator {

	Map<GameType, GameSimulationThread> simThreads;

	GameSimulator() {
		simThreads = new HashMap<GameType, GameSimulationThread>();
	}

	void resetGameSimulator(List<GameType> gameTypes, Player playerPosition,
			CardList playerCards) {
		
		simThreads.clear();
		for (GameType gameType : gameTypes) {
			simThreads.put(gameType, new GameSimulationThread(gameType, playerPosition,
				playerCards));
		}
	}

	SimulationResults simulateMaxEpisodes(Long maxEpisodes) {

		SimulationResults results = new SimulationResults();

		for (GameSimulationThread thread : simThreads.values()) {
			thread.startSimulationWithMaxEpidodes(maxEpisodes);
		}
		for (GameSimulationThread thread : simThreads.values()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (GameSimulationThread thread : simThreads.values()) {
			results.setWonRate(thread.getGameType(), thread.getWonRate());
		}

		return results;
	}
}
