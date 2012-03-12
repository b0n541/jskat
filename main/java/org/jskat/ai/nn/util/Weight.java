package org.jskat.ai.nn.util;

/**
 * Weight between two neurons
 */
class Weight {

	private double weightValue;
	private Neuron inputNeuron;
	private Neuron outputNeuron;

	/**
	 * Constructor
	 * 
	 * @param newInputNeuron
	 *            Input neuron
	 * @param newOutputNeuron
	 *            Output neuron
	 * @param weight
	 *            Weight value
	 */
	Weight(Neuron newInputNeuron, Neuron newOutputNeuron, double weight) {

		this.inputNeuron = newInputNeuron;
		this.outputNeuron = newOutputNeuron;
		this.weightValue = weight;
	}

	/**
	 * Gets the weight value
	 * 
	 * @return The weight value
	 */
	double getWeightValue() {

		return this.weightValue;
	}

	/**
	 * Sets the weight value
	 * 
	 * @param newWeightValue
	 *            The weight value to set
	 */
	void setWeightValue(double newWeightValue) {

		this.weightValue = newWeightValue;
	}

	/**
	 * Gets the input neuron
	 * 
	 * @return Input neuron
	 */
	Neuron getInputNeuron() {

		return this.inputNeuron;
	}

	/**
	 * Gets the output neuron
	 * 
	 * @return Output neuron
	 */
	Neuron getOutputNeuron() {

		return this.outputNeuron;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return Double.toString(this.weightValue);
	}
}
