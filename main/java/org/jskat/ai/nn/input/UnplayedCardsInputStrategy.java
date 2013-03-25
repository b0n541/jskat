package org.jskat.ai.nn.input;

import java.util.ArrayList;
import java.util.List;

import org.jskat.data.Trick;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public class UnplayedCardsInputStrategy implements InputStrategy {

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

		for (Trick trick : trickList) {
			for (Card card : trick.getCardList()) {
				setPlayedCardsInput(result, card);
			}
		}

		return result;
	}

	private static void setPlayedCardsInput(double[] netInputs, Card card) {

		netInputs[getNetInputIndex(card)] = 1.0;
	}

	private static int getNetInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}

}
