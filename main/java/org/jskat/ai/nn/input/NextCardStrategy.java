package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public class NextCardStrategy extends AbstractInputStrategy implements
		InputStrategy {

	@Override
	public int getNeuronCount() {
		return 32;
	}

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		result[getNetworkInputIndex(cardToPlay)] = 1.0;

		return result;
	}

	private static int getNetworkInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}

}
