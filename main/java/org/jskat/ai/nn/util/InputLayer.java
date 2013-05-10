/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
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
 * Input Layer
 */
class InputLayer extends Layer {

	/**
	 * Constructor
	 * 
	 * @param numberOfNeurons
	 */
	InputLayer(int numberOfNeurons) {

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
	 * Sets the input parameter
	 * 
	 * @param inputs
	 */
	void setInputParameter(double[] inputs) {

		if (inputs.length > this.neurons.size()) {
			throw new IllegalArgumentException(
					"Wrong number of input values. Expected: " + neurons.size() + " Actual: " + inputs.length); //$NON-NLS-1$ //$NON-NLS-2$
		}

		for (int i = 0; i < inputs.length; i++) {
			this.neurons.get(i).setActivationValue(inputs[i]);
		}
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

		return "input layer\n" + //$NON-NLS-1$
				outputWeightStrings.toString() + '\n';
	}
}
