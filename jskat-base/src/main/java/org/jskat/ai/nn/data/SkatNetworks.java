/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds all neural networks for the NN player
 */
public class SkatNetworks {

	private static Logger log = LoggerFactory.getLogger(SkatNetworks.class);

	private static int INPUT_NEURONS = GenericNetworkInputGenerator
			.getNeuronCountForAllStrategies();
	private static int HIDDEN_NEURONS = 10;
	private static int OUTPUT_NEURONS = 1;

	private static final boolean USE_BIAS = true;

	private final static SkatNetworks INTSTANCE = new SkatNetworks();

	private static Map<GameType, Map<PlayerParty, List<INeuralNetwork>>> networks;

	/**
	 * Gets an instance of the SkatNetworks
	 * 
	 * @return Instance
	 */
	public static SkatNetworks instance() {

		return INTSTANCE;
	}

	/**
	 * Constructor
	 */
	private SkatNetworks() {

		createNetworks();
		loadNetworks();
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

		Map<PlayerParty, List<INeuralNetwork>> gameTypeNets = networks
				.get(gameType);

		List<INeuralNetwork> playerPartyNets = null;
		if (GameType.RAMSCH.equals(gameType) || isDeclarer) {
			playerPartyNets = gameTypeNets.get(PlayerParty.DECLARER);
		} else {
			playerPartyNets = gameTypeNets.get(PlayerParty.OPPONENT);
		}

		return playerPartyNets.get(trickNoInGame);
	}

	private static void createNetworks() {
		int[] hiddenLayer = { HIDDEN_NEURONS };
		NetworkTopology topo = new NetworkTopology(INPUT_NEURONS, hiddenLayer,
				OUTPUT_NEURONS);

		networks = new HashMap<GameType, Map<PlayerParty, List<INeuralNetwork>>>();
		for (GameType gameType : GameType.values()) {
			networks.put(gameType,
					new HashMap<PlayerParty, List<INeuralNetwork>>());
			for (PlayerParty playerParty : PlayerParty.values()) {
				List<INeuralNetwork> partyNets = new ArrayList<INeuralNetwork>();
				for (int i = 0; i < 10; i++) {
					partyNets.add(new EncogNetworkWrapper(topo, USE_BIAS));
				}
				networks.get(gameType).put(playerParty, partyNets);
			}
		}
	}

	/**
	 * Loads all neural networks from files
	 * 
	 * @param filePath
	 *            Path to files
	 */
	public static void loadNetworks() {
		for (Entry<GameType, Map<PlayerParty, List<INeuralNetwork>>> gameTypeNets : networks
				.entrySet()) {
			for (Entry<PlayerParty, List<INeuralNetwork>> playerPartyNet : gameTypeNets
					.getValue().entrySet()) {
				for (int i = 0; i < 10; i++) {
					playerPartyNet
							.getValue()
							.get(i)
							.loadNetwork(
									"/org/jskat/ai/nn/data/jskat"
											.concat("." + gameTypeNets.getKey())
											.concat("."
													+ playerPartyNet.getKey())
											.concat(".TRICK" + i)
											.concat(".nnet"), INPUT_NEURONS,
									HIDDEN_NEURONS, OUTPUT_NEURONS);
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
		for (Entry<GameType, Map<PlayerParty, List<INeuralNetwork>>> gameTypeNets : networks
				.entrySet()) {
			for (Entry<PlayerParty, List<INeuralNetwork>> playerPartyNet : gameTypeNets
					.getValue().entrySet()) {
				for (int i = 0; i < 10; i++) {
					playerPartyNet
							.getValue()
							.get(i)
							.saveNetwork(
									path.concat("jskat")
											.concat("." + gameTypeNets.getKey())
											.concat("."
													+ playerPartyNet.getKey())
											.concat(".TRICK" + i)
											.concat(".nnet"));
				}
			}
		}
	}

	/**
	 * Resets neural networks
	 */
	public static void resetNeuralNetworks() {

		for (Map<PlayerParty, List<INeuralNetwork>> gameTypeNets : networks
				.values()) {
			for (List<INeuralNetwork> playerPartyNets : gameTypeNets.values()) {
				for (INeuralNetwork net : playerPartyNets) {
					net.resetNetwork();
				}
			}
		}
	}
}
