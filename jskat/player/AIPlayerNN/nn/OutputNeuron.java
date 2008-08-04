package jskat.player.AIPlayerNN.nn;

/**
 * Output neuron
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class OutputNeuron extends Neuron {

	/**
	 * @see Neuron#Neuron(ActivationFunction)
	 */
	public OutputNeuron(ActivationFunction activFunction) {
		super(activFunction);
	}

	/**
	 * Calculates the error signal of an output node
	 * 
	 * @param targetValue
	 * @param learningRate
	 */
	public void calculateError(double targetValue, double learningRate) {
			
			// first calculate error for output neuron
			diff = (targetValue - activationValue);
			errorSignal = (targetValue - activationValue) * dactivFnct(inputSum);
			// adjust all weights leading to this neuron
			for (Weight weight : incomingWeights) {
				weight.setWeightValue(weight.getWeightValue() +
										learningRate * errorSignal *
										weight.getInputNeuron().getActivationValue());
			}
		}

	/**
	 * Gets the real difference between calculated output and desired value
	 * 
	 * @return Difference between output and desired value
	 */
	public double getDiff() {
		
		return diff;
	}

	private double diff = 0.0;
}
