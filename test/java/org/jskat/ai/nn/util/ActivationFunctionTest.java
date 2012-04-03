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
