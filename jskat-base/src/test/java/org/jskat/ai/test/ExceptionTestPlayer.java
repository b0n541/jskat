
package org.jskat.ai.test;

import java.util.Random;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.util.Card;

/**
 * Test player throws an excpetion during card play
 */
public class ExceptionTestPlayer extends AIPlayerRND {

	private final Random random = new Random();
	private final int trickNoWhereExceptionIsThrown;

	public ExceptionTestPlayer() {
		trickNoWhereExceptionIsThrown = random.nextInt(10);
	}

	@Override
	public Card playCard() {
		if (knowledge.getCurrentTrick().getTrickNumberInGame() == trickNoWhereExceptionIsThrown) {
			double exception = 42 / 0;
		}
		return super.playCard();
	}
}
