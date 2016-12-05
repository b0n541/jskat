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

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
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

	private static final double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };
	private static final double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

	/**
	 * Maximum error between calculated output and desired result.
	 */
	private static final double MAX_ERROR = 0.1;

	/**
	 * Minimum iterations for network learning
	 */
	private static final int MIN_ITERATIONS = XOR_INPUT.length;

	/**
	 * Maximum iterations for network learning
	 */
	private static final int MAX_ITERATIONS = 2000;

	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(EncogNetworkWrapperTest.class);

	@Test
	public void testXORDirectOnlineTraining() {

		// Create a neural network, using the utility.
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 3, 0, 1, false);

		// Create training data.
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

		// Train the neural network.
		final Backpropagation train = new Backpropagation(network, trainingSet);
		train.setBatchSize(1);

		// Evaluate the neural network.
		EncogUtility.trainToError(train, MAX_ERROR);
		EncogUtility.evaluate(network, trainingSet);

		if (train.getIteration() > MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations: " + train.getIteration() + ". Error: "
					+ train.getError());
		}
		else if (train.getIteration() < MIN_ITERATIONS) {
			fail("Needed too few iterations: " + train.getIteration());
		}
	}

	@Test
	public void testXORDirectBatchTraining() {

		// Create a neural network, using the utility.
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 3, 0, 1, false);

		// Create training data.
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

		// Train the neural network.
		final Backpropagation train = new Backpropagation(network, trainingSet);
		train.setBatchSize(0);

		// Evaluate the neural network.
		EncogUtility.trainToError(train, MAX_ERROR);
		EncogUtility.evaluate(network, trainingSet);

		if (train.getIteration() > MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations: " + train.getIteration() + ". Error: "
					+ train.getError());
		} else if (train.getIteration() < MIN_ITERATIONS) {
			fail("Needed too few iterations: " + train.getIteration());
		}
	}

	@Test
	public void testXORDirectResilientTraining() {

		// Create a neural network, using the utility.
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 3, 0, 1, false);
		network.reset();

		// Create training data.
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

		// Train the neural network.
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		train.setRPROPType(RPROPType.iRPROPp);

		// Evaluate the neural network.
		EncogUtility.trainToError(train, MAX_ERROR);
		EncogUtility.evaluate(network, trainingSet);

		if (train.getIteration() > MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations: " + train.getIteration() + ". Error: "
					+ train.getError());
		} else if (train.getIteration() < MIN_ITERATIONS) {
			fail("Needed too few iterations: " + train.getIteration());
		}
	}

	/**
	 * Tests the NetworkWrapper with an XOR example and batch training.
	 */
	@Test
	public final void testXORWrapperBatch() {

		int[] hiddenNeurons = { 3, 0 };
		NetworkTopology topo = new NetworkTopology(2, hiddenNeurons, 1);
		INeuralNetwork network = new EncogNetworkWrapper(topo, true);
		network.resetNetwork();

		double error = 1000.0;
		int iteration = 0;

		while (error > MAX_ERROR && iteration < MAX_ITERATIONS * 2) {
			error = network.adjustWeightsBatch(XOR_INPUT, XOR_IDEAL);
			iteration++;
		}

		if (iteration == MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations: " + iteration + ". Error: " + error);
		} else if (iteration < MIN_ITERATIONS) {
			fail("Needed too few iterations: " + iteration);
		} else {
			log.info("Needed " + iteration + " iterations to learn.");
			log.info("Testing network:");
			for (int n = 0; n < XOR_INPUT.length; n++) {
				log.info("Input: " + XOR_INPUT[n][0] + " " + XOR_INPUT[n][1] + " Expected output: " + XOR_IDEAL[n][0]
						+ " Predicted output: " + network.getPredictedOutcome(XOR_INPUT[n]));
			}
		}
	}

	/**
	 * Tests the NetworkWrapper with an XOR example and online training.
	 */
	@Test
	public final void testXORWrapperOnline() {

		int[] hiddenNeurons = { 3, 0 };
		NetworkTopology topo = new NetworkTopology(2, hiddenNeurons, 1);
		INeuralNetwork network = new EncogNetworkWrapper(topo, true);
		network.resetNetwork();

		double error = 1000.0;
		int i = 0;
		int iteration = 0;

		while ((error > MAX_ERROR || iteration < XOR_INPUT.length) && iteration < MAX_ITERATIONS * 2) {
			error = network.adjustWeights(XOR_INPUT[i], XOR_IDEAL[i]);
			i = (i + 1) % XOR_IDEAL.length;
			iteration++;
		}

		if (iteration == MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations: " + iteration + ". Error: " + error);
		} else if (iteration < MIN_ITERATIONS) {
			fail("Needed too few iterations: " + iteration + " Error: " + error);
		} else {
			log.info("Needed " + iteration + " iterations to learn.");
			log.info("Testing network:");
			for (int n = 0; n < XOR_INPUT.length; n++) {
				log.info("Input: " + XOR_INPUT[n][0] + " " + XOR_INPUT[n][1] + " Expected output: " + XOR_IDEAL[n][0]
						+ " Predicted output: " + Arrays.toString(network.getPredictedOutcome(XOR_INPUT[n])));
			}
		}
	}
}
