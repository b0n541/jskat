/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-09
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
