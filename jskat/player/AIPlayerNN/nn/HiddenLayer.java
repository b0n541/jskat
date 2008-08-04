package jskat.player.AIPlayerNN.nn;

import jskat.player.AIPlayerNN.nn.Neuron.ActivationFunction;

/**
 * Hidden layer for NeuralNetwork
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class HiddenLayer extends Layer {

	/**
	 * @see Layer#Layer(int)
	 */
	public HiddenLayer(int numberOfNeurons) {
		
		super(numberOfNeurons);
	}

	/**
	 * @see Layer#createNeuron(ActivationFunction)
	 */
	@Override
	public Neuron createNeuron(ActivationFunction activFnct) {
		
		return new Neuron(activFnct);
	}
}
