/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
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
