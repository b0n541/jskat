/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

*/

package de.jskat.ai.nn.util;

/**
 * Output neuron
 */
class OutputNeuron extends Neuron {

	private double diff = 0.0;

	/**
	 * @see Neuron#Neuron(ActivationFunction)
	 */
	OutputNeuron(ActivationFunction activFunction) {
		
		super(activFunction);
	}

	/**
	 * Calculates the error signal of an output node
	 * 
	 * @param targetValue
	 * @param learningRate
	 */
	protected void calculateError(double targetValue, double learningRate) {
			
			// first calculate error for output neuron
			this.diff = (targetValue - this.activationValue);
			this.errorSignal = (targetValue - this.activationValue) * dactivFnct(this.inputSum);
			// adjust all weights leading to this neuron
			for (Weight weight : this.incomingWeights) {
				weight.setWeightValue(weight.getWeightValue() +
										learningRate * this.errorSignal *
										weight.getInputNeuron().getActivationValue());
			}
		}

	/**
	 * Gets the real difference between calculated output and desired value
	 * 
	 * @return Difference between output and desired value
	 */
	protected double getDiff() {
		
		return this.diff;
	}
}
