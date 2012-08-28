/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
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

/**
 * Weight between two neurons
 */
class Weight {

	private double weightValue;
	private Neuron inputNeuron;
	private Neuron outputNeuron;

	/**
	 * Constructor
	 * 
	 * @param newInputNeuron
	 *            Input neuron
	 * @param newOutputNeuron
	 *            Output neuron
	 * @param weight
	 *            Weight value
	 */
	Weight(Neuron newInputNeuron, Neuron newOutputNeuron, double weight) {

		this.inputNeuron = newInputNeuron;
		this.outputNeuron = newOutputNeuron;
		this.weightValue = weight;
	}

	/**
	 * Gets the weight value
	 * 
	 * @return The weight value
	 */
	double getWeightValue() {

		return this.weightValue;
	}

	/**
	 * Sets the weight value
	 * 
	 * @param newWeightValue
	 *            The weight value to set
	 */
	void setWeightValue(double newWeightValue) {

		this.weightValue = newWeightValue;
	}

	/**
	 * Gets the input neuron
	 * 
	 * @return Input neuron
	 */
	Neuron getInputNeuron() {

		return this.inputNeuron;
	}

	/**
	 * Gets the output neuron
	 * 
	 * @return Output neuron
	 */
	Neuron getOutputNeuron() {

		return this.outputNeuron;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return Double.toString(this.weightValue);
	}
}
