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
 * Holds information about the topology of a neural network
 */
public class NetworkTopology {

	private final int inputSignals;
	private final int outputSignals;
	private final int hiddenLayers;
	private final int[] hiddenNeurons;

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
	public NetworkTopology(int inputs, int outputs, int hiddenLayerCount, int[] hiddenNeuronCounts) {

		if (hiddenNeuronCounts.length != hiddenLayerCount) {

			throw new IllegalArgumentException("Number of hidden layers and number of hidden neurons don't correspond."); //$NON-NLS-1$
		}

		this.inputSignals = inputs;
		this.outputSignals = outputs;
		this.hiddenLayers = hiddenLayerCount;
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

		if (layerID < 0) {
			throw new IllegalArgumentException("Layer must be greater or equals 0."); //$NON-NLS-1$
		}
		if (layerID >= hiddenNeurons.length) {
			throw new IllegalArgumentException("Network has only " + hiddenNeurons.length + " hidden layers."); //$NON-NLS-1$ //$NON-NLS-2$
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

		result.append("hidden ").append(this.hiddenLayers).append('\n'); //$NON-NLS-1$
		for (int i = 0; i < this.hiddenLayers; i++) {

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
