package jskat.player.AIPlayerNN.nn;

import java.util.Vector;

import jskat.player.AIPlayerNN.nn.Neuron.ActivationFunction;

/**
 * Layer of Neurons for the NeuralNetwork
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public abstract class Layer {

	/**
	 * Constructor
	 * 
	 * @param numberOfNeurons Number of Neurons
	 */
	public Layer(int numberOfNeurons) {
		
		// create neurons
		for (int i = 0; i < numberOfNeurons; i++) {
			neurons.add(createNeuron(ActivationFunction.TANH));
		}
	}
	
	/**
	 * Creates a neuron for the layer
	 * 
	 * @param activFnct Activation function
	 * @return Neuron
	 */
	protected abstract Neuron createNeuron(ActivationFunction activFnct);
	
	/**
	 * Gets all Neurons of the Layer
	 * 
	 * @return All Neurons of the Layer
	 */
	public Vector<Neuron> getNeurons() {
		
		return neurons;
	}
	
	/**
	 * @see Object#toString() 
	 */
	public String toString() {
		
		StringBuffer neuronStrings = new StringBuffer();
		StringBuffer inputWeightStrings = new StringBuffer();
		StringBuffer outputWeightStrings = new StringBuffer();
		
		inputWeightStrings.append("input weights: \n");
		neuronStrings.append("Neurons: ");
		outputWeightStrings.append("output weights: \n");
		
		for (Neuron neuron : neurons) {
			
			inputWeightStrings.append(neuron.getInputWeightString()).append("][");
			neuronStrings.append(neuron).append(' ');
			outputWeightStrings.append(neuron.getOutputWeightString()).append("][");
		}
		
		return inputWeightStrings.toString() + '\n' +
				neuronStrings.toString() + '\n' +
				outputWeightStrings.toString();
	}
	
	/**
	 * All Neurons of the Layer
	 */
	protected Vector<Neuron> neurons = new Vector<Neuron>();
}
