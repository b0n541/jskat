/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

 */

package de.jskat.ai.nn.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.nn.util.NetworkTopology;
import de.jskat.ai.nn.util.NeuralNetwork;
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
		int inputNodeCount = 96;
		NetworkTopology topo = new NetworkTopology(inputNodeCount, 1, 1, new int[] { 2 * inputNodeCount + 1 });
		SkatNetworks.suitDeclarer = new NeuralNetwork(topo);
		SkatNetworks.suitOpponent = new NeuralNetwork(topo);
		SkatNetworks.nullDeclarer = new NeuralNetwork(topo);
		SkatNetworks.nullOpponent = new NeuralNetwork(topo);
		SkatNetworks.grandDeclarer = new NeuralNetwork(topo);
		SkatNetworks.grandOpponent = new NeuralNetwork(topo);
	}

	public static NeuralNetwork getNetwork(GameType gameType, boolean isDeclarer) {

		NeuralNetwork net = null;

		switch (gameType) {
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
			if (isDeclarer) {
				log.debug("Returning suit declarer network");
				net = getSuitDeclarer();
			} else {
				log.debug("Returning suit opponent network");
				net = getSuitOpponent();
			}
			break;
		case NULL:
			if (isDeclarer) {
				log.debug("Returning null declarer network");
				net = getNullDeclarer();
			} else {
				log.debug("Returning null opponent network");
				net = getNullOpponent();
			}
			break;
		case GRAND:
			if (isDeclarer) {
				log.debug("Returning grand declarer network");
				net = getGrandDeclarer();
			} else {
				log.debug("Returning grand opponent network");
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

	public static NeuralNetwork getSuitDeclarer() {
		return suitDeclarer;
	}

	public static NeuralNetwork getSuitOpponent() {
		return suitOpponent;
	}

	public static NeuralNetwork getNullDeclarer() {
		return nullDeclarer;
	}

	public static NeuralNetwork getNullOpponent() {
		return nullOpponent;
	}

	public static NeuralNetwork getGrandDeclarer() {
		return grandDeclarer;
	}

	public static NeuralNetwork getGrandOpponent() {
		return grandOpponent;
	}

	public static void loadNetworks(String path) {

		String pathSep = System.getProperty("file.separator");

		suitDeclarer.loadNetwork(path.concat(pathSep).concat("jskat.suit.declarer.nnet"));
		suitOpponent.loadNetwork(path.concat(pathSep).concat("jskat.suit.opponent.nnet"));
		nullDeclarer.loadNetwork(path.concat(pathSep).concat("jskat.null.declarer.nnet"));
		nullOpponent.loadNetwork(path.concat(pathSep).concat("jskat.null.opponent.nnet"));
		grandDeclarer.loadNetwork(path.concat(pathSep).concat("jskat.grand.declarer.nnet"));
		grandOpponent.loadNetwork(path.concat(pathSep).concat("jskat.grand.opponent.nnet"));
	}

	public static void saveNetworks(String path) {

		String pathSep = System.getProperty("file.separator");

		suitDeclarer.saveNetwork(path.concat(pathSep).concat("jskat.suit.declarer.nnet"));
		suitOpponent.saveNetwork(path.concat(pathSep).concat("jskat.suit.opponent.nnet"));
		nullDeclarer.saveNetwork(path.concat(pathSep).concat("jskat.null.declarer.nnet"));
		nullOpponent.saveNetwork(path.concat(pathSep).concat("jskat.null.opponent.nnet"));
		grandDeclarer.saveNetwork(path.concat(pathSep).concat("jskat.grand.declarer.nnet"));
		grandOpponent.saveNetwork(path.concat(pathSep).concat("jskat.grand.opponent.nnet"));
	}

	private final static SkatNetworks instance = new SkatNetworks();

	private static NeuralNetwork suitDeclarer;
	private static NeuralNetwork suitOpponent;
	private static NeuralNetwork nullDeclarer;
	private static NeuralNetwork nullOpponent;
	private static NeuralNetwork grandDeclarer;
	private static NeuralNetwork grandOpponent;
}
