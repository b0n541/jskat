/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.0
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

/**
 * Hidden layer for NeuralNetwork
 */
class HiddenLayer extends Layer {

	/**
	 * @see Layer#Layer(int)
	 */
	HiddenLayer(int numberOfNeurons) {

		super(numberOfNeurons);
	}

	/**
	 * @see Layer#createNeuron(org.jskat.ai.nn.util.Neuron.ActivationFunction)
	 */
	@Override
	Neuron createNeuron(ActivationFunction activFnct) {

		return new Neuron(activFnct);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer outputWeightStrings = new StringBuffer();

		for (Neuron neuron : this.neurons) {

			outputWeightStrings.append(neuron.getOutputWeightString()).append(' ');
		}

		return "hidden layer\n" + //$NON-NLS-1$
				outputWeightStrings.toString() + '\n';
	}

}
