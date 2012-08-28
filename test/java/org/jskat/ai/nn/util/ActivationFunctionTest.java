/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ActivationFunctionTest {
	@Test
	public void testSigmoidFunctionForward() {
		assertEquals(1.0, ActivationFunction.SIGMOID.calcForward(Double.MAX_VALUE), 0.0);
	}

	@Test
	public void testSigmoidFunctionForward2() {
		assertEquals(0.5, ActivationFunction.SIGMOID.calcForward(0.0), 0.0);
	}

	@Test
	public void testSigmoidFunctionForward3() {
		assertEquals(0.0, ActivationFunction.SIGMOID.calcForward(Double.MAX_VALUE * -1), 0.0);
	}
}
