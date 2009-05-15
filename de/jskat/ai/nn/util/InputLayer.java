/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn.util;

import de.jskat.ai.nn.util.Neuron.ActivationFunction;

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
	 * @see Layer#createNeuron(ActivationFunction)
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
			throw new IllegalArgumentException("Wrong number of input values."); //$NON-NLS-1$
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
