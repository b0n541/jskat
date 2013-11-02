/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
		strategies.add(new PlayerPartyMadeCardsAndNextCardStrategy());
		strategies.add(new OpponentPartyMadeCardsAndNextCardStrategy());
		strategies.add(new UnplayedCardsForPlayerAndNextCardInputStrategy());
		strategies.add(new TrickCardAndNextCardInputStrategy());
	}

	@Override
	public double[] getNetInputs(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {

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
