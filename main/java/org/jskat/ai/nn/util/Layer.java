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
