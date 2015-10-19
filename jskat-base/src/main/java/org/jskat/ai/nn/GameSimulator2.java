/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.CreateTableCommand;
import org.jskat.data.JSkatViewType;
import org.jskat.util.GameType;

class GameSimulator2 {

	private final Map<GameType, List<GameSimulation>> gameSimulations = new HashMap<>();
	private final Long maxSimulations;
	private final Long maxTimeInMilliseconds;

	GameSimulator2(Long maxSimulations, Long maxTimeInMilliseconds) {

		this.maxSimulations = maxSimulations != null ? maxSimulations
				: Long.MAX_VALUE;
		this.maxTimeInMilliseconds = maxTimeInMilliseconds != null
				? maxTimeInMilliseconds : Long.MAX_VALUE;

		for (GameType gameType : GameType.values()) {
			gameSimulations.put(gameType, new ArrayList<GameSimulation>());
			JSkatEventBus.INSTANCE
					.post(new CreateTableCommand(JSkatViewType.TRAINING_TABLE,
							getTrainingTableName(gameType)));
		}
	}

	private static String getTrainingTableName(GameType gameType) {
		return "SIM" + gameType;
	}

	void reset() {
		for (GameType gameType : GameType.values()) {
			gameSimulations.get(gameType).clear();
		}
	}

	void add(GameSimulation gameSimulation) {
		gameSimulations.get(gameSimulation.getGameType()).add(gameSimulation);
	}

	GameSimulation simulate(List<GameType> possibleGameTypes) {
		long simulationCount = 0L;
		long startTimeInMilliseconds = System.currentTimeMillis();
		while (simulationCount < maxSimulations && System.currentTimeMillis()
				- startTimeInMilliseconds < maxTimeInMilliseconds) {
			GameSimulation simulation = getNextSimulation(possibleGameTypes);
			simulation.simulateGame(
					getTrainingTableName(simulation.getGameType()));
			simulationCount++;
		}

		return getBestGameSimulation();
	}

	private GameSimulation getBestGameSimulation() {
		double maxWonRate = Double.NEGATIVE_INFINITY;
		GameSimulation bestSimulation = null;
		for (GameType gameType : GameType.values()) {
			for (GameSimulation simulation : gameSimulations.get(gameType)) {
				if (simulation.getWonRate() > maxWonRate) {
					maxWonRate = simulation.getWonRate();
					bestSimulation = simulation;
				}
			}
		}
		return bestSimulation;
	}

	private GameSimulation getNextSimulation(List<GameType> possibleGameTypes) {
		List<GameSimulation> bestSimulations = new ArrayList<>();
		double maxWonRate = Double.NEGATIVE_INFINITY;
		for (GameType gameType : possibleGameTypes) {
			for (GameSimulation simulation : gameSimulations.get(gameType)) {
				if (simulation.getSimulatedGames() == 0L) {
					// simulation has never been run --> return immediately
					return simulation;
				}
				if (simulation.getWonRate() >= maxWonRate) {
					if (simulation.getWonRate() > maxWonRate) {
						// prefer higher won rates
						maxWonRate = simulation.getWonRate();
						bestSimulations.clear();
					}
					bestSimulations.add(simulation);
				}
			}
		}

		Long minSimulationCount = Long.MAX_VALUE;
		GameSimulation result = null;
		for (GameSimulation simulation : bestSimulations) {
			if (simulation.getSimulatedGames() < minSimulationCount) {
				minSimulationCount = simulation.getSimulatedGames();
				result = simulation;
			}
		}
		return result;
	}
}
