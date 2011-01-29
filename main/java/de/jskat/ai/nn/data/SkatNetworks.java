/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

 */

package de.jskat.ai.nn.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import de.jskat.util.GameType;

/**
 * Holds all neural networks for the NN player
 */
public class SkatNetworks {

	private static Log log = LogFactory.getLog(SkatNetworks.class);

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

		SkatNetworks.suitDeclarer = new MultiLayerPerceptron(TransferFunctionType.TANH, inputCount, hiddenCount,
				outputCount);
		SkatNetworks.suitOpponent = new MultiLayerPerceptron(TransferFunctionType.TANH, inputCount, hiddenCount,
				outputCount);
		SkatNetworks.nullDeclarer = new MultiLayerPerceptron(TransferFunctionType.TANH, inputCount, hiddenCount,
				outputCount);
		SkatNetworks.nullOpponent = new MultiLayerPerceptron(TransferFunctionType.TANH, inputCount, hiddenCount,
				outputCount);
		SkatNetworks.grandDeclarer = new MultiLayerPerceptron(TransferFunctionType.TANH, inputCount, hiddenCount,
				outputCount);
		SkatNetworks.grandOpponent = new MultiLayerPerceptron(TransferFunctionType.TANH, inputCount, hiddenCount,
				outputCount);
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
	 * @param path
	 *            Path to files
	 */
	public static void loadNetworks(String path) {

		String pathSep = System.getProperty("file.separator"); //$NON-NLS-1$

		suitDeclarer = NeuralNetwork.load(path.concat(pathSep).concat("jskat.suit.declarer.nnet")); //$NON-NLS-1$
		suitOpponent = NeuralNetwork.load(path.concat(pathSep).concat("jskat.suit.opponent.nnet")); //$NON-NLS-1$
		nullDeclarer = NeuralNetwork.load(path.concat(pathSep).concat("jskat.null.declarer.nnet")); //$NON-NLS-1$
		nullOpponent = NeuralNetwork.load(path.concat(pathSep).concat("jskat.null.opponent.nnet")); //$NON-NLS-1$
		grandDeclarer = NeuralNetwork.load(path.concat(pathSep).concat("jskat.grand.declarer.nnet")); //$NON-NLS-1$
		grandOpponent = NeuralNetwork.load(path.concat(pathSep).concat("jskat.grand.opponent.nnet")); //$NON-NLS-1$
	}

	/**
	 * Saves all networks to files
	 * 
	 * @param path
	 *            Path to files
	 */
	public static void saveNetworks(String path) {

		String pathSep = System.getProperty("file.separator"); //$NON-NLS-1$

		suitDeclarer.save(path.concat(pathSep).concat("jskat.suit.declarer.nnet")); //$NON-NLS-1$
		suitOpponent.save(path.concat(pathSep).concat("jskat.suit.opponent.nnet")); //$NON-NLS-1$
		nullDeclarer.save(path.concat(pathSep).concat("jskat.null.declarer.nnet")); //$NON-NLS-1$
		nullOpponent.save(path.concat(pathSep).concat("jskat.null.opponent.nnet")); //$NON-NLS-1$
		grandDeclarer.save(path.concat(pathSep).concat("jskat.grand.declarer.nnet")); //$NON-NLS-1$
		grandOpponent.save(path.concat(pathSep).concat("jskat.grand.opponent.nnet")); //$NON-NLS-1$
	}

	private final static SkatNetworks instance = new SkatNetworks();

	private static NeuralNetwork suitDeclarer;
	private static NeuralNetwork suitOpponent;
	private static NeuralNetwork nullDeclarer;
	private static NeuralNetwork nullOpponent;
	private static NeuralNetwork grandDeclarer;
	private static NeuralNetwork grandOpponent;
}
