package jskat.player.AIPlayerNN.nn;

import jskat.player.AIPlayerNN.nn.Neuron.ActivationFunction;

/**
 * Input Layer
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class InputLayer extends Layer {

	/**
	 * Constructor
	 * 
	 * @param numberOfNeurons 
	 */
	public InputLayer(int numberOfNeurons) {
		
		super(numberOfNeurons);
	}
	
	/**
	 * @see Layer#createNeuron(ActivationFunction)
	 */
	@Override
	protected Neuron createNeuron(ActivationFunction activFnct) {

		return new Neuron(activFnct);
	}

	/**
	 * Sets the input parameter
	 * 
	 * @param inputs
	 */
	public void setInputParameter(double[] inputs) {
		
		if (inputs.length > neurons.size()) {
			throw new IllegalArgumentException("Wrong number of input values.");
		}
		else {
			for (int i = 0; i < inputs.length; i++) {
				neurons.get(i).setActivationValue(inputs[i]);
			}
		}
	}
}
