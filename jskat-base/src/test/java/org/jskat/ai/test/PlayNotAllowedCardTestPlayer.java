
package org.jskat.ai.test;

import java.util.Random;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * Test player throws an excpetion during card play.
 */
public class PlayNotAllowedCardTestPlayer extends AIPlayerRND {

	/**
	 * Random generator.
	 */
	private final Random random = new Random();

	@Override
	public final Card playCard() {
		CardList notAllowedCards = new CardList(knowledge.getOwnCards());
		notAllowedCards.removeAll(getPlayableCards(knowledge.getTrickCards()));

		if (notAllowedCards.size() > 0) {
			return notAllowedCards.get(random.nextInt(notAllowedCards.size()));
		} else {
			// sometimes all cards are allowed
			return super.playCard();
		}
	}
}
