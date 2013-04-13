package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets the network inputs for the declarer position
 */
public class DeclarerPlayerInputStrategy extends AbstractInputStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		switch (knowledge.getDeclarer()) {
		case FOREHAND:
			result[0] = 1.0;
			break;
		case MIDDLEHAND:
			result[1] = 1.0;
			break;
		case REARHAND:
			result[2] = 1.0;
			break;
		default:
			throw new IllegalArgumentException("Declarer player is unknown.");
		}

		return result;
	}

	@Override
	public int getNeuronCount() {
		return 3;
	}
}
