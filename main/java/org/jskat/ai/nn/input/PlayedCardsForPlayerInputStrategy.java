package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets network inputs for all cards played by the player
 */
public class PlayedCardsForPlayerInputStrategy extends
		AbstractCardInputStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		for (Card card : Card.values()) {
			if (knowledge.isCardPlayedBy(knowledge.getPlayerPosition(), card)) {
				result[getNetworkInputIndex(card)] = 1.0;
			}
		}
		return result;
	}
}
