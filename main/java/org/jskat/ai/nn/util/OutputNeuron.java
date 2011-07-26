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
