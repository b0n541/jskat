package org.jskat.ai.nn.input;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public class CardToPlayInputStrategy implements InputStrategy {

	@Override
	public int getNeuronCount() {
		return 32;
	}

	@Override
	public double[] getNetworkInput(PlayerKnowledge knowledge, Card cardToPlay) {
		double[] result = new double[32];
		result[cardToPlay.getSuit().getSuitOrder() * 8
				+ cardToPlay.getNullOrder()] = 1.0;
		return result;
	}
}
