package org.jskat.ai.nn.util;

/**
 * All activation functions supported by the Neuron
 */
public enum ActivationFunction {
	/**
	 * Sigmoid function (quasi standard)
	 */
	SIGMOID {
		@Override
		public double calcForward(double input) {
			return sigmoid(input);
		}

		@Override
		public double calcBackward(double input) {
			return dsigmoid(input);
		}
	},
	/**
	 * Tangens hyperbolicus function
	 */
	TANH {
		@Override
		public double calcForward(double input) {
			return tanh(input);
		}

		@Override
		public double calcBackward(double input) {
			return dtanh(input);
		}
	};

	/**
	 * Forward calculation
	 * 
	 * @param input
	 *            Input value
	 * @return Result
	 */
	public abstract double calcForward(double input);

	/**
	 * Backward calculation
	 * 
	 * @param input
	 *            Input value
	 * @return Result
	 */
	public abstract double calcBackward(double input);

	private static final double sigmoid(double input) {
		return 1.0d / (1.0d + Math.exp(-1.0d * input));
	}

	private static final double dsigmoid(double input) {
		return sigmoid(input) * (1.0d - sigmoid(input));
	}

	private static final double tanh(double input) {
		return Math.tanh(input);
	}

	private static final double dtanh(double input) {
		return 1.0d - tanh(input) * tanh(input);
	}
}
