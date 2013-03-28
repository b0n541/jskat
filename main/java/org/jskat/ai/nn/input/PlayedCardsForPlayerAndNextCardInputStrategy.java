package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets network inputs for all cards played by the player and the next card to
 * be played
 */
public class PlayedCardsForPlayerAndNextCardInputStrategy extends
		PlayedCardsForPlayerInputStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		super.getNetworkInput(knowledge, cardToPlay);

		result[getNetworkInputIndex(cardToPlay)] = 1.0;

		return result;
	}
}
