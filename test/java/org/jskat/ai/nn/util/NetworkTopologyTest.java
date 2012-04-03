package org.jskat.ai.nn.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NetworkTopologyTest {
	@Test
	public void testGetMaxNodesForLayers() {
		NetworkTopology topology = createSimpleTopology();
		assertEquals(3, topology.getMaxNodesForLayers());
	}

	@Test
	public void testGetMaxNodesForLayers2() {
		NetworkTopology topology = createComplexTopology();
		assertEquals(5, topology.getMaxNodesForLayers());
	}

	@Test
	public void testGetMaxWeightsForLayers() {
		NetworkTopology topology = createSimpleTopology();
		assertEquals(6, topology.getMaxWeightsForLayers());
	}

	@Test
	public void testGetMaxWeightsForLayers2() {
		NetworkTopology topology = createComplexTopology();
		assertEquals(20, topology.getMaxWeightsForLayers());
	}

	private NetworkTopology createSimpleTopology() {
		int[] hiddenNeurons = { 3 };
		return new NetworkTopology(2, 1, 1, hiddenNeurons);
	}

	private NetworkTopology createComplexTopology() {
		int[] hiddenNeurons = { 3, 4, 5, 2 };
		return new NetworkTopology(2, 1, 4, hiddenNeurons);
	}
}
