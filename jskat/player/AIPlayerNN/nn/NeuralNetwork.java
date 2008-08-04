package jskat.player.AIPlayerNN.nn;

import java.util.Random;
import java.util.Vector;

/**
 * Neural network
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class NeuralNetwork {

	/**
	 * Constructor
	 */
	public NeuralNetwork() {
		
		initializeVariables();
	}
	
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
		else {
			// Standard layout
			layers.add(new InputLayer(24));
			layers.add(new HiddenLayer(10));
			layers.add(new OutputLayer(1));
		}
		connectAllLayers();
	}
	
	private void connectAllLayers() {
		
		for (int i = 0; i < layers.size() - 1; i++) {
			
			connectLayers(layers.get(i), layers.get(i + 1));
		}
	}
	
	private void connectLayers(Layer inputLayer, Layer outputLayer) {
		
		Random rand = new Random();
		
		for (Neuron inputNeuron : inputLayer.getNeurons()) {
			for (Neuron outputNeuron : outputLayer.getNeurons()) {
				// create new weight with random weight value
				Weight newWeight = new Weight(inputNeuron, outputNeuron, rand.nextGaussian() * 0.5);
//				Weight newWeight = new Weight(inputNeuron, outputNeuron, 0);
				// connect the weight with the neurons
				inputNeuron.addOutgoingWeight(newWeight);
				outputNeuron.addIncomingWeight(newWeight);
			}
		}
	}
	
	/**
	 * Sets the input parameters
	 * 
	 * @param inputs Input parameters
	 */
	public void setInputParameter(double[] inputs) {
		
		((InputLayer) layers.get(0)).setInputParameter(inputs);
	}
	
	/**
	 * Propagates the input parameters forward to the output layer 
	 */
	public void propagateForward() {
		
		for (int i = 1; i < layers.size(); i++) {
			for (Neuron neuron : layers.get(i).getNeurons()) {
				neuron.calcActivationValue();
			}
		}
	}

	/**
	 * Sets the desired output parameter
	 * 
	 * @param outputs Output parameter
	 */
	public void setOutputParameter(double[] outputs) {
		
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
	public void propagateBackward() {
		
		for (int i = layers.size() - 2; i > 0; i--) {
			for (Neuron neuron : layers.get(i).getNeurons()) {
				neuron.adjustWeights(learningRate);
			}
		}
		
		iterations++;
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
	 * @see Object#toString()
	 */
	public String toString() {
		
		StringBuffer result = new StringBuffer();
		
		result.append("iterations: " + iterations).append('\n');
		
		for (Layer layer : layers) {
			
			result.append(layer).append("\n\n");
		}
		
		return result.toString();
	}
	
	/**
	 * Gets a string representation of the current input parameters
	 * 
	 * @return Input parameters as String
	 */
	public String getInputParameters() {
		
		return getNeuronActivationValues(layers.firstElement().getNeurons());
	}
	
	/**
	 * Gets a string representation of the current output parameters
	 * 
	 * @return Output parameters as String
	 */
	public String getOutputParameters() {
		
		return getNeuronActivationValues(layers.lastElement().getNeurons());
	}
	
	private String getNeuronActivationValues(Vector<Neuron> neurons) {
		
		StringBuffer result = new StringBuffer();
		
		for (Neuron neuron : neurons) {
			result.append(neuron.getActivationValue()).append(' ');
		}
		
		return result.toString();
	}
	
	private long iterations = 0;
	private double learningRate = 0.3;
	
	private Vector<Layer> layers = new Vector<Layer>();
	
	private NetworkTopology topo;
}
