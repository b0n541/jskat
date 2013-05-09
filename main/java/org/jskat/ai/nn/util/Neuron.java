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

import java.util.ArrayList;
import java.util.List;

/**
 * Neuron for NeuralNetwork
 */
class Neuron {

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
	private final List<Weight> outgoingWeights = new ArrayList<Weight>();

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
