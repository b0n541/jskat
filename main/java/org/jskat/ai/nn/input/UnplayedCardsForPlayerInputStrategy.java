package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets network inputs for unplayed cards by the player
 */
public class UnplayedCardsForPlayerInputStrategy extends
		AbstractCardInputStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		for (Card card : Card.values()) {
			if (knowledge.isOwnCard(card)) {
				result[getNetworkInputIndex(card)] = 1.0;
			}
		}

		return result;
	}
}
