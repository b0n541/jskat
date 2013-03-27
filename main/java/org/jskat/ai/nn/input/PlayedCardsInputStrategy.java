package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public class PlayedCardsInputStrategy implements InputStrategy {

	@Override
	public int getNeuronCount() {
		return 32;
	}

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = new double[32];
		for (Card card : Card.values()) {
			if (knowledge.isCardPlayedBy(knowledge.getPlayerPosition(), card)) {
				result[getNetInputIndex(card)] = 1.0;
			}
		}

		result[getNetInputIndex(cardToPlay)] = 1.0;

		return result;
	}

	private static int getNetInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}

}
