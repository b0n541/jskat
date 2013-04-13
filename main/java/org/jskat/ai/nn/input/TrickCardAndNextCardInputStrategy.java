package org.jskat.ai.nn.input;

import org.jskat.data.Trick;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets the network inputs for played cards in the game per trick
 */
public class TrickCardAndNextCardInputStrategy extends TrickCardInputStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = super.getNetworkInput(knowledge, cardToPlay);

		Trick trick = knowledge.getCurrentTrick();

		// set next card to play
		if (trick.getFirstCard() == null) {
			result[getTrickOffset(trick) + 3 + getNetworkInputIndex(cardToPlay)] = 1.0;
		} else if (trick.getSecondCard() == null) {
			result[getTrickOffset(trick) + 3 + getNetworkInputIndex(cardToPlay)] = 1.0;
		} else if (trick.getThirdCard() == null) {
			result[getTrickOffset(trick) + 3 + getNetworkInputIndex(cardToPlay)] = 1.0;
		}

		return result;
	}
}
