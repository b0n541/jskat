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

import java.util.ArrayList;
import java.util.List;

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

	private static INeuralNetwork suitDeclarerOpening;
	private static INeuralNetwork suitDeclarerMiddleGame;
	private static INeuralNetwork suitDeclarerEndGame;
	private static INeuralNetwork suitOpponentOpening;
	private static INeuralNetwork suitOpponentMiddleGame;
	private static INeuralNetwork suitOpponentEndGame;
	private static INeuralNetwork nullDeclarerOpening;
	private static INeuralNetwork nullDeclarerMiddleGame;
	private static INeuralNetwork nullDeclarerEndGame;
	private static INeuralNetwork nullOpponentOpening;
	private static INeuralNetwork nullOpponentMiddleGame;
	private static INeuralNetwork nullOpponentEndGame;
	private static INeuralNetwork grandDeclarerOpening;
	private static INeuralNetwork grandDeclarerMiddleGame;
	private static INeuralNetwork grandDeclarerEndGame;
	private static INeuralNetwork grandOpponentOpening;
	private static INeuralNetwork grandOpponentMiddleGame;
	private static INeuralNetwork grandOpponentEndGame;
	private static INeuralNetwork ramschDeclarerOpening;
	private static INeuralNetwork ramschDeclarerMiddleGame;
	private static INeuralNetwork ramschDeclarerEndGame;

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

		initNetworks();
	}

	private void initNetworks() {
		createNetworks();
		//		loadNetworks(ClassLoader.getSystemResource("org/jskat/ai/nn/data") //$NON-NLS-1$
		// .getPath());
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
	public static List<INeuralNetwork> getNetwork(final GameType gameType,
			final boolean isDeclarer) {

		List<INeuralNetwork> result = new ArrayList<INeuralNetwork>();

		switch (gameType) {
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
			if (isDeclarer) {
				log.debug("Returning suit declarer network"); //$NON-NLS-1$
				result = getSuitDeclarer();
			} else {
				log.debug("Returning suit opponent network"); //$NON-NLS-1$
				result = getSuitOpponent();
			}
			break;
		case NULL:
			if (isDeclarer) {
				log.debug("Returning null declarer network"); //$NON-NLS-1$
				result = getNullDeclarer();
			} else {
				log.debug("Returning null opponent network"); //$NON-NLS-1$
				result = getNullOpponent();
			}
			break;
		case GRAND:
			if (isDeclarer) {
				log.debug("Returning grand declarer network"); //$NON-NLS-1$
				result = getGrandDeclarer();
			} else {
				log.debug("Returning grand opponent network"); //$NON-NLS-1$
				result = getGrandOpponent();
			}
			break;
		case RAMSCH:
			log.debug("Returning ramsch declarer network"); //$NON-NLS-1$
			result = getRamschDeclarer();
			break;
		case PASSED_IN:
			break;
		}

		return result;
	}

	/**
	 * Gets the neural network for declarer in suit games
	 * 
	 * @return Neural network
	 */
	private static List<INeuralNetwork> getSuitDeclarer() {
		return createNetworkList(suitDeclarerOpening, suitDeclarerMiddleGame,
				suitDeclarerEndGame);
	}

	/**
	 * Gets the neural network for opponent in suit games
	 * 
	 * @return Neural network
	 */
	private static List<INeuralNetwork> getSuitOpponent() {
		return createNetworkList(suitOpponentOpening, suitOpponentMiddleGame,
				suitOpponentEndGame);
	}

	/**
	 * Gets the neural network for declarer in null games
	 * 
	 * @return Neural network
	 */
	private static List<INeuralNetwork> getNullDeclarer() {
		return createNetworkList(nullDeclarerOpening, nullDeclarerMiddleGame,
				nullDeclarerEndGame);
	}

	/**
	 * Gets the neural network for opponent in null games
	 * 
	 * @return Neural network
	 */
	private static List<INeuralNetwork> getNullOpponent() {
		return createNetworkList(nullOpponentOpening, nullOpponentMiddleGame,
				nullOpponentEndGame);
	}

	/**
	 * Gets the neural network for declarer in grand games
	 * 
	 * @return Neural network
	 */
	private static List<INeuralNetwork> getGrandDeclarer() {
		return createNetworkList(grandDeclarerOpening, grandDeclarerMiddleGame,
				grandDeclarerEndGame);
	}

	/**
	 * Gets the neural network for opponent in grand games
	 * 
	 * @return Neural network
	 */
	private static List<INeuralNetwork> getGrandOpponent() {
		return createNetworkList(grandOpponentOpening, grandOpponentMiddleGame,
				grandOpponentEndGame);
	}

	/**
	 * Gets the neural network for declarer in ramsch games
	 * 
	 * @return Neural network
	 */
	private static List<INeuralNetwork> getRamschDeclarer() {
		return createNetworkList(ramschDeclarerOpening,
				ramschDeclarerMiddleGame, ramschDeclarerEndGame);
	}

	private static List<INeuralNetwork> createNetworkList(
			final INeuralNetwork openingNet,
			final INeuralNetwork middleGameNet, final INeuralNetwork endGameNet) {

		List<INeuralNetwork> result = new ArrayList<INeuralNetwork>();

		for (int i = 0; i < 10; i++) {
			switch (i) {
			case 0:
			case 1:
			case 2:
				result.add(openingNet);
				break;
			case 3:
			case 4:
			case 5:
			case 6:
				result.add(middleGameNet);
				break;
			case 7:
			case 8:
			case 9:
				result.add(endGameNet);
				break;
			}
		}

		return result;
	}

	private void createNetworks() {
		int[] hiddenLayer = { hiddenNeurons };
		NetworkTopology topo = new NetworkTopology(inputNeurons, outputNeurons,
				1, hiddenLayer);

		suitDeclarerOpening = new EncogNetworkWrapper(topo);
		suitDeclarerMiddleGame = new EncogNetworkWrapper(topo);
		suitDeclarerEndGame = new EncogNetworkWrapper(topo);
		suitOpponentOpening = new EncogNetworkWrapper(topo);
		suitOpponentMiddleGame = new EncogNetworkWrapper(topo);
		suitOpponentEndGame = new EncogNetworkWrapper(topo);
		nullDeclarerOpening = new EncogNetworkWrapper(topo);
		nullDeclarerMiddleGame = new EncogNetworkWrapper(topo);
		nullDeclarerEndGame = new EncogNetworkWrapper(topo);
		nullOpponentOpening = new EncogNetworkWrapper(topo);
		nullOpponentMiddleGame = new EncogNetworkWrapper(topo);
		nullOpponentEndGame = new EncogNetworkWrapper(topo);
		grandDeclarerOpening = new EncogNetworkWrapper(topo);
		grandDeclarerMiddleGame = new EncogNetworkWrapper(topo);
		grandDeclarerEndGame = new EncogNetworkWrapper(topo);
		grandOpponentOpening = new EncogNetworkWrapper(topo);
		grandOpponentMiddleGame = new EncogNetworkWrapper(topo);
		grandOpponentEndGame = new EncogNetworkWrapper(topo);
		ramschDeclarerOpening = new EncogNetworkWrapper(topo);
		ramschDeclarerMiddleGame = new EncogNetworkWrapper(topo);
		ramschDeclarerEndGame = new EncogNetworkWrapper(topo);
	}

	/**
	 * Loads all neural networks from files
	 * 
	 * @param filePath
	 *            Path to files
	 */
	public static void loadNetworks(final String filePath) {

		suitDeclarerOpening
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.suit.declarer.opening.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		suitDeclarerMiddleGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.suit.declarer.middlegame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		suitDeclarerEndGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.suit.declarer.endgame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		suitOpponentOpening
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.suit.opponent.opening.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		suitOpponentMiddleGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.suit.opponent.middlegame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		suitOpponentEndGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.suit.opponent.endgame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		nullDeclarerOpening
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.null.declarer.opening.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		nullDeclarerMiddleGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.null.declarer.middlegame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		nullDeclarerEndGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.null.declarer.endgame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		nullOpponentOpening
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.null.opponent.opening.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		nullOpponentMiddleGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.null.opponent.middlegame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		nullOpponentEndGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.null.opponent.endgame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		grandDeclarerOpening
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.grand.declarer.opening.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		grandDeclarerMiddleGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.grand.declarer.middlegame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		grandDeclarerEndGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.grand.declarer.endgame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		grandOpponentOpening
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.grand.opponent.opening.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		grandOpponentMiddleGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.grand.opponent.middlegame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		grandOpponentEndGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.grand.opponent.endgame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		ramschDeclarerOpening
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.ramsch.declarer.opening.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		ramschDeclarerMiddleGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.ramsch.declarer.middlegame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
		ramschDeclarerEndGame
				.loadNetwork(
						"/org/jskat/ai/nn/data/jskat.ramsch.declarer.endgame.nnet", inputNeurons, hiddenNeurons, outputNeurons); //$NON-NLS-1$
	}

	/**
	 * Saves all networks to files
	 * 
	 * @param path
	 *            Path to files
	 */
	public static void saveNetworks(final String path) {

		suitDeclarerOpening.saveNetwork(path
				.concat("jskat.suit.declarer.opening.nnet")); //$NON-NLS-1$
		suitDeclarerMiddleGame.saveNetwork(path
				.concat("jskat.suit.declarer.middlegame.nnet")); //$NON-NLS-1$
		suitDeclarerEndGame.saveNetwork(path
				.concat("jskat.suit.declarer.endgame.nnet")); //$NON-NLS-1$
		suitOpponentOpening.saveNetwork(path
				.concat("jskat.suit.opponent.opening.nnet")); //$NON-NLS-1$
		suitOpponentMiddleGame.saveNetwork(path
				.concat("jskat.suit.opponent.middlegame.nnet")); //$NON-NLS-1$
		suitOpponentEndGame.saveNetwork(path
				.concat("jskat.suit.opponent.endgame.nnet")); //$NON-NLS-1$
		nullDeclarerOpening.saveNetwork(path
				.concat("jskat.null.declarer.opening.nnet")); //$NON-NLS-1$
		nullDeclarerMiddleGame.saveNetwork(path
				.concat("jskat.null.declarer.middlegame.nnet")); //$NON-NLS-1$
		nullDeclarerEndGame.saveNetwork(path
				.concat("jskat.null.declarer.endgame.nnet")); //$NON-NLS-1$
		nullOpponentOpening.saveNetwork(path
				.concat("jskat.null.opponent.opening.nnet")); //$NON-NLS-1$
		nullOpponentMiddleGame.saveNetwork(path
				.concat("jskat.null.opponent.middlegame.nnet")); //$NON-NLS-1$
		nullOpponentEndGame.saveNetwork(path
				.concat("jskat.null.opponent.endgame.nnet")); //$NON-NLS-1$
		grandDeclarerOpening.saveNetwork(path
				.concat("jskat.grand.declarer.opening.nnet")); //$NON-NLS-1$
		grandDeclarerMiddleGame.saveNetwork(path
				.concat("jskat.grand.declarer.middlegame.nnet")); //$NON-NLS-1$
		grandDeclarerEndGame.saveNetwork(path
				.concat("jskat.grand.declarer.endgame.nnet")); //$NON-NLS-1$
		grandOpponentOpening.saveNetwork(path
				.concat("jskat.grand.opponent.opening.nnet")); //$NON-NLS-1$
		grandOpponentMiddleGame.saveNetwork(path
				.concat("jskat.grand.opponent.middlegame.nnet")); //$NON-NLS-1$
		grandOpponentEndGame.saveNetwork(path
				.concat("jskat.grand.opponent.endgame.nnet")); //$NON-NLS-1$
		ramschDeclarerOpening.saveNetwork(path
				.concat("jskat.ramsch.declarer.opening.nnet")); //$NON-NLS-1$
		ramschDeclarerMiddleGame.saveNetwork(path
				.concat("jskat.ramsch.declarer.middlegame.nnet")); //$NON-NLS-1$
		ramschDeclarerEndGame.saveNetwork(path
				.concat("jskat.ramsch.declarer.endgame.nnet")); //$NON-NLS-1$
	}

	/**
	 * Resets neural networks
	 */
	public static void resetNeuralNetworks() {
		suitDeclarerOpening.resetNetwork();
		suitDeclarerMiddleGame.resetNetwork();
		suitDeclarerEndGame.resetNetwork();
		suitOpponentOpening.resetNetwork();
		suitOpponentMiddleGame.resetNetwork();
		suitOpponentEndGame.resetNetwork();
		nullDeclarerOpening.resetNetwork();
		nullDeclarerMiddleGame.resetNetwork();
		nullDeclarerEndGame.resetNetwork();
		nullOpponentOpening.resetNetwork();
		nullOpponentMiddleGame.resetNetwork();
		nullOpponentEndGame.resetNetwork();
		grandDeclarerOpening.resetNetwork();
		grandDeclarerMiddleGame.resetNetwork();
		grandDeclarerEndGame.resetNetwork();
		grandOpponentOpening.resetNetwork();
		grandOpponentMiddleGame.resetNetwork();
		grandOpponentEndGame.resetNetwork();
		ramschDeclarerOpening.resetNetwork();
		ramschDeclarerMiddleGame.resetNetwork();
		ramschDeclarerEndGame.resetNetwork();
	}
}
