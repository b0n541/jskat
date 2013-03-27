package org.jskat.ai.nn.input;

import java.util.ArrayList;
import java.util.List;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public class GenericNetworkInputGenerator implements NetworkInputGenerator {

	private List<InputStrategy> strategies = new ArrayList<InputStrategy>();

	public GenericNetworkInputGenerator() {
		strategies.add(new PlayedCardsInputStrategy());
		strategies.add(new UnplayedCardsInputStrategy());
	}

	@Override
	public double[] getNetInputs(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = new double[64];
		int index = 0;
		for (int strategyCount = 0; strategyCount < strategies.size(); strategyCount++) {
			InputStrategy strategy = strategies.get(strategyCount);
			double[] networkInput = strategy.getNetworkInput(knowledge,
					cardToPlay);
			for (int i = 0; i < strategy.getNeuronCount(); i++) {
				result[index] = networkInput[i];
				index++;
			}
		}

		return result;
	}
}
