/**
 * Copyright (C) 2016 Jan Schäfer (jansch@users.sourceforge.net)
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

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.RPROPType;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.simple.EncogUtility;
import org.jskat.AbstractJSkatTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for using neural networks with the Encog library.
 */
public class EncogNetworkWrapperTest extends AbstractJSkatTest {

	private static final double EPSILON = 0.1;

	/**
	 * Minimum difference between calculated output and desired result.
	 */
	private static final double MIN_DIFF = 0.1;

	/**
	 * Maximum iterations for network learning
	 */
	private static final int MAX_ITERATIONS = 2000;

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(EncogNetworkWrapperTest.class);

	/**
	 * Tests the NetworkWrapper with an XOR example.
	 */
	@Test
	public final void testXOR() {

		int[] hiddenNeurons = { 3 };
		NetworkTopology topo = new NetworkTopology(2, hiddenNeurons, 1);
		INeuralNetwork network = new EncogNetworkWrapper(topo, true);
		network.resetNetwork();

		double[][] input = { { 1.0, 1.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 0.0, 0.0 } };
		double[][] output = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

		double error = 1000.0;
		int i = 0;
		int iteration = 0;

		while (error > MIN_DIFF && iteration < MAX_ITERATIONS) {
			// error = network.adjustWeightsBatch(input, output);
			error = network.adjustWeights(input[i], output[i]);
			i = (i + 1) % output.length;
			iteration++;
		}

		if (iteration == MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations. Error: " + error);
		} else {
			log.info("Needed " + iteration + " iterations to learn.");
			log.info("Testing network:");
			for (int n = 0; n < input.length; n++) {
				log.info("Input: " + input[n][0] + " " + input[n][1] + " Expected output: " + output[n][0]
						+ " Predicted output: " + Arrays.toString(network.getPredictedOutcome(input[n])));
			}
		}
	}

	/**
	 * Tests the {@link BasicNetwork} directly with an XOR example.
	 */
	@Test
	public final void testXORDirect() {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		network.getStructure().finalizeStructure();
		network.reset();

		double[][] input = { { 1.0, 1.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 0.0, 0.0 } };
		double[][] output = { { 0.0 }, // A XOR B
				{ 1.0 }, { 1.0 }, { 0.0 } };
		BasicMLDataSet trainingSet = new BasicMLDataSet(input, output);

		double error = 1000.0;
		int i = 0;
		int iteration = 0;
		while (error > MIN_DIFF && iteration < MAX_ITERATIONS) {
			i = (i + 1) % trainingSet.size();
			Backpropagation trainer = new Backpropagation(network,
					new BasicMLDataSet(Arrays.asList(trainingSet.get(i))));
			trainer.setBatchSize(1);
			trainer.iteration();
			error = trainer.getError();
			iteration++;
		}

		if (iteration == MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations. Error: " + error);
		} else {
			log.debug("Needed " + iteration + " iterations to learn.");
			log.debug("Testing network:");
			for (int n = 0; n < input.length; n++) {
				log.debug("Input: " + input[n][0] + " " + input[n][1] + " Expected output: " + output[n][0]
						+ " Predicted output: " + network.compute(new BasicMLData(input[n])));
			}
		}
	}

	@Test
	public void testXOROnlineTraining() {

		double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };
		double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

		// Create a neural network, using the utility.
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 3, 0, 1, false);

		// Create training data.
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

		// Train the neural network.
		final Backpropagation train = new Backpropagation(network, trainingSet);
		train.setBatchSize(1);

		// Evaluate the neural network.
		EncogUtility.trainToError(train, EPSILON);
		EncogUtility.evaluate(network, trainingSet);

		if (train.getIteration() > MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations: " + train.getIteration() + ". Error: "
					+ train.getError());
		}
		// Shut down Encog.
		Encog.getInstance().shutdown();
	}

	@Test
	public void testXORBatchTraining() {

		double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };
		double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

		// Create a neural network, using the utility.
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 3, 0, 1, false);

		// Create training data.
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

		// Train the neural network.
		final Backpropagation train = new Backpropagation(network, trainingSet);
		train.setBatchSize(0);

		// Evaluate the neural network.
		EncogUtility.trainToError(train, EPSILON);
		EncogUtility.evaluate(network, trainingSet);

		if (train.getIteration() > MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations: " + train.getIteration() + ". Error: "
					+ train.getError());
		}
		// Shut down Encog.
		Encog.getInstance().shutdown();
	}

	@Test
	public void testXORResilientTraining() {

		double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };
		double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

		// Create a neural network, using the utility.
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 3, 0, 1, false);
		network.reset();

		// Create training data.
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

		// Train the neural network.
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		train.setRPROPType(RPROPType.iRPROPp);

		// Evaluate the neural network.
		EncogUtility.trainToError(train, 0.01);
		EncogUtility.evaluate(network, trainingSet);

		// Shut down Encog.
		Encog.getInstance().shutdown();
	}
}
