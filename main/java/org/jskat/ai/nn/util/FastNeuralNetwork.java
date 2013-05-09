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

import java.util.Random;

/**
 * Fast implementation of a neural network
 */
public class FastNeuralNetwork implements INeuralNetwork {
	// FIXME (jansch 24.03.2012)
	// change constructor for NetworkTopology(input, output, hidden1, ...)
	// change return value of getPredictedOutcome() to double[]
	// change weights from double[][] to List<List<Double>>
	// change activationValues from double[][] to List<List<Double>>
	Random rand = new Random();

	final NetworkTopology topo;
	private final ActivationFunction activFunction;
	private final Double learningRate;

	private long iterations;

	private double[][] weights;

	private double averageDifference;

	/**
	 * Constructor
	 * 
	 * @param newTopo
	 *            Topology
	 */
	public FastNeuralNetwork(NetworkTopology newTopo) {
		this(newTopo, ActivationFunction.SIGMOID, 0.3);
	}

	/**
	 * Constructor
	 * 
	 * @param newTopo
	 *            Topology
	 * @param newActivFunction
	 *            Activation function
	 * @param newLearningRate
	 *            Learning rate
	 */
	public FastNeuralNetwork(NetworkTopology newTopo, ActivationFunction newActivFunction, Double newLearningRate) {
		topo = newTopo;
		activFunction = newActivFunction;
		learningRate = newLearningRate;
		iterations = 0;
		createWeights();
		createErrorSignals();
	}

