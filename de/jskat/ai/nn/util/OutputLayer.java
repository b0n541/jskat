/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn.util;

import java.util.ArrayList;
import java.util.List;

import de.jskat.ai.nn.util.Neuron.ActivationFunction;

/**
 * Input Layer
 */
class OutputLayer extends Layer {

	private List<OutputNeuron> outNeurons;

	/**
	 * Constructor
	 * 
	 * @param numberOfNeurons 
	 */
	public OutputLayer(int numberOfNeurons) {
		
		super(numberOfNeurons);
		
		// Cast all output neurons to use their extra functionality
		this.outNeurons = new ArrayList<OutputNeuron>();
		for (Neuron neuron : this.neurons) {
			this.outNeurons.add((OutputNeuron) neuron);
		}
	}

	/**
	 * @see Layer#createNeuron(ActivationFunction)
	 */
	@Override
	Neuron createNeuron(ActivationFunction activFnct) {
		
		return new OutputNeuron(activFnct);
	}

	/**
	 * Sets all output parameter and calculates the error signal 
	 * of the output layer
	 * 
	 * @param outputs Output parameter
	 * @param learningRate Learning rate
	 */
	public void setOuputParameter(double[] outputs, double learningRate) {
		
		if (outputs.length > this.neurons.size()) {
			throw new IllegalArgumentException("Wrong number of output values.");
		}

		for (int i = 0; i < outputs.length; i++) {
			this.outNeurons.get(i).calculateError(outputs[i], learningRate);
		}
	}
	
	/**
	 * Gets the average difference of all neurons
	 * 
	 * @return Average difference
	 */
	public double getAvgDiff() {
		
		double diffSum = 0.0;
		
		for (OutputNeuron neuron : this.outNeurons) {
			
			diffSum += neuron.getDiff();
		}
		
		return diffSum / this.neurons.size();
	}
	
	/**
	 * @see Object#toString() 
	 */
	@Override
	public String toString() {
		
		return "output layer\n";
	}
}
