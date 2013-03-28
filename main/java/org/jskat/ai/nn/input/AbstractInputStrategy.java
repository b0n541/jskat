package org.jskat.ai.nn.input;

public abstract class AbstractInputStrategy implements InputStrategy {

	double[] result = new double[getNeuronCount()];
}
