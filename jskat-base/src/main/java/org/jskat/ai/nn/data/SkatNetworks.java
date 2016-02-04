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
package org.jskat.ai.nn.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jskat.ai.nn.input.GenericNetworkInputGenerator;
import org.jskat.ai.nn.util.EncogNetworkWrapper;
import org.jskat.ai.nn.util.INeuralNetwork;
import org.jskat.ai.nn.util.NetworkTopology;
import org.jskat.util.GameType;

/**
 * Holds all neural networks for the NN player.
 */
public final class SkatNetworks {

	private static int INPUT_NEURONS = GenericNetworkInputGenerator.getNeuronCountForAllStrategies();
	private static int HIDDEN_NEURONS = 317;
	// private static int HIDDEN_NEURONS2 = 234;
	// private static int HIDDEN_NEURONS3 = 23;
	private static int OUTPUT_NEURONS = 1;

	private static final boolean USE_BIAS = true;

	private final static SkatNetworks INSTANCE = new SkatNetworks();

	private static Map<GameType, Map<PlayerParty, List<INeuralNetwork>>> networks;

	/**
	 * Private constructor for singleton class.
	 */
	private SkatNetworks() {

		createNetworks();
		// loadNetworks();
	}

	/**
	 * Gets a neural network.
	 *
	 * @param gameType
	 *            Game type
	 * @param isDeclarer
	 *            TRUE, if declarer network is desired
	 * @param trickNoInGame
	 *            Trick number in current game
	 * @return Neural network
	 */
	public static INeuralNetwork getNetwork(GameType gameType, boolean isDeclarer, int trickNoInGame) {

		Map<PlayerParty, List<INeuralNetwork>> gameTypeNets = networks.get(gameType);

		List<INeuralNetwork> playerPartyNets = null;
		if (GameType.RAMSCH.equals(gameType) || isDeclarer) {
			playerPartyNets = gameTypeNets.get(PlayerParty.DECLARER);
		} else {
			playerPartyNets = gameTypeNets.get(PlayerParty.OPPONENT);
		}

		return playerPartyNets.get(trickNoInGame);
	}

	/**
	 * Gets an instance of the SkatNetworks.
	 *
	 * @return Instance
	 */
	public static SkatNetworks instance() {

		return INSTANCE;
	}

	/**
	 * Loads all neural networks from files.
	 */
	public static void loadNetworks() {
		for (Entry<GameType, Map<PlayerParty, List<INeuralNetwork>>> gameTypeNets : networks.entrySet()) {
			for (Entry<PlayerParty, List<INeuralNetwork>> playerPartyNet : gameTypeNets.getValue().entrySet()) {
				for (int trick = 0; trick < 10; trick++) {
					playerPartyNet.getValue().get(trick)
							.loadNetwork("/org/jskat/ai/nn/data/jskat".concat("." + gameTypeNets.getKey())
									.concat("." + playerPartyNet.getKey()).concat(".TRICK" + trick).concat(".nnet"),
							INPUT_NEURONS, HIDDEN_NEURONS, OUTPUT_NEURONS);
				}
			}
		}
	}

	/**
	 * Resets neural networks
	 */
	public static void resetNeuralNetworks() {

		createNetworks();
	}

	/**
	 * Saves all networks to files
	 *
	 * @param savePath
	 *            Path to files
	 */
	public static void saveNetworks(final String savePath) {
		for (GameType gameType : GameType.values()) {
			saveNetworks(savePath, gameType);
		}
	}

	/**
	 * Saves all networks of the specified game type to files
	 *
	 * @param savePath
	 *            Path to files
	 * @param gameType
	 *            Game type
	 */
	public static void saveNetworks(final String savePath, GameType gameType) {

		Map<PlayerParty, List<INeuralNetwork>> gameTypeNetworks = networks.get(gameType);

		for (Entry<PlayerParty, List<INeuralNetwork>> playerPartyNets : gameTypeNetworks.entrySet()) {
			for (int trick = 0; trick < 10; trick++) {
				playerPartyNets.getValue().get(trick).saveNetwork(savePath.concat("jskat").concat("." + gameType)
						.concat("." + playerPartyNets.getKey()).concat(".TRICK" + trick).concat(".nnet"));
			}
		}
	}

	private static void createNetworks() {
		int[] hiddenLayer = { HIDDEN_NEURONS };
		NetworkTopology topo = new NetworkTopology(INPUT_NEURONS, hiddenLayer, OUTPUT_NEURONS);

		networks = new HashMap<GameType, Map<PlayerParty, List<INeuralNetwork>>>();
		for (GameType gameType : GameType.values()) {
			networks.put(gameType, new HashMap<PlayerParty, List<INeuralNetwork>>());
			for (PlayerParty playerParty : PlayerParty.values()) {
				List<INeuralNetwork> networkList = new ArrayList<>();
				networks.get(gameType).put(playerParty, networkList);
				INeuralNetwork net = new EncogNetworkWrapper(topo, USE_BIAS);
				for (int trick = 0; trick < 10; trick++) {
					networkList.add(net);
				}
			}
		}
	}
}
