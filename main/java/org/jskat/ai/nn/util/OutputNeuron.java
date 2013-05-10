/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.1
 * Copyright (C) 2013-05-10
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
 * Output neuron
 */
class OutputNeuron extends Neuron {

	private double diff = 0.0;

	/**
	 * @see Neuron#Neuron(ActivationFunction)
	 */
	OutputNeuron(ActivationFunction activFunction) {

		super(activFunction);
	}

	/**
	 * Calculates the error signal of an output node
	 * 
	 * @param targetValue
	 * @param learningRate
	 */
	protected void calculateError(double targetValue, double learningRate) {

		// first calculate error for output neuron
		this.diff = (targetValue - this.activationValue);
		this.errorSignal = (targetValue - this.activationValue)
				* dactivFnct(this.inputSum);
		// adjust all weights leading to this neuron
		for (Weight weight : this.incomingWeights) {
			weight.setWeightValue(weight.getWeightValue() + learningRate
					* this.errorSignal
					* weight.getInputNeuron().getActivationValue());
		}
	}

	/**
	 * Gets the real difference between calculated output and desired value
	 * 
	 * @return Difference between output and desired value
	 */
	protected double getDiff() {

		return this.diff;
	}
}
