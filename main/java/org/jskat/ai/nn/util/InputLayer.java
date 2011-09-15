/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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
 * Input Layer
 */
class InputLayer extends Layer {

	/**
	 * Constructor
	 * 
	 * @param numberOfNeurons
	 */
	InputLayer(int numberOfNeurons) {

		super(numberOfNeurons);
	}

	/**
	 * @see Layer#createNeuron(org.jskat.ai.nn.util.Neuron.ActivationFunction)
	 */
	@Override
	Neuron createNeuron(ActivationFunction activFnct) {

		return new Neuron(activFnct);
	}

	/**
	 * Sets the input parameter
	 * 
	 * @param inputs
	 */
	void setInputParameter(double[] inputs) {

		if (inputs.length > this.neurons.size()) {
			throw new IllegalArgumentException("Wrong number of input values."); //$NON-NLS-1$
		}

		for (int i = 0; i < inputs.length; i++) {
			this.neurons.get(i).setActivationValue(inputs[i]);
		}
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

		return "input layer\n" + //$NON-NLS-1$
				outputWeightStrings.toString() + '\n';
	}
}
