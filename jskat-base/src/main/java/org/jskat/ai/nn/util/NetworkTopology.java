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
package org.jskat.ai.nn.util;

/**
 * Holds information about the topology of a neural network
 */
public class NetworkTopology {

	private final int inputSignals;
	private final int outputSignals;
	private final int[] hiddenNeurons;

	/**
	 * Constructor
	 * 
	 * @param inputs
	 *            Number of input neurons
	 * @param outputs
	 *            Number of output neurons
	 * @param hiddenLayers
	 *            Number of neurons in every hidden layer
	 */
	public NetworkTopology(int inputs, int[] hiddenLayers, int outputs) {
		this.inputSignals = inputs;
		this.hiddenNeurons = hiddenLayers;
		this.outputSignals = outputs;
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

		return hiddenNeurons.length;
	}

	/**
	 * Gets the number of hidden neurons in a hidden layer
	 * 
	 * @param layerID
	 *            ID of the hidden layer
	 * @return Number of hidden neurons in the hidden layer
	 */
	int getHiddenNeuronCount(int layerID) {

		if (layerID < 0) {
			throw new IllegalArgumentException(
					"Layer must be greater or equals 0."); //$NON-NLS-1$
		}
		if (layerID >= hiddenNeurons.length) {
			throw new IllegalArgumentException(
					"Network has only " + hiddenNeurons.length + " hidden layers."); //$NON-NLS-1$ //$NON-NLS-2$
		}
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

		result.append("hidden ").append(this.hiddenNeurons.length).append('\n'); //$NON-NLS-1$
		for (int i = 0; i < this.hiddenNeurons.length; i++) {

			result.append(getHiddenNeuronCount(i)).append('\n');
		}

		result.append("output\n"); //$NON-NLS-1$
		result.append(getOutputNeuronCount()).append('\n');

		return result.toString();
	}

	int getMaxWeightsForLayers() {
		int result = 0;
		int previousNodes = getInputNeuronCount();
		for (int i = 1; i < getLayerCount(); i++) {
			int currentNodes = getNeuronCount(i);
			// all previous nodes are connected to all current nodes
			result = Math.max(result, previousNodes * currentNodes);
			previousNodes = currentNodes;
		}
		return result;
	}

	int getLayerCount() {
		return getHiddenLayerCount() + 2;
	}

	int getLastLayer() {
		return getLayerCount() - 1;
	}

	int getNeuronCount(int layer) {
		int result = 0;
		if (layer == 0) {
			result = getInputNeuronCount();
		} else if (layer == getLastLayer()) {
			result = getOutputNeuronCount();
		} else {
			result = getHiddenNeuronCount(layer - 1);
		}
		return result;
	}

	int getMaxNodesForLayers() {
		int result = 0;
		for (int i = 0; i < getLayerCount(); i++) {
			result = Math.max(result, getNeuronCount(i));
		}
		return result;
	}
}
