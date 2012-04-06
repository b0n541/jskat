package org.jskat.ai.nn.util;

import org.junit.Test;

public class EncogNetworkWrapperTest {

	private static final double MIN_DIFF = 0.0000001;

	@Test
	public void testBooleanFunction() {

		int[] hiddenNeurons = { 3 };
		NetworkTopology topo = new NetworkTopology(3, 1, 1, hiddenNeurons);
		INeuralNetwork network = new EncogNetworkWrapper(topo);

		double[][] input = { { 1.0, 1.0, 1.0 }, { 1.0, 1.0, 0.0 }, { 1.0, 0.0, 1.0 }, { 1.0, 0.0, 0.0 },
				{ 0.0, 1.0, 1.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0 } };
		double[][] output = { { 1.0 }, // A and B or C
				{ 1.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 } };

		// assertTrue(network.adjustWeights(input, output) < 0.5);
	}

	@Test
	public void testXOR() {

		int[] hiddenNeurons = { 3 };
		NetworkTopology topo = new NetworkTopology(2, 1, 1, hiddenNeurons);
		INeuralNetwork network = new EncogNetworkWrapper(topo);

		double[][] input = { { 1.0, 1.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 0.0, 0.0 } };
		double[][] output = { { 0.0 }, // A XOR B
				{ 1.0 }, { 1.0 }, { 0.0 } };

		// assertTrue(network.train(input, output) < 0.5);
	}
}
