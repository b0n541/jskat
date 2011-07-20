/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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
 * Holds information about the topology of a neural network
 */
public class NetworkTopology {

	private int inputSignals;
	private int outputSignals;
	private int hiddenLayers;
	private int[] hiddenNeurons;

	/**
	 * Constructor
	 * 
	 * @param inputs
	 *            Number of input neurons
	 * @param outputs
	 *            Number of output neurons
	 * @param hiddenLayerCount
	 *            Number of hidden layers
	 * @param hiddenNeuronCounts
	 *            Number of hidden neurons per hidden layer
	 */
	public NetworkTopology(int inputs, int outputs, int hiddenLayerCount,
			int[] hiddenNeuronCounts) {

		if (hiddenNeuronCounts.length != hiddenLayerCount) {

			throw new IllegalArgumentException(
					"Number of hidden layers and number of hidden neurons don't correspond."); //$NON-NLS-1$
		}

		this.inputSignals = inputs;
		this.outputSignals = outputs;
		this.hiddenLayers = hiddenLayerCount;
		this.hiddenNeurons = new int[this.hiddenLayers];
		this.hiddenNeurons = hiddenNeuronCounts;
	}

	/**
	 * Gets the number of input neurons
	 * 
	 * @return Number of input neurons
	 */
	int getInputNeuronCount() {

		return this.inputSignals;
	}

	/**
	 * Gets the number of hidden layers
	 * 
	 * @return Number of hidden layers
	 */
	int getHiddenLayerCount() {

		return this.hiddenLayers;
	}

	/**
	 * Gets the number of hidden neurons in a hidden layer
	 * 
	 * @param layerID
	 *            ID of the hidden layer
	 * @return Number of hidden neurons in the hidden layer
	 */
	int getHiddenNeuronCount(int layerID) {

		return this.hiddenNeurons[layerID];
	}

	/**
	 * Gets the number of output neurons
	 * 
	 * @return Number of output neurons
	 */
	int getOutputNeuronCount() {

		return this.outputSignals;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();

		// result.append("<topo>\n");
		// result.append("\t<numberoflayers>").append(this.hiddenLayers +
		// 2).append("</numberoflayers>\n");
		// result.append("\t<layer id=\"0\" type=\"input\">\n");
		// result.append("\t\t<numberofneurons>").append(getInputNeuronCount()).append("</numberofneurons>\n");
		// result.append("\t</layer>\n");
		// for (int i = 0; i < this.hiddenLayers; i++) {
		//
		// result.append("\t<layer id=\"" + (i + 1) + "\" type=\"hidden\">\n");
		// result.append("\t\t<numberofneurons>").append(getHiddenNeuronCount(i)).append("</numberofneurons>\n");
		// }
		// result.append("\t</layer>\n");
		// result.append("\t<layer id=\"" + (this.hiddenLayers + 1) +
		// "\" type=\"output\">\n");
		// result.append("\t\t<numberofneurons>").append(getOutputNeuronCount()).append("</numberofneurons>\n");
		// result.append("\t</layer>\n");
		// result.append("</topo>\n");

		result.append("input\n"); //$NON-NLS-1$
		result.append(getInputNeuronCount()).append('\n');

		result.append("hidden ").append(this.hiddenLayers).append('\n'); //$NON-NLS-1$
		for (int i = 0; i < this.hiddenLayers; i++) {

			result.append(getHiddenNeuronCount(i)).append('\n');
		}

		result.append("output\n"); //$NON-NLS-1$
		result.append(getOutputNeuronCount()).append('\n');

		return result.toString();
	}
}
