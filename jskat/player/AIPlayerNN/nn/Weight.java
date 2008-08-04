package jskat.player.AIPlayerNN.nn;

/**
 * Weight between two neurons
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class Weight {

	/**
	 * Constructor
	 * 
	 * @param inputNeuron Input neuron
	 * @param outputNeuron Output neuron
	 * @param weight Weight value
	 */
	public Weight(Neuron inputNeuron, Neuron outputNeuron, double weight) {
		
		this.inputNeuron = inputNeuron;
		this.outputNeuron = outputNeuron;
		this.weightValue = weight;
	}
	
	/**
	 * Gets the weight value
	 * 
	 * @return The weight value
	 */
	public double getWeightValue() {
		return weightValue;
	}

	/**
	 * Sets the weight value
	 * 
	 * @param weightValue The weight value to set
	 */
	public void setWeightValue(double weightValue) {
		this.weightValue = weightValue;
	}
	
	/**
	 * Gets the input neuron
	 * 
	 * @return Input neuron
	 */
	public Neuron getInputNeuron() {
		
		return inputNeuron;
	}
	
	/**
	 * Gets the output neuron
	 * 
	 * @return Output neuron
	 */
	public Neuron getOutputNeuron() {
		
		return outputNeuron;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "_" + weightValue + "_";
	}
	
	private double weightValue;
	private Neuron inputNeuron;
	private Neuron outputNeuron;
}
