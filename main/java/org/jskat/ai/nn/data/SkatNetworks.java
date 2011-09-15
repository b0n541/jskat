/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.nn.util.NetworkTopology;
import org.jskat.ai.nn.util.NeuralNetwork;
import org.jskat.util.GameType;

/**
 * Holds all neural networks for the NN player
 */
public class SkatNetworks {

	private static Log log = LogFactory.getLog(SkatNetworks.class);

	private final static SkatNetworks instance = new SkatNetworks();

	private static NeuralNetwork suitDeclarer;
	private static NeuralNetwork suitOpponent;
	private static NeuralNetwork nullDeclarer;
	private static NeuralNetwork nullOpponent;
	private static NeuralNetwork grandDeclarer;
	private static NeuralNetwork grandOpponent;

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
		// 32 input nodes for every player
		// 1 output node for win/lost
		// 1 hidden layer
		// 2*n+1 nodes in hidden layer
		// n number of nodes in input layer
		int inputCount = 96;
		// int hiddenCount = 2 * inputCount + 1;
		int hiddenCount = 50;
		int outputCount = 1;

		int[] hiddenLayer = { hiddenCount };

		NetworkTopology topol = new NetworkTopology(inputCount, outputCount, 1,
				hiddenLayer);

		// SkatNetworks.suitDeclarer = new NeuralNetwork(topol);
		// SkatNetworks.suitOpponent = new NeuralNetwork(topol);
		// SkatNetworks.nullDeclarer = new NeuralNetwork(topol);
		// SkatNetworks.nullOpponent = new NeuralNetwork(topol);
		// SkatNetworks.grandDeclarer = new NeuralNetwork(topol);
		// SkatNetworks.grandOpponent = new NeuralNetwork(topol);

		loadNetworks(ClassLoader.getSystemResource("org/jskat/ai/nn/data") //$NON-NLS-1$
				.getPath());

		// SkatNetworks.suitDeclarer = new
		// MultiLayerPerceptron(TransferFunctionType.TANH, inputCount,
		// hiddenCount,
		// outputCount);
		// SkatNetworks.suitOpponent = new
		// MultiLayerPerceptron(TransferFunctionType.TANH, inputCount,
		// hiddenCount,
		// outputCount);
		// SkatNetworks.nullDeclarer = new
		// MultiLayerPerceptron(TransferFunctionType.TANH, inputCount,
		// hiddenCount,
		// outputCount);
		// SkatNetworks.nullOpponent = new
		// MultiLayerPerceptron(TransferFunctionType.TANH, inputCount,
		// hiddenCount,
		// outputCount);
		// SkatNetworks.grandDeclarer = new
		// MultiLayerPerceptron(TransferFunctionType.TANH, inputCount,
		// hiddenCount,
		// outputCount);
		// SkatNetworks.grandOpponent = new
		// MultiLayerPerceptron(TransferFunctionType.TANH, inputCount,
		// hiddenCount,
		// outputCount);
		// SkatNetworks.loadNetworks(ClassLoader.getSystemResource(
		//				"org/jskat/ai/nn/data").toString()); //$NON-NLS-1$
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
	public static NeuralNetwork getNetwork(GameType gameType, boolean isDeclarer) {

		NeuralNetwork net = null;

		switch (gameType) {
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
			if (isDeclarer) {
				log.debug("Returning suit declarer network"); //$NON-NLS-1$
				net = getSuitDeclarer();
			} else {
				log.debug("Returning suit opponent network"); //$NON-NLS-1$
				net = getSuitOpponent();
			}
			break;
		case NULL:
			if (isDeclarer) {
				log.debug("Returning null declarer network"); //$NON-NLS-1$
				net = getNullDeclarer();
			} else {
				log.debug("Returning null opponent network"); //$NON-NLS-1$
				net = getNullOpponent();
			}
			break;
		case GRAND:
			if (isDeclarer) {
				log.debug("Returning grand declarer network"); //$NON-NLS-1$
				net = getGrandDeclarer();
			} else {
				log.debug("Returning grand opponent network"); //$NON-NLS-1$
				net = getGrandOpponent();
			}
			break;
		case PASSED_IN:
		case RAMSCH:
			// TODO get ramsch nets too
			break;
		}

		return net;
	}