	/**
	 * Gets the weights for the network
	 * 
	 * @return Weights
	 */
	public double[][] getWeights() {
		double[][] result = new double[weights.length][weights[0].length];
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				result[i][j] = weights[i][j];
			}
		}
		return result;
	}

	/**
	 * Sets the weights to a value
	 * 
	 * @param value
	 *            Value
	 */
	public void setWeightValues(double value) {
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[0].length; j++) {
				weights[i][j] = value;
			}
		}
	}

	private void createWeights() {
		int layerCount = topo.getLayerCount();
		int maxWeightsPerLayer = topo.getMaxWeightsForLayers();
		weights = new double[layerCount - 1][maxWeightsPerLayer];
		createRandomWeights();
	}

	private double[][] createErrorSignals() {
		double[][] errorSignals = createNeuronValueArray();
		for (int i = 0; i < errorSignals.length; i++) {
			for (int j = 0; j < errorSignals[i].length; j++) {
				errorSignals[i][j] = 0.0;
			}
		}
		return errorSignals;
	}

	private void createRandomWeights() {
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				weights[i][j] = getLittleRandomWeightValue();
			}
		}
	}

	private double getLittleRandomWeightValue() {
		double result = (rand.nextDouble() - 0.5);
		if (result == 0.0) {
			result += 0.000001;
		}
		return result;
	}

	/**
	 * Gets the count of the inputs
	 * 
	 * @return Input count
	 */
	public int getInputCount() {
		return topo.getInputNeuronCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getAvgDiff() {
		return averageDifference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized double adjustWeights(double[] inputs, double[] outputs) {
		double[][] activationValues = propagateForward(inputs);
		double[][] errorSignals = setOutputParameter(activationValues, outputs);
		propagateBackward(activationValues, errorSignals);
		iterations++;
		return getAvgDiff();
	}

	private double[][] setOutputParameter(double[][] activationValues, double[] targetValues) {
		double[][] errorSignals = createErrorSignals();
		double diffSum = 0.0;
		// Calculate error signals for output neurons
		for (int outputNeuron = 0; outputNeuron < targetValues.length; outputNeuron++) {
			// first calculate error for output neuron
			double currActivationValue = activationValues[topo.getLastLayer()][outputNeuron];
			double difference = targetValues[outputNeuron] - currActivationValue;
			diffSum += difference;
			errorSignals[topo.getLastLayer()][outputNeuron] = difference
					* activFunction
							.calcBackward(getInputSignalSum(activationValues, topo.getLastLayer(), outputNeuron));
		}
		averageDifference = diffSum / targetValues.length;
		return errorSignals;
	}

	private void propagateBackward(double[][] activationValues, double[][] errorSignals) {
		for (int layer = topo.getLastLayer(); layer > 0; layer--) {
			// adjust weights leading to the layer
			for (int neuron = 0; neuron < topo.getNeuronCount(layer); neuron++) {
				int prevLayer = layer - 1;
				for (int prevLayerNeuron = 0; prevLayerNeuron < topo.getNeuronCount(prevLayer); prevLayerNeuron++) {
					weights[prevLayer][prevLayerNeuron * topo.getNeuronCount(layer) + neuron] += learningRate
							* errorSignals[layer][neuron] * activationValues[prevLayer][prevLayerNeuron];
				}
			}
			int prevLayer = layer - 1;
			if (prevLayer > 0) {
				for (int prevLayerNeuron = 0; prevLayerNeuron < topo.getNeuronCount(prevLayer); prevLayerNeuron++) {
					double errorSum = 0.0;
					for (int neuron = 0; neuron < topo.getNeuronCount(layer); neuron++) {
						errorSum += errorSignals[layer][neuron]
								* weights[prevLayer][prevLayerNeuron * topo.getNeuronCount(layer) + neuron];
					}
					errorSignals[prevLayer][prevLayerNeuron] = errorSum
							* activFunction
									.calcBackward(getInputSignalSum(activationValues, prevLayer, prevLayerNeuron));
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetNetwork() {
		createRandomWeights();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getPredictedOutcome(double[] inputs) {
		double[][] activationValues = propagateForward(inputs);
		return activationValues[topo.getLastLayer()][0];
	}

	/**
	 * Propagates the input parameters forward to the output layer
	 */
	private double[][] propagateForward(double[] inputs) {
		// Set input signals
		double[][] activationValues = createActivationValues(inputs);
		// Start with second layer
		for (int layer = 1; layer < topo.getLayerCount(); layer++) {
			for (int neuron = 0; neuron < topo.getNeuronCount(layer); neuron++) {
				double inputSum = getInputSignalSum(activationValues, layer, neuron);
				activationValues[layer][neuron] = activFunction.calcForward(inputSum);
			}
		}
		return activationValues;
	}

	private double getInputSignalSum(double[][] activationValues, int layer, int neuron) {
		double inputSum = 0.0;
		int prevLayer = layer - 1;
		for (int prevLayerNeuron = 0; prevLayerNeuron < topo.getNeuronCount(prevLayer); prevLayerNeuron++) {
			inputSum += activationValues[prevLayer][prevLayerNeuron]
					* weights[prevLayer][prevLayerNeuron * topo.getNeuronCount(layer) + neuron];
		}
		return inputSum;
	}

	private double[][] createActivationValues(double[] inputs) {
		double[][] result = createNeuronValueArray();
		for (int i = 0; i < inputs.length; i++) {
			result[0][i] = inputs[i];
		}
		return result;
	}

	private double[][] createNeuronValueArray() {
		return new double[topo.getLayerCount()][topo.getMaxNodesForLayers()];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getIterations() {
		return iterations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNetwork(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadNetwork(String fileName, int inputNeurons, int hiddenNeurons, int outputNeurons) {
		// TODO Auto-generated method stub
	}

	/**
	 * Get the count of the outputs
	 * 
	 * @return Output count
	 */
	public int getOutputCount() {
		return topo.getOutputNeuronCount();
	}

	/**
	 * Get the count of neurons in a hidden layer
	 * 
	 * @param layer
	 *            Layer
	 * @return Count of neurons
	 */
	public int getHiddenNeuronCount(int layer) {
		return topo.getHiddenNeuronCount(layer);
	}

	/**
	 * Gets the activation function
	 * 
	 * @return Activation function
	 */
	public ActivationFunction getActivationFunction() {
		return activFunction;
	}
}
