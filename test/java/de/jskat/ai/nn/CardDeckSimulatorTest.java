/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

 */

package de.jskat.ai.nn;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jskat.util.CardDeck;

/**
 * Tests for CardDeckSimulator
 */
public class CardDeckSimulatorTest {

	/**
	 * Checks simulation of unknown cards
	 */
	@Test
	public void simulateUnknownCards001() {

		CardDeck simCards = new CardDeck();

		simCards.set(0, null);
		simCards.set(8, null);
		simCards.set(16, null);
		simCards.set(24, null);
		CardDeckSimulator.simulateUnknownCards(simCards);

		assertFalse(simCards.get(0).equals(null)
				&& simCards.get(8).equals(null)
				&& simCards.get(16).equals(null)
				&& simCards.get(24).equals(null));
	}

	/**
	 * Checks simulation of unknown cards
	 */
	@Test
	public void simulateUnknownCards002() {

		CardDeck simCards = new CardDeck();

		simCards.set(0, null);
		simCards.set(1, null);
		simCards.set(2, null);
		simCards.set(3, null);
		simCards.set(4, null);
		simCards.set(5, null);
		simCards.set(6, null);
		simCards.set(7, null);
		CardDeckSimulator.simulateUnknownCards(simCards);

		assertTrue(simCards.get(0) != null && simCards.get(1) != null
				&& simCards.get(2) != null && simCards.get(3) != null
				&& simCards.get(4) != null && simCards.get(5) != null
				&& simCards.get(6) != null && simCards.get(7) != null);
	}

	/**
	 * Checks simulation of unknown cards
	 */
	@Test
	public void simulateUnknownCards003() {

		CardDeck simCards = new CardDeck();

		simCards.set(0, null);
		simCards.set(1, null);
		simCards.set(2, null);
		simCards.set(3, null);
		simCards.set(4, null);
		simCards.set(5, null);
		simCards.set(6, null);
		simCards.set(7, null);
		simCards.set(16, null);
		simCards.set(17, null);
		simCards.set(18, null);
		simCards.set(19, null);
		simCards.set(20, null);
		simCards.set(21, null);
		simCards.set(22, null);
		simCards.set(23, null);
		simCards.set(24, null);
		simCards.set(30, null);
		simCards.set(31, null);
		CardDeckSimulator.simulateUnknownCards(simCards);

		assertTrue(simCards.get(0) != null && simCards.get(1) != null
				&& simCards.get(2) != null && simCards.get(3) != null
				&& simCards.get(4) != null && simCards.get(5) != null
				&& simCards.get(6) != null && simCards.get(1) != null
				&& simCards.get(16) != null && simCards.get(18) != null
				&& simCards.get(19) != null && simCards.get(20) != null
				&& simCards.get(21) != null && simCards.get(22) != null
				&& simCards.get(23) != null && simCards.get(24) != null
				&& simCards.get(30) != null && simCards.get(31) != null);
	}
}
