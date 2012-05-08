package org.jskat.ai.nn.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.PersistBasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * Wraps the Encog network to fulfill the interface {@link INeuralNetwork}
 */
public class EncogNetworkWrapper implements INeuralNetwork {

	private BasicNetwork network;
	private final PersistBasicNetwork networkPersister;

	/**
	 * Constructor
	 * 
	 * @param newTopo
	 *            Network topology
	 */
	public EncogNetworkWrapper(final NetworkTopology newTopo) {
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, newTopo.getInputNeuronCount()));
		for (int i = 0; i < newTopo.getHiddenLayerCount(); i++) {
			network.addLayer(new BasicLayer(new ActivationSigmoid(), true, newTopo.getHiddenNeuronCount(i)));
		}
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		network.getStructure().finalizeStructure();
		network.reset();

		networkPersister = new PersistBasicNetwork();
	}

	@Override
	public double getAvgDiff() {

		return 0;
	}

	public double train(final double[][] inputs, final double[][] outputs) {
		BasicMLDataSet trainingSet = new BasicMLDataSet(inputs, outputs);
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		train.iteration((int) trainingSet.getRecordCount());
		return train.getError();
	}

	@Override
	public double adjustWeights(final double[] inputs, final double[] outputs) {
		BasicMLData input = new BasicMLData(inputs);
		BasicMLData output = new BasicMLData(outputs);
		MLDataPair dataPair = new BasicMLDataPair(input, output);
		BasicMLDataSet trainingSet = new BasicMLDataSet();
		trainingSet.add(dataPair);
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		train.iteration((int) trainingSet.getRecordCount());
		return train.getError();
	}

	@Override
	public void resetNetwork() {
		network.reset();
	}

	@Override
	public double getPredictedOutcome(final double[] inputs) {
		MLData inputData = new BasicMLData(inputs);
		MLData output = network.compute(inputData);
		return output.getData(0);
	}

	@Override
	public long getIterations() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveNetwork(final String fileName) {
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
	public void loadNetwork(final String fileName, final int inputNeurons, final int hiddenNeurons,
			final int outputNeurons) {
		network = (BasicNetwork) networkPersister.read(getClass().getResourceAsStream(fileName));
	}
}
