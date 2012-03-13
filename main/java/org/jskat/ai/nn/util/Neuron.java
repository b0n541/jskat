/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0
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
 * Neuron for NeuralNetwork
 */
class Neuron {

	/**
	 * All activation functions supported by the Neuron
	 */
	protected enum ActivationFunction {
		/**
		 * Sigmoid function (quasi standard)
		 */
		SIGMOID,
		/**
		 * Tangens hyperbolicus function
		 */
		TANH
	}

	private ActivationFunction activFunction = ActivationFunction.SIGMOID;

	/**
	 * Activation value
	 */
	protected double activationValue = 0.0;
	/**
	 * Error signal
	 */
	protected double errorSignal = 0.0;
	/**
	 * Sum of input signals
	 */
	protected double inputSum = 0.0;

	/**
	 * Input weights
	 */
	protected List<Weight> incomingWeights = new ArrayList<Weight>();
	private List<Weight> outgoingWeights = new ArrayList<Weight>();

	/**
	 * Constructor
	 * 
	 * @param activationFunction
	 *            Activation function
	 */
	Neuron(ActivationFunction activationFunction) {

		this.activationValue = 0.0;
		this.errorSignal = 0.0;
		this.activFunction = activationFunction;
	}

	/**
	 * Sets the activation value
	 * 
	 * @param input
	 *            Activation value
	 */
	void setActivationValue(double input) {

		this.activationValue = input;
	}

	/**
	 * Calculates the activation value
	 */
	void calcActivationValue() {

		this.inputSum = 0.0;
		for (Weight weight : this.incomingWeights) {
			this.inputSum += weight.getInputNeuron().getActivationValue() * weight.getWeightValue();
		}
		this.activationValue = activFnct(this.inputSum);
	}

	/**
	 * Gets the activation value
	 * 
	 * @return Activation value
	 */
	double getActivationValue() {

		return this.activationValue;
	}

	private double activFnct(double input) {

		double result = 0.0;

		switch (this.activFunction) {
		case SIGMOID:
			result = sigmoid(input);
			break;
		case TANH:
			result = tanh(input);
			break;
		}

		return result;
	}

	/**
	 * Derived activation function
	 * 
	 * @param input
	 *            Input value
	 * @return Activation value
	 */
	protected double dactivFnct(double input) {

		double result = 0.0;

		switch (this.activFunction) {
		case SIGMOID:
			result = dsigmoid(input);
			break;
		case TANH:
			result = dtanh(input);
			break;
		}

		return result;
	}

	private static final double sigmoid(double input) {

		return 1.0d / (1.0d + Math.exp(-1.0 * input));
	}

	private static final double dsigmoid(double input) {

		return sigmoid(input) * (1.0d - sigmoid(input));
	}

	private static final double tanh(double input) {

		return Math.tanh(input);
	}

	private static final double dtanh(double input) {

		return 1.0d - tanh(input) * tanh(input);
	}

	/**
	 * Adjusts all internal weights<br>
	 * Use only after {@link OutputNeuron#calculateError(double, double)}
	 * 
	 * @param learningRate
	 */
	protected void adjustWeights(double learningRate) {

		// first calculate error from output weights
		double errorSum = 0.0;
		for (Weight weight : this.outgoingWeights) {
			Neuron outputNeuron = weight.getOutputNeuron();
			errorSum += outputNeuron.getErrorSignal() * weight.getWeightValue();
		}
		this.errorSignal = dactivFnct(this.inputSum) * errorSum;
		// adjust all weights leading to this neuron
		for (Weight weight : this.incomingWeights) {
			weight.setWeightValue(weight.getWeightValue() + learningRate * this.errorSignal
					* weight.getInputNeuron().getActivationValue());
		}
	}

	/**
	 * Gets the current error signal for the Neuron
	 * 
	 * @return Error signal
	 */
	double getErrorSignal() {

		return this.errorSignal;
	}

	/**
	 * Adds a new incoming weight ot the incoming weights
	 * 
	 * @param incomingWeight
	 *            New incoming weight
	 */
	void addIncomingWeight(Weight incomingWeight) {

		this.incomingWeights.add(incomingWeight);
	}

	/**
	 * Adds a new outgoing weight to the outgoing weights
	 * 
	 * @param outgoingWeight
	 *            New outgoing weight
	 */
	void addOutgoingWeight(Weight outgoingWeight) {

		this.outgoingWeights.add(outgoingWeight);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return Double.toString(this.activationValue);
	}

	/**
	 * Gets a string representation of all incoming weights
	 * 
	 * @return All incoming weights as String
	 */
	String getInputWeightString() {

		StringBuffer result = new StringBuffer();

		for (Weight weight : this.incomingWeights) {

			result.append(weight).append(' ');
		}

		return result.toString();
	}

	/**
	 * Gets a string representation of all outgoing weights
	 * 
	 * @return All outgoing weights as String
	 */
	String getOutputWeightString() {

		StringBuffer result = new StringBuffer();

		for (Weight weight : this.outgoingWeights) {

			result.append(weight.toString()).append(' ');
		}

		return result.toString();
	}
}
