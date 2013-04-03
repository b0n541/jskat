package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets the network inputs for unplayed cards in the game
 */
public class UnplayedCardsInputStrategy extends AbstractCardInputStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		for (Card card : Card.values()) {
			if (!knowledge.isCardPlayed(card)) {
				result[getNetworkInputIndex(card)] = 1.0;
			}
		}

		return result;
	}
}
