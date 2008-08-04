package jskat.player.AIPlayerNN.nn;

import java.util.Vector;

/**
 * Neuron for NeuralNetwork
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class Neuron {

	/**
	 * Constructor
	 * 
	 * @param activFunction Activation function 
	 */
	public Neuron(ActivationFunction activFunction) {
		
		activationValue = 0.0;
		errorSignal = 0.0;
		this.activFunction = activFunction;
	}
	
	/**
	 * Sets the activation value
	 * 
	 * @param input Activation value
	 */
	public void setActivationValue(double input) {
		
		activationValue = input;
	}
	
	/**
	 * Calculates the activation value
	 */
	public void calcActivationValue() {
		
		inputSum = 0.0;
		for (Weight weight : incomingWeights) {
			inputSum += weight.getInputNeuron().getActivationValue() * weight.getWeightValue();
		}
		activationValue = activFnct(inputSum);
	}
	
	/**
	 * Gets the activation value
	 * 
	 * @return Activation value
	 */
	public double getActivationValue() {
		
		return activationValue;
	}

	private double activFnct(double input) {
		
		double result = 0.0;
		
		switch (activFunction) {
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
	 * @param input Input value
	 * @return Activation value
	 */
	protected double dactivFnct(double input) {
		
		double result = 0.0;
		
		switch (activFunction) {
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
		
		return 1.0d / (1.0d + Math.pow(Math.E, -1.0d * input));
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
	public void adjustWeights(double learningRate) {

		// first calculate error from output weights
		double errorSum = 0.0;
		for (Weight weight : outgoingWeights) {
			Neuron outputNeuron = weight.getOutputNeuron();
			errorSum += outputNeuron.getErrorSignal() * weight.getWeightValue();
		}
		errorSignal = dactivFnct(inputSum) * errorSum;
		// adjust all weights leading to this neuron
		for (Weight weight : incomingWeights) {
			weight.setWeightValue(weight.getWeightValue() +
									learningRate * errorSignal *
									weight.getInputNeuron().getActivationValue());
		}
	}
	
	/**
	 * Gets the current error signal for the Neuron
	 * 
	 * @return Error signal
	 */
	public double getErrorSignal() {

		return errorSignal;
	}

	/**
	 * Adds a new incoming weight ot the incoming weights
	 * 
	 * @param incomingWeight New incoming weight
	 */
	public void addIncomingWeight(Weight incomingWeight) {
		
		this.incomingWeights.add(incomingWeight);
	}
	
	/**
	 * Adds a new outgoing weight to the outgoing weights
	 * 
	 * @param outgoingWeight New outgoing weight
	 */
	public void addOutgoingWeight(Weight outgoingWeight) {
		
		this.outgoingWeights.add(outgoingWeight);
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return activationValue + "(e:" + errorSignal + ")";
	}
	
	/**
	 * Gets a string representation of all incoming weights
	 * 
	 * @return All incoming weights as String
	 */
	public String getInputWeightString() {
		
		StringBuffer result = new StringBuffer();
		
		for (Weight weight : incomingWeights) {
			
			result.append(weight).append(' ');
		}
		
		return result.toString();
	}
	
	/**
	 * Gets a string representation of all outgoing weights
	 * 
	 * @return All outgoing weights as String
	 */
	public String getOutputWeightString() {
		
		StringBuffer result = new StringBuffer();
		
		for (Weight weight : outgoingWeights) {
			
			result.append(weight.toString()).append(' ');
		}
		
		return result.toString();
	}

	/**
	 * All activation functions supported by the Neuron
	 * 
	 * @author Jan Sch&auml;fer <j@nschaefer.net>
	 */
	public enum ActivationFunction {
		/**
		 * Sigmoid function (quasi standard)
		 */
		SIGMOID,
		/**
		 * Tangens hyperbolicus function
		 */
		TANH
	}
	
	private ActivationFunction activFunction = ActivationFunction.TANH;
	
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
	protected Vector<Weight> incomingWeights = new Vector<Weight>();
	private Vector<Weight> outgoingWeights = new Vector<Weight>();
}
