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

public class EncogNetworkWrapper implements INeuralNetwork {

	private BasicNetwork network;
	private final PersistBasicNetwork networkPersister;

	public EncogNetworkWrapper(NetworkTopology newTopo) {
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

	public double train(double[][] inputs, double[][] outputs) {
		BasicMLDataSet trainingSet = new BasicMLDataSet(inputs, outputs);
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		train.iteration((int) trainingSet.getRecordCount());
		return train.getError();
	}

	@Override
	public double adjustWeights(double[] inputs, double[] outputs) {
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
	public double getPredictedOutcome(double[] inputs) {
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
	public boolean saveNetwork(String fileName) {
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
	public void loadNetwork(String fileName, int inputNeurons, int hiddenNeurons, int outputNeurons) {
		network = (BasicNetwork) networkPersister.read(getClass().getResourceAsStream(fileName));
	}
}
