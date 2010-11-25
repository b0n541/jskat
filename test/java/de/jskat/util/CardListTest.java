/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for class Card
 */
public class CardListTest {

	/**
	 * Creates the logger
	 */
	@BeforeClass
	public static void createLogger() {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$
	}

	/**
	 * Test double sorting
	 */
	@Test
	public void testSort001() {

		CardList cards = new CardList();
		cards.add(Card.CA);
		cards.add(Card.HA);
		cards.add(Card.DA);
		cards.add(Card.HT);
		cards.add(Card.CJ);
		cards.add(Card.D7);

		// sort cards
		cards.sort(GameType.DIAMONDS);
		// check order
		assertTrue(cards.get(0) == Card.CJ);
		assertTrue(cards.get(1) == Card.DA);
		assertTrue(cards.get(2) == Card.D7);
		assertTrue(cards.get(3) == Card.CA);
		assertTrue(cards.get(4) == Card.HA);
		assertTrue(cards.get(5) == Card.HT);

		// sort cards again
		cards.sort(GameType.DIAMONDS);
		// check order
		assertTrue(cards.get(0) == Card.CJ);
		assertTrue(cards.get(1) == Card.DA);
		assertTrue(cards.get(2) == Card.D7);
		assertTrue(cards.get(3) == Card.CA);
		assertTrue(cards.get(4) == Card.HA);
		assertTrue(cards.get(5) == Card.HT);
	}
}
