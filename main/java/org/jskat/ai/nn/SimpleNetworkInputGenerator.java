package org.jskat.ai.nn;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Creates input signals for neural networks<br />
 * The signals are divided into three parts<br />
 * Opponent 1|Neural network player|Opponent 2
 */
public class SimpleNetworkInputGenerator implements NetworkInputGenerator {

	final static int INPUT_LENGTH = 291;
	final static int PLAYER_LENGTH = 97;

	final static double ACTIVE = 1.0;
	final static double INACTIVE = 0.0d;

	@Override
	public double[] getNetInputs(PlayerKnowledge knowledge, Card cardToPlay) {
		double[] netInputs = new double[INPUT_LENGTH];

		// set game declarer (1 neuron per player)

		// set played cards (32 neurons per player)

		// set known cards (32 neurons per player)

		// set uncertain cards (32 neuron per player)

		// set card to be played (see played cards)

		return netInputs;
	}
}
