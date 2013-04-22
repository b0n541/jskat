package org.jskat.ai.nn.input;

import org.jskat.data.Trick;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public class CurrentTrickAndNextCardStrategy extends CurrentTrickStrategy {

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = super.getNetworkInput(knowledge, cardToPlay);

		Trick trick = knowledge.getCurrentTrick();

		// set already played cards
		if (trick.getFirstCard() == null) {
			result[3 + getNetworkInputIndex(cardToPlay)] = 1.0;
		}
		if (trick.getSecondCard() == null) {
			result[3 + 32 + getNetworkInputIndex(cardToPlay)] = 1.0;
		}
		// third card not needed to be set

		return result;
	}
}
