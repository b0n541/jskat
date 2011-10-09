/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0
 * Build date: 2011-10-09
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
