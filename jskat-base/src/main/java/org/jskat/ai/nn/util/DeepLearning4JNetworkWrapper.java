/**
 * Copyright (C) 2017 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn.util;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Wraps the DeepLearning4J network to fulfill the interface
 * {@link NeuralNetwork}
 */
public class DeepLearning4JNetworkWrapper implements NeuralNetwork {

	private final MultiLayerNetwork net;
	private final NetworkTopology topo;

	/**
	 * Constructor
	 *
	 * @param topo
	 *            Network topology
	 * @param useBias
	 *            TRUE, if bias nodes should be used
	 */
	public DeepLearning4JNetworkWrapper(final NetworkTopology topo, final boolean useBias) {

		this.topo = topo;

		final NeuralNetConfiguration.Builder networkBuilder = new NeuralNetConfiguration.Builder()
				.iterations(1)
				.weightInit(WeightInit.XAVIER)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(0.05)
				.miniBatch(false);

		final NeuralNetConfiguration.ListBuilder layerBuilder = new NeuralNetConfiguration.ListBuilder(networkBuilder);

		int layerIndex = 0;
		layerBuilder.layer(layerIndex, new DenseLayer.Builder()
				.nIn(topo.getInputNeuronCount())
				.nOut(topo.getHiddenNeuronCount(0))
				.activation(Activation.SIGMOID)
				.weightInit(WeightInit.DISTRIBUTION)
				.dist(new UniformDistribution(-1, 1)).build());
		layerIndex++;
		for (int i = 0; i < topo.getHiddenLayerCount() - 1; i++) {

			layerBuilder.layer(layerIndex, new DenseLayer.Builder()
					.nIn(topo.getHiddenNeuronCount(i))
					.nOut(topo.getHiddenNeuronCount(i + 1))
					.activation(Activation.SIGMOID)
					.weightInit(WeightInit.DISTRIBUTION)
					.dist(new UniformDistribution(-1, 1)).build());
			layerIndex++;
		}

		final OutputLayer.Builder outputLayerBuilder = new OutputLayer.Builder();
		outputLayerBuilder
				.nIn(topo.getHiddenNeuronCount(topo.getHiddenLayerCount() - 1))
				.nOut(topo.getOutputNeuronCount())
				.activation(Activation.SOFTMAX)
				.weightInit(WeightInit.DISTRIBUTION)
				.dist(new UniformDistribution(-1, 1));

		layerBuilder.layer(layerIndex, outputLayerBuilder.build()).backprop(true);

		net = new MultiLayerNetwork(layerBuilder.build());
		net.printConfiguration();
		net.init();
	}

	@Override
	public double adjustWeights(final double[] inputs, final double[] outputs) {

		final INDArray input = Nd4j.zeros(1, inputs.length);
		final INDArray output = Nd4j.zeros(1, outputs.length);

		for (int i = 0; i < inputs.length; i++) {
			input.putScalar(new int[] { 0, i }, inputs[i]);
		}
		for (int i = 0; i < outputs.length; i++) {
			output.putScalar(new int[] { 0, i }, outputs[i]);
		}

		net.fit(new DataSet(input, output));

		return net.gradientAndScore().getValue();
	}

	@Override
	public double adjustWeightsBatch(final double[][] inputs, final double[][] outputs) {

		final INDArray input = Nd4j.zeros(inputs.length, inputs[0].length);
		final INDArray output = Nd4j.zeros(outputs.length, outputs[0].length);

		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < inputs[i].length; j++) {
				input.putScalar(new int[] { i, j }, inputs[i][j]);
			}
		}

		for (int i = 0; i < outputs.length; i++) {
			for (int j = 0; j < outputs[i].length; j++) {
				output.putScalar(new int[] { i, j }, outputs[i][j]);
			}
		}

		net.fit(new DataSet(input, output));

		return net.gradientAndScore().getValue();
	}

	@Override
	public void resetNetwork() {
		net.init();
	}

	@Override
	public double[] getPredictedOutcome(final double[] inputs) {
		final INDArray output = net.output(Nd4j.create(inputs));
		final double[] result = new double[output.length()];
		for (int i = 0; i < output.length(); i++) {
			result[i] = output.getDouble(i);
		}
		return result;
	}

	@Override
	public long getIterations() {
		return net.getLayerWiseConfigurations().getIterationCount();
	}

	@Override
	public boolean saveNetwork(final String fileName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadNetwork(final String fileName) {
		// TODO Auto-generated method stub
	}
}