	/**
	 * Gets the neural network for declarer in suit games
	 * 
	 * @return Neural network
	 */
	public static NeuralNetwork getSuitDeclarer() {
		return suitDeclarer;
	}

	/**
	 * Gets the neural network for opponent in suit games
	 * 
	 * @return Neural network
	 */
	public static NeuralNetwork getSuitOpponent() {
		return suitOpponent;
	}

	/**
	 * Gets the neural network for declarer in null games
	 * 
	 * @return Neural network
	 */
	public static NeuralNetwork getNullDeclarer() {
		return nullDeclarer;
	}

	/**
	 * Gets the neural network for opponent in null games
	 * 
	 * @return Neural network
	 */
	public static NeuralNetwork getNullOpponent() {
		return nullOpponent;
	}

	/**
	 * Gets the neural network for declarer in grand games
	 * 
	 * @return Neural network
	 */
	public static NeuralNetwork getGrandDeclarer() {
		return grandDeclarer;
	}

	/**
	 * Gets the neural network for opponent in grand games
	 * 
	 * @return Neural network
	 */
	public static NeuralNetwork getGrandOpponent() {
		return grandOpponent;
	}

	/**
	 * Loads all neural networks from files
	 * 
	 * @param filePath
	 *            Path to files
	 */
	public static void loadNetworks(String filePath) {

		String pathSep = System.getProperty("file.separator"); //$NON-NLS-1$

		suitDeclarer = new NeuralNetwork();
		suitDeclarer
				.loadNetwork("/org/jskat/ai/nn/data/jskat.suit.declarer.nnet"); //$NON-NLS-1$
		suitOpponent = new NeuralNetwork();
		suitOpponent
				.loadNetwork("/org/jskat/ai/nn/data/jskat.suit.opponent.nnet"); //$NON-NLS-1$
		nullDeclarer = new NeuralNetwork();
		nullDeclarer
				.loadNetwork("/org/jskat/ai/nn/data/jskat.null.declarer.nnet"); //$NON-NLS-1$
		nullOpponent = new NeuralNetwork();
		nullOpponent
				.loadNetwork("/org/jskat/ai/nn/data/jskat.null.opponent.nnet"); //$NON-NLS-1$
		grandDeclarer = new NeuralNetwork();
		grandDeclarer
				.loadNetwork("/org/jskat/ai/nn/data/jskat.grand.declarer.nnet"); //$NON-NLS-1$
		grandOpponent = new NeuralNetwork();
		grandOpponent
				.loadNetwork("/org/jskat/ai/nn/data/jskat.grand.opponent.nnet"); //$NON-NLS-1$
	}

	/**
	 * Saves all networks to files
	 * 
	 * @param path
	 *            Path to files
	 */
	public static void saveNetworks(String path) {

		String pathSep = System.getProperty("file.separator"); //$NON-NLS-1$

		suitDeclarer.saveNetwork(path.concat(pathSep).concat(
				"jskat.suit.declarer.nnet")); //$NON-NLS-1$
		suitOpponent.saveNetwork(path.concat(pathSep).concat(
				"jskat.suit.opponent.nnet")); //$NON-NLS-1$
		nullDeclarer.saveNetwork(path.concat(pathSep).concat(
				"jskat.null.declarer.nnet")); //$NON-NLS-1$
		nullOpponent.saveNetwork(path.concat(pathSep).concat(
				"jskat.null.opponent.nnet")); //$NON-NLS-1$
		grandDeclarer.saveNetwork(path.concat(pathSep).concat(
				"jskat.grand.declarer.nnet")); //$NON-NLS-1$
		grandOpponent.saveNetwork(path.concat(pathSep).concat(
				"jskat.grand.opponent.nnet")); //$NON-NLS-1$
	}

	/**
	 * Resets neural networks
	 */
	public static void resetNeuralNetworks() {
		suitDeclarer.resetNetwork();
		suitOpponent.resetNetwork();
		nullDeclarer.resetNetwork();
		nullOpponent.resetNetwork();
		grandDeclarer.resetNetwork();
		grandOpponent.resetNetwork();
	}
}
