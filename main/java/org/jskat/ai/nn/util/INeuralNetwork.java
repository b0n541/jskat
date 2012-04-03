package org.jskat.ai.nn.util;

public interface INeuralNetwork {

	/**
	 * Gets the average difference of all output neurons
	 * 
	 * @return Average difference
	 */
	public abstract double getAvgDiff();

	/**
	 * Adjusts the weights of the net according inputs and desired outputs
	 * 
	 * @param inputs
	 *            Input attributes
	 * @param outputs
	 *            Output attributes
	 * @return Average diff
	 */
	public abstract double adjustWeights(double[] inputs, double[] outputs);

	/**
	 * Resets the network, sets random values for all weights
	 */
	public abstract void resetNetwork();

	/**
	 * Gets the predicted outcome of a game according inputs
	 * 
	 * @param inputs
	 *            Input attributes
	 * @return Predicted outcome
	 */
	public abstract double getPredictedOutcome(double[] inputs);

	/**
	 * Gets the number of iterations the NeuralNetwork was trained so far
	 * 
	 * @return Number of iterations
	 */
	public abstract long getIterations();

	/**
	 * Save the network parameters to a file
	 * 
	 * @param fileName
	 *            File name to save to
	 * @return TRUE if the saving was successful
	 */
	public abstract boolean saveNetwork(String fileName);

	/**
	 * Loads network parameters from a file
	 * 
	 * @param fileName
	 *            File name to load from
	 * @param inputNeurons
	 *            Number of input neurons
	 * @param hiddenNeurons
	 *            Number of hidden neurons
	 * @param outputNeurons
	 *            Number of output neurons
	 */
	public abstract void loadNetwork(String fileName, int inputNeurons, int hiddenNeurons, int outputNeurons);
}