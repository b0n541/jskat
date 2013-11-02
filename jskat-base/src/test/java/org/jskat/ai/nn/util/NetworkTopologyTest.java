/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
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
		return new NetworkTopology(2, hiddenNeurons, 1);
	}

	private NetworkTopology createComplexTopology() {
		int[] hiddenNeurons = { 3, 4, 5, 2 };
		return new NetworkTopology(2, hiddenNeurons, 1);
	}
}
