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
 * Test cases for class CardDeck
 */
public class CardDeckTest {

	/**
	 * Creates the logger
	 */
	@BeforeClass
	public static void createLogger() {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$
	}

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
