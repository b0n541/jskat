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

import java.util.ArrayList;
import java.util.List;

import org.jskat.ai.nn.util.Neuron.ActivationFunction;

/**
 * Input Layer
 */
class OutputLayer extends Layer {

	private List<OutputNeuron> outNeurons;

	/**
	 * Constructor
	 * 
	 * @param numberOfNeurons
	 */
	OutputLayer(int numberOfNeurons) {

		super(numberOfNeurons);

		// Cast all output neurons to use their extra functionality
		this.outNeurons = new ArrayList<OutputNeuron>();
		for (Neuron neuron : this.neurons) {
			this.outNeurons.add((OutputNeuron) neuron);
		}
	}

	/**
	 * @see Layer#createNeuron(ActivationFunction)
	 */
	@Override
	Neuron createNeuron(ActivationFunction activFnct) {

		return new OutputNeuron(activFnct);
	}

	/**
	 * Sets all output parameter and calculates the error signal of the output
	 * layer
	 * 
	 * @param outputs
	 *            Output parameter
	 * @param learningRate
	 *            Learning rate
	 */
	void setOuputParameter(double[] outputs, double learningRate) {

		if (outputs.length > this.neurons.size()) {
			throw new IllegalArgumentException("Wrong number of output values."); //$NON-NLS-1$
		}

		for (int i = 0; i < outputs.length; i++) {
			this.outNeurons.get(i).calculateError(outputs[i], learningRate);
		}
	}

	/**
	 * Gets the average difference of all neurons
	 * 
	 * @return Average difference
	 */
	double getAvgDiff() {

		double diffSum = 0.0;

		for (OutputNeuron neuron : this.outNeurons) {

			diffSum += neuron.getDiff();
		}

		return diffSum / this.neurons.size();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return "output layer\n" + //$NON-NLS-1$
				this.outNeurons.get(0).toString() + '\n';
	}
}
