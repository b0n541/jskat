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

import static org.junit.Assert.fail;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.jskat.AbstractJSkatTest;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for using neural networks with the Encog library.
 */
public class EncogNetworkWrapperTest extends AbstractJSkatTest {

	/**
	 * Minimum difference between calculated output and desired result.
	 */
	private static final double MIN_DIFF = 0.05;
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory
			.getLogger(EncogNetworkWrapperTest.class);

	/**
	 * Tests the {@link EncogNetworkWrapper} with a boolean statement (A AND B
	 * OR C).
	 */
	@Test
	@Ignore
	public final void testBooleanFunction() {

		int[] hiddenNeurons = { 3 };
		NetworkTopology topo = new NetworkTopology(3, hiddenNeurons, 1);
		INeuralNetwork network = new EncogNetworkWrapper(topo, true);

		double[][] input = { { 1.0, 1.0, 1.0 }, { 1.0, 1.0, 0.0 },
				{ 1.0, 0.0, 1.0 }, { 1.0, 0.0, 0.0 }, { 0.0, 1.0, 1.0 },
				{ 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0 } };
		double[][] output = { { 1.0 }, // A and B or C
				{ 1.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 }, { 1.0 }, { 0.0 } };

		// assertTrue(network.adjustWeights(input, output) < 0.5);
	}

	/**
	 * Tests the NetworkWrapper with an XOR example.
	 */
	@Test
	public final void testXOR() {

		int[] hiddenNeurons = { 3 };
		NetworkTopology topo = new NetworkTopology(2, hiddenNeurons, 1);
		INeuralNetwork network = new EncogNetworkWrapper(topo, false);
		network.resetNetwork();

		double[][] input = { { 1.0, 1.0 }, { 1.0, 0.0 }, { 0.0, 1.0 },
				{ 0.0, 0.0 } };
		double[][] output = { { 0.0 }, // A XOR B
				{ 1.0 }, { 1.0 }, { 0.0 } };

		double error = 1000.0;
		int i = 0;
		int runs = 0;

		final int MAX_RUNS = 10000;
		while (error > MIN_DIFF && runs < MAX_RUNS) {
			network.adjustWeights(input[i], output[i]);
			error = network.getAvgDiff();
			i = (i + 1) % input.length;
			runs++;
		}

		if (runs == MAX_RUNS) {
			fail("Needed more than " + MAX_RUNS + " runs. Error: " + error);
		} else {
			log.debug("Needed " + runs + " to learn.");
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
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 1));
		network.getStructure().finalizeStructure();
		network.reset();

		BasicMLDataSet trainingSet = new BasicMLDataSet();
		double[][] input = { { 1.0, 1.0 }, { 1.0, 0.0 }, { 0.0, 1.0 },
				{ 0.0, 0.0 } };
		double[][] output = { { 0.0 }, // A XOR B
				{ 1.0 }, { 1.0 }, { 0.0 } };

		for (int i = 0; i < input.length; i++) {
			trainingSet.add(new BasicMLDataPair(new BasicMLData(input[i]),
					new BasicMLData(output[i])));
		}

		Propagation trainer = new ResilientPropagation(network, trainingSet);

		double error = 1000.0;
		int runs = 0;
		final int maxRuns = 200;
		while (error > MIN_DIFF && runs < maxRuns) {
			trainer.iteration();
			error = trainer.getError();
			runs++;
		}

		if (runs == maxRuns) {
			fail("Needed more than " + maxRuns + " runs. Error: " + error);
		} else {
			log.debug("Needed " + runs + " to learn.");
		}
	}
}
