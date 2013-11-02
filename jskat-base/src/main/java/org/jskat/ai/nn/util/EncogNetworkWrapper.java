/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
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
