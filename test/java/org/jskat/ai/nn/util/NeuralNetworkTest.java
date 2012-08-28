/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
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
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

/**
 * Test class for {@link NeuralNetwork}
 */
public class NeuralNetworkTest extends AbstractJSkatTest {

	private INeuralNetwork network;

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
				// should learn the function in less than 3000 steps
				assertTrue(i < 3000);
				learnedBooleanFunction = true;
				break;
			}
		}

		assertTrue(learnedBooleanFunction);
	}
}
