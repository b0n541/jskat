/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-09
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.nn.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Input Layer
 */
class OutputLayer extends Layer {

	private final List<OutputNeuron> outNeurons;

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
	 * @see Layer#createNeuron(org.jskat.ai.nn.util.Neuron.ActivationFunction)
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
