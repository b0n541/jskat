/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.1
 * Copyright (C) 2013-05-10
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

public class FastNeuralNetworkTest {

	private static final double MIN_DIFF = 0.0000001;

	@Test
	public void testCreation() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertNotNull(net);
	}

	@Test
	public void testInputNeuronCount() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertEquals(2, net.getInputCount());
	}

	@Test
	public void testOutputNeuronCount() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertEquals(1, net.getOutputCount());
	}

	@Test
	public void testHiddenNeuronCount() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertEquals(3, net.getHiddenNeuronCount(0));
	}

	@Test
	public void testHiddenNeuronCount2() {
		try {
			FastNeuralNetwork net = createSimpleNetwork();
			net.getHiddenNeuronCount(1);
			fail("Illegal argument exception not thrown."); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		} catch (Exception e) {
			fail("Illegal argument exception not thrown."); //$NON-NLS-1$
		}
	}

	@Test
	public void testHiddenNeuronCount3() {
		try {
			FastNeuralNetwork net = createSimpleNetwork();
			net.getHiddenNeuronCount(-1);
			fail("Illegal argument exception not thrown."); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		} catch (Exception e) {
			fail("Illegal argument exception not thrown."); //$NON-NLS-1$
		}
	}

	@Test
	public void testWeights() {
		FastNeuralNetwork net = createSimpleNetwork();
		double[][] weights = net.getWeights();
		assertEquals(2, weights.length);
		assertEquals(6, weights[0].length);
	}

	@Test
	public void testWeights2() {
		FastNeuralNetwork net = createComplexNetwork();
		double[][] weights = net.getWeights();
		assertEquals(5, weights.length);
		assertEquals(20, weights[0].length);
	}

	@Test
	public void testRandomWeightInit() {
		FastNeuralNetwork net = createSimpleNetwork();
		double[][] weights = net.getWeights();
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				assertTrue(weights[i][j] != 0.0);
			}
		}
	}

	@Test
	public void testResetNetwork() {
		FastNeuralNetwork net = createSimpleNetwork();
		double[][] initialWeights = net.getWeights();
		net.resetNetwork();
		double[][] resetedWeights = net.getWeights();
		for (int i = 0; i < initialWeights.length; i++) {
			for (int j = 0; j < initialWeights[i].length; j++) {
				assertFalse(initialWeights[i][j] == resetedWeights[i][j]);
			}
		}
	}

	@Test
	public void testActivationFunction() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertEquals(ActivationFunction.SIGMOID, net.getActivationFunction());
	}

	@Test
	public void testSetWeights() {
		FastNeuralNetwork net = createSimpleNetwork();
		net.setWeightValues(1);
		double[][] weights = net.getWeights();
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				assertEquals(1, weights[i][j], 0.0);
			}
		}
	}

	@Test
	public void testGetPredictedOutcome() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertEquals(0, net.getIterations());
		net.setWeightValues(1.0);
		double[] inputs = { 1.0, 1.0 };
		assertTrue(net.getPredictedOutcome(inputs) > 0.9);
		assertEquals(0, net.getIterations());
	}

	@Test
	public void testGetPredictedOutcome2() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertEquals(0, net.getIterations());
		net.setWeightValues(0.0);
		double[] inputs = { 1.0, 1.0 };
		assertEquals(0.5, net.getPredictedOutcome(inputs), 0.0);
		assertEquals(0, net.getIterations());
	}

	@Test
	public void testGetPredictedOutcome3() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertEquals(0, net.getIterations());
		net.setWeightValues(-1.0);
		double[] inputs = { -1.0, -1.0 };
		assertTrue(net.getPredictedOutcome(inputs) < 0.1);
		assertEquals(0, net.getIterations());
	}

	@Test
	public void testGetPredictedOutcome4() {
		FastNeuralNetwork net = createComplexNetwork();
		assertEquals(0, net.getIterations());
		net.setWeightValues(1.0);
		double[] inputs = { 1.0, 1.0 };
		assertTrue(net.getPredictedOutcome(inputs) > 0.8);
		assertEquals(0, net.getIterations());
	}

	@Test
	public void testGetPredictedOutcome5() throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		int[] hiddenNodes = { 2 };
		NetworkTopology topo = new NetworkTopology(2, 1, 1, hiddenNodes);
		FastNeuralNetwork net = new FastNeuralNetwork(topo, ActivationFunction.SIGMOID, 0.25);

		Field field = net.getClass().getDeclaredField("weights"); //$NON-NLS-1$
		field.setAccessible(true);
		Object fieldObject = field.get(net);
		double[][] weights = (double[][]) fieldObject;

		weights[0][0] = 0.62;
		weights[0][1] = 0.42;
		weights[0][2] = 0.55;
		weights[0][3] = -0.17;
		weights[1][0] = 0.35;
		weights[1][1] = 0.81;
		weights[1][2] = 0.0;
		weights[1][3] = 0.0;

		double[] inputs = { 0.0, 1.0 };
		assertEquals(0.643962658, net.getPredictedOutcome(inputs), MIN_DIFF);
	}

	@Test
	public void testGetInputSum() throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		int[] hiddenNodes = { 3 };
		NetworkTopology topo = new NetworkTopology(2, 1, 1, hiddenNodes);
		FastNeuralNetwork net = new FastNeuralNetwork(topo, ActivationFunction.SIGMOID, 0.25);

		Field field = net.getClass().getDeclaredField("weights"); //$NON-NLS-1$
		field.setAccessible(true);
		Object fieldObject = field.get(net);
		double[][] weights = (double[][]) fieldObject;
		weights[0][0] = 0.1;
		weights[0][1] = 0.2;
		weights[0][2] = 0.3;
		weights[0][3] = 0.4;
		weights[0][4] = 0.5;
		weights[0][5] = 0.6;
		weights[1][0] = 0.1;
		weights[1][1] = 0.2;
		weights[1][2] = 0.3;
		weights[1][3] = 0.0;
		weights[1][4] = 0.0;
		weights[1][5] = 0.0;

		Method method = net.getClass().getDeclaredMethod("getInputSignalSum", double[][].class, int.class, int.class); //$NON-NLS-1$
		method.setAccessible(true);
		double[][] activationValues = { { 1.0, 1.0, 0.0 }, { 0.0, 0.0, 0.0 }, { 0.0, 0.0, 0.0 } };
		double inputSum = ((Double) method.invoke(net, activationValues, 1, 0)).doubleValue();
		assertEquals(0.5, inputSum, MIN_DIFF);
		inputSum = ((Double) method.invoke(net, activationValues, 1, 1)).doubleValue();
		assertEquals(0.7, inputSum, MIN_DIFF);
		inputSum = ((Double) method.invoke(net, activationValues, 1, 2)).doubleValue();
		assertEquals(0.9, inputSum, MIN_DIFF);
		activationValues[1][0] = 1.0;
		activationValues[1][1] = 1.0;
		activationValues[1][2] = 1.0;
		inputSum = ((Double) method.invoke(net, activationValues, 2, 0)).doubleValue();
		assertEquals(0.6, inputSum, MIN_DIFF);
	}

	@Test
	public void testAdjustWeights() {
		FastNeuralNetwork net = createSimpleNetwork();
		assertEquals(0, net.getIterations());
		net.setWeightValues(1.0);
		double[] inputs = { 1.0, 1.0 };
		double[] outputs = { 1.0 };
		net.adjustWeights(inputs, outputs);
		assertEquals(1, net.getIterations());
	}

	@Test
	public void testAdjustWeights2() throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		int[] hiddenNodes = { 3 };
		NetworkTopology topo = new NetworkTopology(2, 1, 1, hiddenNodes);
		FastNeuralNetwork net = new FastNeuralNetwork(topo, ActivationFunction.SIGMOID, 0.25);

		Field field = net.getClass().getDeclaredField("weights"); //$NON-NLS-1$
		field.setAccessible(true);
		Object fieldObject = field.get(net);
		double[][] weights = (double[][]) fieldObject;

		weights[0][0] = 0.1;
		weights[0][1] = -0.2;
		weights[0][2] = 0.3;
		weights[0][3] = -0.4;
		weights[0][4] = 0.5;
		weights[0][5] = -0.6;
		weights[1][0] = -0.1;
		weights[1][1] = 0.2;
		weights[1][2] = -0.3;
		weights[1][3] = 0.0;
		weights[1][4] = 0.0;
		weights[1][5] = 0.0;

		double[] inputs = { 0.0, 1.0 };
		double[] outputs = { 0.0 };

		double predictedOutcome = net.getPredictedOutcome(inputs);

		assertEquals(0.49451460111701684, predictedOutcome, 0.0);

		net.adjustWeights(inputs, outputs);

		assertEquals(0.1, weights[0][0], 0.0);
		assertEquals(-0.2, weights[0][1], 0.0);
		assertEquals(0.3, weights[0][2], 0.0);
		assertEquals(-0.3991654286675917, weights[0][3], 0.0);
		assertEquals(0.49868721622470275, weights[0][4], 0.0);
		assertEquals(-0.5978015117778, weights[0][5], 0.0);
		assertEquals(-0.11240193287365641, weights[1][0], 0.0);
		assertEquals(0.18076386376678677, weights[1][1], 0.0);
		assertEquals(-0.31095044001293987, weights[1][2], 0.0);
		assertEquals(0.0, weights[1][3], 0.0);
		assertEquals(0.0, weights[1][4], 0.0);
		assertEquals(0.0, weights[1][5], 0.0);
	}

	@Test
	public void testBooleanFunction() {

		int[] hiddenNeurons = { 3 };
		NetworkTopology topo = new NetworkTopology(3, 1, 1, hiddenNeurons);
		INeuralNetwork network = new FastNeuralNetwork(topo, ActivationFunction.SIGMOID, 0.3);

		double[][] input = { { 1.0, 1.0, 1.0 }, { 1.0, 1.0, 0.0 }, { 1.0, 0.0, 1.0 }, { 1.0, 0.0, 0.0 },
				{ 0.0, 1.0, 1.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0 } };
		double[][] output = { { 1.0 }, // A and B or C
				{ 1.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 } };

		int goodGuesses = 0;
		boolean learnedBooleanFunction = false;
		int highestBestGuesses = 0;
		int maxSteps = 100000;
		for (int i = 0; i < maxSteps; i++) {
			network.adjustWeights(input[i % input.length], output[i % input.length]);

			if (Math.abs(network.getAvgDiff()) < 0.1) {
				goodGuesses++;
			} else {
				highestBestGuesses = Math.max(highestBestGuesses, goodGuesses);
				goodGuesses = 0;
			}

			if (goodGuesses > input.length) {
				// should learn the function in 2500 steps
				learnedBooleanFunction = true;
				break;
			}
		}

		assertTrue("Didn't learn boolean function in " + maxSteps + " steps. " + highestBestGuesses
				+ " best guesses in a row.", learnedBooleanFunction);
	}

	@Test
	public void testXOR() {

		int[] hiddenNeurons = { 3 };
		NetworkTopology topo = new NetworkTopology(2, 1, 1, hiddenNeurons);
		INeuralNetwork network = new FastNeuralNetwork(topo, ActivationFunction.SIGMOID, 0.3);

		double[][] input = { { 1.0, 1.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 0.0, 0.0 } };
		double[][] output = { { 0.0 }, // A XOR B
				{ 1.0 }, { 1.0 }, { 0.0 } };

		int goodGuesses = 0;
		boolean learnedBooleanFunction = false;
		int highestBestGuesses = 0;
		int maxSteps = 100000;
		for (int i = 0; i < maxSteps; i++) {
			network.adjustWeights(input[i % input.length], output[i % input.length]);

			if (Math.abs(network.getAvgDiff()) < 0.1) {
				goodGuesses++;
			} else {
				highestBestGuesses = Math.max(highestBestGuesses, goodGuesses);
				goodGuesses = 0;
			}

			if (goodGuesses > input.length) {
				// should learn the function in 2500 steps
				learnedBooleanFunction = true;
				break;
			}
		}

		assertTrue("Didn't learn boolean function in " + maxSteps + " steps. " + highestBestGuesses
				+ " best guesses in a row.", learnedBooleanFunction);
	}

	@Test
	public void testAgainstOldImplementation() {
		int[] hiddenNeurons = { 3 };
		NetworkTopology topo = new NetworkTopology(3, 1, 1, hiddenNeurons);
		INeuralNetwork oldNetwork = new NeuralNetwork(topo);
		INeuralNetwork newNetwork = new FastNeuralNetwork(topo, ActivationFunction.SIGMOID, 0.3);

		double[][] inputs = { { 1.0, 1.0, 1.0 }, { 1.0, 1.0, 0.0 }, { 1.0, 0.0, 1.0 }, { 1.0, 0.0, 0.0 },
				{ 0.0, 1.0, 1.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0 } };
		double[][] outputs = { { 1.0 }, // A and B or C
				{ 1.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 } };
		trainNetwork(oldNetwork, inputs, outputs);
		trainNetwork(newNetwork, inputs, outputs);

		for (int i = 0; i < inputs.length; i++) {
			assertEquals(oldNetwork.getPredictedOutcome(inputs[i]), newNetwork.getPredictedOutcome(inputs[i]), 0.05);
		}
	}

	private void trainNetwork(INeuralNetwork network, double[][] inputs, double[][] outputs) {
		int goodGuesses = 0;
		boolean learnedBooleanFunction = false;
		int highestBestGuesses = 0;
		int maxSteps = 100000;
		for (int i = 0; i < maxSteps; i++) {
			network.adjustWeights(inputs[i % inputs.length], outputs[i % inputs.length]);

			if (Math.abs(network.getAvgDiff()) < 0.05) {
				goodGuesses++;
			} else {
				highestBestGuesses = Math.max(highestBestGuesses, goodGuesses);
				goodGuesses = 0;
			}

			if (goodGuesses > inputs.length) {
				// should learn the function in 2500 steps
				learnedBooleanFunction = true;
				break;
			}
		}

		assertTrue("Net couldn't learn pattern in " + maxSteps + " steps.", learnedBooleanFunction);
	}

	private FastNeuralNetwork createSimpleNetwork() {
		int[] hiddenLayers = { 3 };
		NetworkTopology topology = new NetworkTopology(2, 1, 1, hiddenLayers);
		FastNeuralNetwork net = new FastNeuralNetwork(topology, ActivationFunction.SIGMOID, 0.3);
		return net;
	}

	private FastNeuralNetwork createComplexNetwork() {
		int[] hiddenLayers = { 3, 4, 5, 2 };
		NetworkTopology topology = new NetworkTopology(2, 1, 4, hiddenLayers);
		FastNeuralNetwork net = new FastNeuralNetwork(topology, ActivationFunction.SIGMOID, 0.3);
		return net;
	}
}
