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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.event.table.TableCreatedEvent;
import org.jskat.data.JSkatViewType;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

public class GameSimulator2 {

	private final static Logger LOG = LoggerFactory
			.getLogger(GameSimulator2.class);

	Map<GameType, List<AIPlayerNN>> aiPlayer = new HashMap<>();
	Map<GameType, List<GameSimulation>> gameSimulations = new HashMap<>();

	GameSimulator2() {
		for (GameType gameType : GameType.values()) {
			aiPlayer.put(gameType,
					Arrays.asList(new AIPlayerNN(NOPLogger.NOP_LOGGER),
							new AIPlayerNN(NOPLogger.NOP_LOGGER),
							new AIPlayerNN(NOPLogger.NOP_LOGGER)));
			gameSimulations.put(gameType, new ArrayList<GameSimulation>());
			JSkatEventBus.INSTANCE
					.post(new TableCreatedEvent(JSkatViewType.TRAINING_TABLE,
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
		gameSimulations.get(gameSimulation.gameType).add(gameSimulation);
	}

	public void simulate(List<GameType> gameTypes) {
		for (GameType gameType : gameTypes) {
			GameSimulation gameSimulation = getNextSimulation(
					gameSimulations.get(gameType));
			List<AIPlayerNN> player = aiPlayer.get(gameType);
			gameSimulation.simulateGame(getTrainingTableName(gameType),
					player.get(0), player.get(1), player.get(2));
		}
	}

	private GameSimulation getNextSimulation(
			List<GameSimulation> possibleGameSimulations) {
		GameSimulation result = null;
		double maxWonRate = 0.0;

		for (GameSimulation currSim : possibleGameSimulations) {
			if (currSim.getSimulatedGames() == 0L) {
				// simulation has never been run
				return currSim;
			}
			if (currSim.getWonRate() > maxWonRate) {
				// prefer higher won rates
				maxWonRate = currSim.getWonRate();
				result = currSim;
			}
		}

		return result;
	}
}
