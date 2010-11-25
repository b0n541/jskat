/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn.util;

import de.jskat.ai.nn.util.Neuron.ActivationFunction;

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
	 * @see Layer#createNeuron(ActivationFunction)
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
