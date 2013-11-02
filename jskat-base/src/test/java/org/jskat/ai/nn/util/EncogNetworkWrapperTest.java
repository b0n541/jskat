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
	 * Maximum iterations for network learning
	 */
	private static final int MAX_ITERATIONS = 500;
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory
			.getLogger(EncogNetworkWrapperTest.class);

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
		int iteration = 0;

		while (error > MIN_DIFF && iteration < MAX_ITERATIONS) {
			network.adjustWeights(input[i], output[i]);
			error = network.getAvgDiff();
			i = (i + 1) % input.length;
			iteration++;
		}

		if (iteration == MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations. Error: "
					+ error);
		} else {
			log.debug("Needed " + iteration + " iterations to learn.");
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
		int iteration = 0;
		while (error > MIN_DIFF && iteration < MAX_ITERATIONS) {
			trainer.iteration();
			error = trainer.getError();
			iteration++;
		}

		if (iteration == MAX_ITERATIONS) {
			fail("Needed more than " + MAX_ITERATIONS + " iterations. Error: "
					+ error);
		} else {
			log.debug("Needed " + iteration + " iterations to learn.");
		}
	}
}
