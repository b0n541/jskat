/**
 * Copyright (C) 2017 Jan Schäfer (jansch@users.sourceforge.net)
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
package org.jskat.ai.nn.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jskat.ai.nn.input.GenericNetworkInputGenerator;
import org.jskat.ai.nn.util.DeepLearning4JNetworkWrapper;
import org.jskat.ai.nn.util.NetworkTopology;
import org.jskat.ai.nn.util.NeuralNetwork;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds all neural networks for the NN player.
 */
public final class SkatNetworks {

	private static final Logger LOG = LoggerFactory.getLogger(SkatNetworks.class);

	private static int INPUT_NEURONS = GenericNetworkInputGenerator.getNeuronCountForAllStrategies();
	private static int OUTPUT_NEURONS = 2;
	private static int[] HIDDEN_NEURONS = { INPUT_NEURONS * 2 + 1, 50 };

	private static final boolean USE_BIAS = true;

	private final static SkatNetworks INSTANCE = new SkatNetworks();

	private static Map<GameType, Map<PlayerParty, List<NeuralNetwork>>> networks;

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
	public static NeuralNetwork getNetwork(final GameType gameType, final boolean isDeclarer, final int trickNoInGame) {

		final Map<PlayerParty, List<NeuralNetwork>> gameTypeNets = networks.get(gameType);

		List<NeuralNetwork> playerPartyNets = null;
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
		for (final Entry<GameType, Map<PlayerParty, List<NeuralNetwork>>> gameTypeNets : networks.entrySet()) {
			for (final Entry<PlayerParty, List<NeuralNetwork>> playerPartyNet : gameTypeNets.getValue().entrySet()) {
				for (int trick = 0; trick < 10; trick++) {
					playerPartyNet.getValue().get(trick)
							.loadNetwork("/org/jskat/ai/nn/data/jskat".concat("." + gameTypeNets.getKey())
									.concat("." + playerPartyNet.getKey()).concat(".TRICK" + trick).concat(".nnet"));
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
		for (final GameType gameType : GameType.values()) {
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
	public static void saveNetworks(final String savePath, final GameType gameType) {

		final Map<PlayerParty, List<NeuralNetwork>> gameTypeNetworks = networks.get(gameType);

		for (final Entry<PlayerParty, List<NeuralNetwork>> playerPartyNets : gameTypeNetworks.entrySet()) {
			for (int trick = 0; trick < 10; trick++) {
				playerPartyNets.getValue().get(trick).saveNetwork(savePath.concat("jskat").concat("." + gameType)
						.concat("." + playerPartyNets.getKey()).concat(".TRICK" + trick).concat(".nnet"));
			}
		}
	}

	private static void createNetworks() {
		final NetworkTopology topo = new NetworkTopology(INPUT_NEURONS, HIDDEN_NEURONS, OUTPUT_NEURONS);

		networks = new HashMap<>();
		// for (final GameType gameType : GameType.values()) {
		final GameType gameType = GameType.GRAND;
		networks.put(gameType, new HashMap<PlayerParty, List<NeuralNetwork>>());
		for (final PlayerParty playerParty : PlayerParty.values()) {
			networks.get(gameType).put(playerParty, new ArrayList<NeuralNetwork>());
			// NeuralNetwork network = new EncogNetworkWrapper(topo, USE_BIAS);
			final NeuralNetwork network = new DeepLearning4JNetworkWrapper(topo, USE_BIAS);
			for (int trick = 0; trick < 10; trick++) {
				networks.get(gameType).get(playerParty).add(network);
			}
		}
		// }
		LOG.debug(networks.toString());
	}
}
