package org.jskat.ai.nn.input;

import java.util.ArrayList;
import java.util.List;

import org.jskat.data.Trick;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public class PlayedCardsInputStrategy implements InputStrategy {

	@Override
	public int getNeuronCount() {
		return 32;
	}

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {

		List<Trick> trickList = new ArrayList<Trick>();
		trickList.addAll(knowledge.getCompletedTricks());
		trickList.add(knowledge.getCurrentTrick());

		double[] result = new double[32];
		for (Card card : Card.values()) {
			if (knowledge.hasCard(knowledge.getPlayerPosition(), card)) {
				result[getNetInputIndex(card)] = 1.0;
			}
		}

		return result;
	}

	private static int getNetInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}

}
