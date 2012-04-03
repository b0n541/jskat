/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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

/**
 * Layer of Neurons for the NeuralNetwork
 */
abstract class Layer {

	/**
	 * All Neurons of the Layer
	 */
	protected List<Neuron> neurons = new ArrayList<Neuron>();

	/**
	 * Constructor
	 * 
	 * @param numberOfNeurons
	 *            Number of Neurons
	 */
	Layer(int numberOfNeurons) {

		// create neurons
		for (int i = 0; i < numberOfNeurons; i++) {
			this.neurons.add(createNeuron(ActivationFunction.TANH));
		}
	}

	/**
	 * Creates a neuron for the layer
	 * 
	 * @param activFnct
	 *            Activation function
	 * @return Neuron
	 */
	abstract Neuron createNeuron(ActivationFunction activFnct);

	/**
	 * Gets all Neurons of the Layer
	 * 
	 * @return All Neurons of the Layer
	 */
	List<Neuron> getNeurons() {

		return this.neurons;
	}
}
