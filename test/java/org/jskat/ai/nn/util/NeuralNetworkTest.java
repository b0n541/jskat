package org.jskat.ai.nn.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link NeuralNetwork}
 */
public class NeuralNetworkTest {

	private NeuralNetwork network;

	@Before
	public void createNeuralNetwork() throws Exception {
		int[] hiddenNeurons = { 1 };
		NetworkTopology topo = new NetworkTopology(2, 1, 1, hiddenNeurons);
		network = new NeuralNetwork(topo);
	}

	@Test
	public void testAdjustWeights() {

		assertEquals(0, network.getIterations());

		double[] inputs = { 1, 1 };
		double[] outputs = { 1 };
		network.adjustWeights(inputs, outputs);

		assertEquals(1, network.getIterations());

		network.toString();
	}
}
