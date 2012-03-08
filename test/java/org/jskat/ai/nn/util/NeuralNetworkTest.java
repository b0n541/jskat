package org.jskat.ai.nn.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

/**
 * Test class for {@link NeuralNetwork}
 */
public class NeuralNetworkTest extends AbstractJSkatTest {

	private NeuralNetwork network;

	@Test
	public void testAdjustWeights() {

		int[] hiddenNeurons = { 1 };
		NetworkTopology topo = new NetworkTopology(2, 1, 1, hiddenNeurons);
		network = new NeuralNetwork(topo);

		assertEquals(0, network.getIterations());

		double[] inputs = { 1, 1 };
		double[] outputs = { 1 };
		network.adjustWeights(inputs, outputs);

		assertEquals(1, network.getIterations());

		network.toString();
	}

	@Test
	public void testBooleanFunction() {

		int[] hiddenNeurons = { 2 };
		NetworkTopology topo = new NetworkTopology(3, 1, 1, hiddenNeurons);
		network = new NeuralNetwork(topo);

		double[][] input = { { 1.0, 1.0, 1.0 }, { 1.0, 1.0, 0.0 }, { 1.0, 0.0, 1.0 }, { 1.0, 0.0, 0.0 },
				{ 0.0, 1.0, 1.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0 } };
		double[][] output = { { 1.0 }, // A and B or C
				{ 1.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 } };

		int goodGuess = 0;

		boolean learnedBooleanFunction = false;
		for (int i = 0; i < 10000; i++) {
			network.adjustWeights(input[i % input.length], output[i % input.length]);

			if (network.getAvgDiff() < 0.1) {
				goodGuess++;
			} else {
				goodGuess = 0;
			}

			if (goodGuess > input.length) {
				// should learn the function in 2500 steps
				assertTrue(i < 2500);
				learnedBooleanFunction = true;
				break;
			}
		}

		assertTrue(learnedBooleanFunction);
	}
}
