/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.test.util;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;

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
		cards.add(Card.CJ);
		cards.add(Card.CA);
		cards.add(Card.DA);

		// sort cards
		cards.sort(GameType.DIAMONDS);
		// check order
		assertTrue(cards.get(0) == Card.CJ);
		assertTrue(cards.get(1) == Card.DA);
		assertTrue(cards.get(2) == Card.CA);
		
		// sort cards again
		cards.sort(GameType.DIAMONDS);
		// check order
		assertTrue(cards.get(0) == Card.CJ);
		assertTrue(cards.get(1) == Card.DA);
		assertTrue(cards.get(2) == Card.CA);
	}
}
