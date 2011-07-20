/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20 21:16:11
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
package org.jskat.ai.nn.util;

import org.jskat.ai.nn.util.Neuron.ActivationFunction;

/**
 * Hidden layer for NeuralNetwork
 */
class HiddenLayer extends Layer {

	/**
	 * @see Layer#Layer(int)
	 */
	HiddenLayer(int numberOfNeurons) {

		super(numberOfNeurons);
	}

	/**
	 * @see Layer#createNeuron(ActivationFunction)
	 */
	@Override
	Neuron createNeuron(ActivationFunction activFnct) {

		return new Neuron(activFnct);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer outputWeightStrings = new StringBuffer();

		for (Neuron neuron : this.neurons) {

			outputWeightStrings.append(neuron.getOutputWeightString()).append(
					' ');
		}

		return "hidden layer\n" + //$NON-NLS-1$
				outputWeightStrings.toString() + '\n';
	}

}
