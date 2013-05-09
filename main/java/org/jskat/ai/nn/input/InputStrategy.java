package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public interface InputStrategy {

	/**
	 * Gets the number of neurons the strategy creates
	 * 
	 * @return Number of neurons
	 */
	public int getNeuronCount();

	/**
	 * Gets the network input
	 * 
	 * @param knowledge
	 *            Player knowledge
	 * @param cardToPlay
	 *            Next card to play
	 * @return Network input
	 */
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay);
}
