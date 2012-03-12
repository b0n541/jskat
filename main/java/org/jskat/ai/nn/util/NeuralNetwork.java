/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Neural network
 */
public class NeuralNetwork {

	private static Log log = LogFactory.getLog(NeuralNetwork.class);

	Random rand = new Random();

	private long iterations = 0;
	private double learningRate = 0.3;

	private List<Layer> layers = new ArrayList<Layer>();

	private NetworkTopology topo;

	/**
	 * Constructor
	 */
	public NeuralNetwork() {

		initializeVariables();
	}

	/**
	 * Constructor
	 * 
	 * @param newTopology
	 *            Topology of the network
	 */
	public NeuralNetwork(NetworkTopology newTopology) {

		topo = newTopology;
		initializeVariables();
	}

	private void initializeVariables() {

		iterations = 0;
		layers.clear();

		if (topo != null) {
			// Use topology object
			layers.add(new InputLayer(topo.getInputNeuronCount()));
			for (int i = 0; i < topo.getHiddenLayerCount(); i++) {
				layers.add(new HiddenLayer(topo.getHiddenNeuronCount(i)));
			}
			layers.add(new OutputLayer(topo.getOutputNeuronCount()));
		}
		connectAllLayers();
	}

	private void connectAllLayers() {

		for (int i = 0; i < layers.size() - 1; i++) {

			connectLayers(layers.get(i), layers.get(i + 1));
		}
	}

	private void connectLayers(Layer inputLayer, Layer outputLayer) {

		ArrayList<Double> weights = new ArrayList<Double>();

		for (int i = 0; i < inputLayer.getNeurons().size() * outputLayer.getNeurons().size(); i++) {

			weights.add(new Double(getLittleRandomWeightValue()));
		}

		connectLayers(inputLayer, outputLayer, weights);
	}

	private double getLittleRandomWeightValue() {
		return (rand.nextDouble() - 0.5) * 0.1;
	}

	private void connectLayers(Layer inputLayer, Layer outputLayer, ArrayList<Double> weights) {

		int weightIndex = 0;
		for (Neuron inputNeuron : inputLayer.getNeurons()) {
			for (Neuron outputNeuron : outputLayer.getNeurons()) {
				// create new weight with random weight value
				Weight newWeight = new Weight(inputNeuron, outputNeuron, weights.get(weightIndex).doubleValue());
				// Weight newWeight = new Weight(inputNeuron, outputNeuron, 0);
				// connect the weight with the neurons
				inputNeuron.addOutgoingWeight(newWeight);
				outputNeuron.addIncomingWeight(newWeight);
				weightIndex++;
			}
		}
	}

	/**
	 * Sets the input parameters
	 * 
	 * @param inputs
	 *            Input parameters
	 */
	private void setInputParameter(double[] inputs) {

		((InputLayer) layers.get(0)).setInputParameter(inputs);
	}

	/**
	 * Propagates the input parameters forward to the output layer
	 */
	private void propagateForward() {

		for (int i = 1; i < layers.size(); i++) {
			for (Neuron neuron : layers.get(i).getNeurons()) {
				neuron.calcActivationValue();
			}
		}
	}

	/**
	 * Sets the desired output parameter
	 * 
	 * @param outputs
	 *            Output parameter
	 */
	private void setOutputParameter(double[] outputs) {

		((OutputLayer) layers.get(layers.size() - 1)).setOuputParameter(outputs, learningRate);
	}

	/**
	 * Gets the average difference of all output neurons
	 * 
	 * @return Average difference
	 */
	public double getAvgDiff() {

		return ((OutputLayer) layers.get(layers.size() - 1)).getAvgDiff();
	}

	/**
	 * Propagates the error signal back to the input layer
	 */
	private void propagateBackward() {

		for (int i = layers.size() - 2; i > 0; i--) {
			for (Neuron neuron : layers.get(i).getNeurons()) {
				neuron.adjustWeights(learningRate);
			}
		}
	}

	/**
	 * Adjusts the weights of the net according inputs and desired outputs
	 * 
	 * @param inputs
	 *            Input attributes
	 * @param outputs
	 *            Output attributes
	 * @return Average diff
	 */
	public synchronized double adjustWeights(double[] inputs, double[] outputs) {

		setInputParameter(inputs);
		propagateForward();
		setOutputParameter(outputs);
		propagateBackward();

		iterations++;

		return getAvgDiff();
	}

	/**
	 * Resets the network, sets random values for all weights
	 */
	public synchronized void resetNetwork() {
		for (Layer layer : layers) {
			for (Neuron neuron : layer.getNeurons()) {
				for (Weight weight : neuron.incomingWeights) {
					weight.setWeightValue(getLittleRandomWeightValue());
				}
			}
		}
		iterations = 0;
	}

	/**
	 * Gets the predicted outcome of a game according inputs
	 * 
	 * @param inputs
	 *            Input attributes
	 * @return Predicted outcome
	 */
	public synchronized double getPredictedOutcome(double[] inputs) {

		setInputParameter(inputs);
		propagateForward();
		return getOutputValue(0);
	}

	/**
	 * Gets the number of iterations the NeuralNetwork was trained so far
	 * 
	 * @return Number of iterations
	 */
	public long getIterations() {

		return iterations;
	}

	/**
	 * Gets the value of an output node
	 * 
	 * @param outputNodeIndex
	 *            Index of the output node
	 * @return Value of the output node
	 */
	private double getOutputValue(int outputNodeIndex) {

		return layers.get(layers.size() - 1).getNeurons().get(outputNodeIndex).getActivationValue();
	}

