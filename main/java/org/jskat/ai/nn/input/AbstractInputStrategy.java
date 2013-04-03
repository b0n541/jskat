package org.jskat.ai.nn.input;

public abstract class AbstractInputStrategy implements InputStrategy {

	protected double[] getEmptyInputs() {
		return new double[getNeuronCount()];
	}
}
