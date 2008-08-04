package jskat.player.AIPlayerNN.nn;

import java.util.Vector;

import jskat.player.AIPlayerNN.nn.Neuron.ActivationFunction;

/**
 * Input Layer
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class OutputLayer extends Layer {

	/**
	 * Constructor
	 * 
	 * @param numberOfNeurons 
	 */
	public OutputLayer(int numberOfNeurons) {
		
		super(numberOfNeurons);
		
		// Cast all output neurons to use their extra functionality
		outNeurons = new Vector<OutputNeuron>();
		for (Neuron neuron : neurons) {
			outNeurons.add((OutputNeuron) neuron);
		}
	}

	/**
	 * @see Layer#createNeuron(ActivationFunction)
	 */
	@Override
	protected Neuron createNeuron(ActivationFunction activFnct) {
		
		return new OutputNeuron(activFnct);
	}

	/**
	 * Sets all output parameter and calculates the error signal 
	 * of the output layer
	 * 
	 * @param outputs Output parameter
	 * @param learningRate Learning rate
	 */
	public void setOuputParameter(double[] outputs, double learningRate) {
		
		if (outputs.length > neurons.size()) {
			throw new IllegalArgumentException("Wrong number of output values.");
		}
		else {
			for (int i = 0; i < outputs.length; i++) {
				outNeurons.get(i).calculateError(outputs[i], learningRate);
			}
		}
	}
	
	/**
	 * Gets the average difference of all neurons
	 * 
	 * @return Average difference
	 */
	public double getAvgDiff() {
		
		double diffSum = 0.0;
		
		for (OutputNeuron neuron : outNeurons) {
			
			diffSum += neuron.getDiff();
		}
		
		return diffSum / neurons.size();
	}
	
	private Vector<OutputNeuron> outNeurons;
}
