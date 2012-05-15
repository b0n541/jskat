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
	private final ResilientPropagation trainer;

	private final MLData inputs;
	private final MLData outputs;

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

		inputs = new BasicMLData(newTopo.getInputNeuronCount());
		outputs = new BasicMLData(newTopo.getOutputNeuronCount());
		MLDataPair dataPair = new BasicMLDataPair(inputs, outputs);
		BasicMLDataSet trainingSet = new BasicMLDataSet();
		trainingSet.add(dataPair);
		trainer = new ResilientPropagation(network, trainingSet);
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
	public double adjustWeights(final double[] inputValues, final double[] outputValues) {
		for (int i = 0; i < inputValues.length; i++) {
			inputs.setData(i, inputValues[i]);
		}
		for (int i = 0; i < outputValues.length; i++) {
			outputs.setData(i, outputValues[i]);
		}
		trainer.iteration(1);
		return trainer.getError();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetNetwork() {
		network.reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getPredictedOutcome(final double[] inputValues) {
		for (int i = 0; i < inputValues.length; i++) {
			inputs.setData(i, inputValues[i]);
		}
		MLData output = network.compute(inputs);
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
