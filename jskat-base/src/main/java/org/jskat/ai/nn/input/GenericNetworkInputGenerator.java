/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn.input;

import java.util.ArrayList;
import java.util.List;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;

public class GenericNetworkInputGenerator implements NetworkInputGenerator {

	private static final List<InputStrategy> strategies = new ArrayList<InputStrategy>();

	static {
		strategies.add(new DeclarerPositionInputStrategy());
		strategies.add(new PlayerPositionInputStrategy());
		strategies.add(new CurrentTrickForehandPositionStrategy());
		// strategies.add(new OpponentPartyMadeCardsStrategy());
		strategies.add(new PlayerPartyMadeCardsAndNextCardStrategy());
		strategies.add(new UnplayedPlayerPartyCardsAndNextCardStrategy());
		// strategies.add(new UnplayedPlayerCardsAndNextCardStrategy());
		strategies.add(new CurrentTrickAndNextCardStrategy());
	}

	@Override
	public double[] getNetInputs(ImmutablePlayerKnowledge knowledge,
			Card cardToPlay) {

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
