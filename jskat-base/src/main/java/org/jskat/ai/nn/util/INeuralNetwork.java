/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
 * Copyright (C) 2013-11-02
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