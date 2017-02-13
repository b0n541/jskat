/**
 * Copyright (C) 2017 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.PersistBasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.RPROPType;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.simple.EncogUtility;

/**
 * Wraps the Encog network to fulfill the interface {@link INeuralNetwork}
 */
public class EncogNetworkWrapper implements INeuralNetwork {

	private BasicNetwork network;
	private ResilientPropagation trainer;
	private final PersistBasicNetwork networkPersister;

	/**
	 * Constructor
	 *
	 * @param topo
	 *            Network topology
	 * @param useBias
	 *            TRUE, if bias nodes should be used
	 */
	public EncogNetworkWrapper(NetworkTopology topo, boolean useBias) {
		network = EncogUtility.simpleFeedForward(topo.getInputNeuronCount(), topo.getHiddenNeuronCount(0),
				topo.getHiddenNeuronCount(1), topo.getOutputNeuronCount(), false);

		networkPersister = new PersistBasicNetwork();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getAvgDiff() {
		return 0.0;
	}

	@Override
	public synchronized double adjustWeights(final double[] inputValues, final double[] outputValues) {

		MLDataSet trainingSet = new BasicMLDataSet(
				Arrays.asList(new BasicMLDataPair(new BasicMLData(inputValues), new BasicMLData(outputValues))));

		trainer = new ResilientPropagation(network, trainingSet);
		trainer.setRPROPType(RPROPType.iRPROPp);
		// trainer = new Backpropagation(network, trainingSet);
		trainer.setBatchSize(1);

		trainer.iteration();

		return trainer.getError();
	}

	@Override
	public synchronized double adjustWeightsBatch(final double[][] inputValues, final double[][] outputValues) {

		MLDataSet trainingSet = new BasicMLDataSet(inputValues, outputValues);

		trainer = new ResilientPropagation(network, trainingSet);
		trainer.setRPROPType(RPROPType.iRPROPp);
		// trainer = new Backpropagation(network, trainingSet);
		trainer.setBatchSize(0);

		trainer.iteration();

		return trainer.getError();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void resetNetwork() {
		network.reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized double[] getPredictedOutcome(final double[] inputValues) {
		MLData output = network.compute(new BasicMLData(inputValues));
		return output.getData();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getIterations() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean saveNetwork(final String fileName) {
		try {
			networkPersister.save(new FileOutputStream(fileName), network);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void loadNetwork(final String fileName) {
		network = (BasicNetwork) networkPersister.read(getClass().getResourceAsStream(fileName));
	}
}
