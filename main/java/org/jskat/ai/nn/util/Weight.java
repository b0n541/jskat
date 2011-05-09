/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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

/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

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
	 * @param newInputNeuron Input neuron
	 * @param newOutputNeuron Output neuron
	 * @param weight Weight value
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
	 * @param newWeightValue The weight value to set
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
