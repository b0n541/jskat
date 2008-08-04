package jskat.player.AIPlayerNN.nn;

public class NetworkTopology {

	public NetworkTopology(int inputs, int outputs, int hiddenLayers, int[] hiddenNeurons) {
		
		if (hiddenNeurons.length != hiddenLayers) {
			
			throw new IllegalArgumentException("Number of hidden layers and number of hidden neurons don't correspond.");
		}
		
		inputSignals = inputs;
		outputSignals = outputs;
		this.hiddenLayers = hiddenLayers;
		this.hiddenNeurons = new int[this.hiddenLayers];
		this.hiddenNeurons = hiddenNeurons;
	}

	public int getInputNeuronCount() {
		return inputSignals;
	}

	public int getHiddenLayerCount() {
		return hiddenLayers;
	}

	public int getHiddenNeuronCount(int i) {
		return hiddenNeurons[i];
	}

	public int getOutputNeuronCount() {
		return outputSignals;
	}

	private int inputSignals;
	private int outputSignals;
	private int hiddenLayers;
	private int[] hiddenNeurons;
}