	/**
	 * Save the network parameters to a file
	 * 
	 * @param fileName
	 *            File name to save to
	 * @return TRUE if the saving was successful
	 */
	public boolean saveNetwork(String fileName) {

		boolean result = false;

		FileWriter writer = null;

		try {

			writer = new FileWriter(fileName, false);
			writer.write(toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (writer != null) {
				try {
					writer.close();
					result = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	/**
	 * Loads network parameters from a file
	 * 
	 * @param fileName
	 *            File name to load from
	 * @param inputNeurons
	 *            Number of input neurons
	 * @param hiddenNeurons
	 *            Number of hidden neurons
	 * @param outputNeurons
	 *            Number of output neurons
	 */
	public void loadNetwork(String fileName, int inputNeurons, int hiddenNeurons, int outputNeurons) {

		BufferedReader reader = null;
		String input;
		ArrayList<String> lines = new ArrayList<String>();

		InputStream is = null;
		try {
			is = getClass().getResourceAsStream(fileName);
			reader = new BufferedReader(new InputStreamReader(is));

			while ((input = reader.readLine()) != null) {
				log.debug(input);
				lines.add(input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		log.debug(lines.size() + " lines read..."); //$NON-NLS-1$
		iterations = getIterations(lines);
		topo = getTopology(lines);

		// FIXME (jan 25.02.2012) this works only for one hidden layer
		if (topo.getInputNeuronCount() == inputNeurons && topo.getHiddenNeuronCount(0) == hiddenNeurons
				&& topo.getOutputNeuronCount() == outputNeurons) {
			layers = getLayers(topo, lines);
			log.debug(this);
		} else {
			log.warn("Loaded network from " + fileName + " has wrong topology. Resetting network to random weights."); //$NON-NLS-1$ //$NON-NLS-2$
			// FIXME (jan 25.02.2012) this is duplicated in
			// SkatNetworks.loadNetworks()
			int[] hiddenLayer = { hiddenNeurons };
			topo = new NetworkTopology(inputNeurons, outputNeurons, 1, hiddenLayer);
		}
	}

	private static long getIterations(ArrayList<String> netData) {

		return Long.parseLong(netData.get(1));
	}

	private static NetworkTopology getTopology(ArrayList<String> netData) {

		int inputs = 0;
		int outputs = 0;
		int hiddenLayerCount = 0;
		int[] hiddenNeuronCounts = null;

		Iterator<String> iter = netData.iterator();
		while (iter.hasNext()) {

			String currLine = iter.next();
			log.debug("Current line: " + currLine + " length: " + currLine.length());
			if (currLine.equals("iterations")) {
				// ignore next line
				iter.next();
			} else if (currLine.equals("input")) {
				log.debug("parsing input node count");
				inputs = Integer.parseInt(iter.next());
				log.debug(inputs + " input nodes");
			} else if (currLine.substring(0, 6).equals("hidden")) {
				log.debug("parsing hidden layer count");
				StringTokenizer tokens = new StringTokenizer(currLine);
				tokens.nextToken();
				hiddenLayerCount = Integer.parseInt(tokens.nextToken());
				log.debug(hiddenLayerCount + " hidden layers");
				hiddenNeuronCounts = new int[hiddenLayerCount];
				for (int i = 0; i < hiddenLayerCount; i++) {
					hiddenNeuronCounts[i] = Integer.parseInt(iter.next());
				}
			} else if (currLine.equals("output")) {
				log.debug("parsing output node count");
				outputs = Integer.parseInt(iter.next());
				log.debug(outputs + " output nodes");
			} else if (currLine.equals("weights")) {
				// TODO don't like breaks in while loops
				break;
			}
		}

		return new NetworkTopology(inputs, outputs, hiddenLayerCount, hiddenNeuronCounts);
	}

	private ArrayList<Layer> getLayers(NetworkTopology newTopo, ArrayList<String> netData) {

		ArrayList<Layer> newLayers = new ArrayList<Layer>();

		newLayers.add(new InputLayer(newTopo.getInputNeuronCount()));
		for (int i = 0; i < newTopo.getHiddenLayerCount(); i++) {
			newLayers.add(new HiddenLayer(newTopo.getHiddenNeuronCount(i)));
		}
		newLayers.add(new OutputLayer(newTopo.getOutputNeuronCount()));

		Iterator<String> iter = netData.iterator();
		while (iter.hasNext()) {

			String currLine = iter.next();
			log.debug("Current line: " + currLine + " length: " + currLine.length());
			if (currLine.equals("input layer")) {
				log.debug("parsing input layer weights");
				StringTokenizer tokens = new StringTokenizer(iter.next());
				ArrayList<Double> weights = new ArrayList<Double>();
				while (tokens.hasMoreTokens()) {
					weights.add(new Double(tokens.nextToken()));
				}
				connectLayers(newLayers.get(0), newLayers.get(1), weights);
			} else if (currLine.equals("hidden layer")) {
				// TODO implement it for more then one hiddel layer
				log.debug("parsing hidden layer weights");
				StringTokenizer tokens = new StringTokenizer(iter.next());
				ArrayList<Double> weights = new ArrayList<Double>();
				while (tokens.hasMoreTokens()) {
					weights.add(new Double(tokens.nextToken()));
				}
				connectLayers(newLayers.get(1), newLayers.get(2), weights);
			}
		}

		return newLayers;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();

		result.append("iterations\n"); //$NON-NLS-1$
		result.append(iterations);
		result.append('\n');

		result.append("topology\n"); //$NON-NLS-1$
		result.append(topo);

		result.append("weights\n"); //$NON-NLS-1$
		for (Layer layer : layers) {

			result.append(layer);
		}

		return result.toString();
	}
}
