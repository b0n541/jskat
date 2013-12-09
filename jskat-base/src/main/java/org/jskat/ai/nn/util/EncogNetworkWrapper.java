/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.PersistBasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

/**
 * Wraps the Encog network to fulfill the interface {@link INeuralNetwork}
 */
public class EncogNetworkWrapper implements INeuralNetwork {

	private BasicNetwork network;
	private final PersistBasicNetwork networkPersister;

	private final List<MLDataPair> dataPairList = new ArrayList<MLDataPair>();
	private final int MAX_SIZE = 10;
	private final int currentIndex = -1;

	/**
	 * Constructor
	 * 
	 * @param topo
	 *            Network topology
	 */
	public EncogNetworkWrapper(NetworkTopology topo, boolean useBias) {
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationSigmoid(), useBias, topo
				.getInputNeuronCount()));
		for (int i = 0; i < topo.getHiddenLayerCount(); i++) {
			network.addLayer(new BasicLayer(new ActivationSigmoid(), useBias,
					topo.getHiddenNeuronCount(i)));
		}
		network.addLayer(new BasicLayer(new ActivationSigmoid(), useBias, 1));
		network.getStructure().finalizeStructure();
		network.reset();

		networkPersister = new PersistBasicNetwork();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getAvgDiff() {
		return 0.0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized double adjustWeights(final double[] inputValues,
			final double[] outputValues) {

		// if (dataPairList.size() < MAX_SIZE) {
		// dataPairList.add(new BasicMLDataPair(new BasicMLData(inputValues),
		// new BasicMLData(outputValues)));
		// currentIndex++;
		// } else {
		// currentIndex = (currentIndex + 1) % MAX_SIZE;
		// dataPairList.set(currentIndex, new BasicMLDataPair(new BasicMLData(
		// inputValues), new BasicMLData(outputValues)));
		// }
		List<MLDataPair> data = new ArrayList<MLDataPair>();
		data.add(new BasicMLDataPair(new BasicMLData(inputValues),
				new BasicMLData(outputValues)));
		MLDataSet trainingSet = new BasicMLDataSet(data);

		// BasicTraining trainer = new ResilientPropagation(network,
		// trainingSet);
		BasicTraining trainer = new Backpropagation(network, trainingSet);
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
	public synchronized double getPredictedOutcome(final double[] inputValues) {
		MLData output = network.compute(new BasicMLData(inputValues));
		return output.getData(0);
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
	public synchronized void loadNetwork(final String fileName,
			final int inputNeurons, final int hiddenNeurons,
			final int outputNeurons) {
		network = (BasicNetwork) networkPersister.read(getClass()
				.getResourceAsStream(fileName));
	}
}
