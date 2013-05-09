package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets network inputs for unplayed cards by the player and the next card to be
 * played
 */
public class UnplayedCardsForPlayerAndNextCardInputStrategy extends
		UnplayedCardsForPlayerInputStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = super.getNetworkInput(knowledge, cardToPlay);

		result[getNetworkInputIndex(cardToPlay)] = 0.0;

		return result;
	}
}
