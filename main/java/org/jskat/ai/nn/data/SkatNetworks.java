/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.nn.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jskat.ai.nn.util.EncogNetworkWrapper;
import org.jskat.ai.nn.util.INeuralNetwork;
import org.jskat.ai.nn.util.NetworkTopology;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds all neural networks for the NN player
 */
public class SkatNetworks {

	private static Logger log = LoggerFactory.getLogger(SkatNetworks.class);

	// 32 input nodes for every player
	// 32 input nodes for skat cards
	// 1 hidden layer
	// n number of nodes in hidden layer
	// 1 output node for win/lost
	private static int inputNeurons = 1089;
	private static int hiddenNeurons = 10;
	private static int outputNeurons = 1;

	private final static SkatNetworks instance = new SkatNetworks();

	private static Map<GameType, Map<PlayerParty, Map<GamePhase, INeuralNetwork>>> networks;

	/**
	 * Gets an instance of the SkatNetworks
	 * 
	 * @return Instance
	 */
	public static SkatNetworks instance() {

		return instance;
	}

	/**
	 * Constructor
	 */
	private SkatNetworks() {

		createNetworks();
	}

	/**
	 * Gets a neural network
	 * 
	 * @param gameType
	 *            Game type
	 * @param isDeclarer
	 *            TRUE, if declarer network is desired
	 * @return Neural network
	 */
	public static INeuralNetwork getNetwork(GameType gameType,
			boolean isDeclarer, int trickNoInGame) {

		Map<PlayerParty, Map<GamePhase, INeuralNetwork>> gameTypeNets = networks
				.get(gameType);

		Map<GamePhase, INeuralNetwork> playerPartyNets = null;
		if (GameType.RAMSCH.equals(gameType) || isDeclarer) {
			playerPartyNets = gameTypeNets.get(PlayerParty.DECLARER);
		} else {
			playerPartyNets = gameTypeNets.get(PlayerParty.OPPONENT);
		}

		return playerPartyNets.get(GamePhase.of(trickNoInGame));
	}

	private static void createNetworks() {
		int[] hiddenLayer = { hiddenNeurons };
		NetworkTopology topo = new NetworkTopology(inputNeurons, outputNeurons,
				1, hiddenLayer);

		networks = new HashMap<GameType, Map<PlayerParty, Map<GamePhase, INeuralNetwork>>>();
		for (GameType gameType : GameType.values()) {
			networks.put(gameType,
					new HashMap<PlayerParty, Map<GamePhase, INeuralNetwork>>());
			for (PlayerParty playerParty : PlayerParty.values()) {
				networks.get(gameType).put(playerParty,
						new HashMap<GamePhase, INeuralNetwork>());
				for (GamePhase gamePhase : GamePhase.values()) {
					networks.get(gameType).get(playerParty)
							.put(gamePhase, new EncogNetworkWrapper(topo));
				}
			}
		}
	}

	/**
	 * Loads all neural networks from files
	 * 
	 * @param filePath
	 *            Path to files
	 */
	public static void loadNetworks(final String filePath) {

		for (Entry<GameType, Map<PlayerParty, Map<GamePhase, INeuralNetwork>>> gameTypeNets : networks
				.entrySet()) {
			for (Entry<PlayerParty, Map<GamePhase, INeuralNetwork>> playerPartyNets : gameTypeNets
					.getValue().entrySet()) {
				for (Entry<GamePhase, INeuralNetwork> gamePhaseNet : playerPartyNets
						.getValue().entrySet()) {
					gamePhaseNet.getValue().loadNetwork(
							"/org/jskat/ai/nn/data/jskat"
									.concat("." + gameTypeNets.getKey())
									.concat("." + playerPartyNets.getKey())
									.concat("." + gamePhaseNet.getKey())
									.concat(".nnet"), inputNeurons,
							hiddenNeurons, outputNeurons);
				}
			}
		}
	}

	/**
	 * Saves all networks to files
	 * 
	 * @param path
	 *            Path to files
	 */
	public static void saveNetworks(final String path) {
		for (Entry<GameType, Map<PlayerParty, Map<GamePhase, INeuralNetwork>>> gameTypeNets : networks
				.entrySet()) {
			for (Entry<PlayerParty, Map<GamePhase, INeuralNetwork>> playerPartyNets : gameTypeNets
					.getValue().entrySet()) {
				for (Entry<GamePhase, INeuralNetwork> gamePhaseNet : playerPartyNets
						.getValue().entrySet()) {
					gamePhaseNet.getValue().saveNetwork(
							path.concat("jskat")
									.concat("." + gameTypeNets.getKey())
									.concat("." + playerPartyNets.getKey())
									.concat("." + gamePhaseNet.getKey())
									.concat(".nnet"));
				}
			}
		}
	}

	/**
	 * Resets neural networks
	 */
	public static void resetNeuralNetworks() {

		for (Map<PlayerParty, Map<GamePhase, INeuralNetwork>> gameTypeNets : networks
				.values()) {
			for (Map<GamePhase, INeuralNetwork> playerPartyNets : gameTypeNets
					.values()) {
				for (INeuralNetwork network : playerPartyNets.values()) {
					network.resetNetwork();
				}
			}
		}
	}
}
