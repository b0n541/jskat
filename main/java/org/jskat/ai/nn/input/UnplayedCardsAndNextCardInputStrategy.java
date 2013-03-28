package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets the network inputs for unplayed cards in the game and the next card to
 * be played
 */
public class UnplayedCardsAndNextCardInputStrategy extends
		UnplayedCardsInputStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		super.getNetworkInput(knowledge, cardToPlay);

		result[getNetworkInputIndex(cardToPlay)] = 0.0;

		return result;
	}
}
