/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jskat.AbstractJSkatTest;

/**
 * Test cases for class CardDeck
 */
public class CardDeckTest extends AbstractJSkatTest {

	/**
	 * Checks method that returns all cards
	 */
	@Test
	public void getAllCards001() {

		assertTrue(CardDeck.getAllCards().size() == 32);
	}

	/**
	 * Checks setting a card position to null
	 */
	@Test
	public void setNullCard001() {

		CardDeck simCards = new CardDeck();
		simCards.set(0, null);

		assertTrue(simCards.get(0) == null);
	}
}
