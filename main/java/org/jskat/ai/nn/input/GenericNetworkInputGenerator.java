package org.jskat.ai.nn.input;

import java.util.ArrayList;
import java.util.List;

import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;

public class GenericNetworkInputGenerator implements NetworkInputGenerator {

	private static final List<InputStrategy> strategies = new ArrayList<InputStrategy>();

	static {
		strategies.add(new DeclarerPositionInputStrategy());
		strategies.add(new PlayerPositionInputStrategy());
		strategies.add(new PlayerPartyMadeCardsStrategy());
		strategies.add(new OpponentPartyMadeCardsStrategy());
		strategies.add(new CurrentTrickStrategy());
		strategies.add(new NextCardStrategy());
	}

	@Override
	public double[] getNetInputs(PlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = new double[getNeuronCountForAllStrategies()];
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

	/**
	 * Gets the neuron count needed for all strategies
	 * 
	 * @return Neuron count
	 */
	public static int getNeuronCountForAllStrategies() {
		int result = 0;

		for (InputStrategy strategy : strategies) {
			result += strategy.getNeuronCount();
		}

		return result;
	}
}
