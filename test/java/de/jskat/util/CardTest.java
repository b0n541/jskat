/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for class Card
 */
public class CardTest {

	/**
	 * Creates the logger
	 */
	@BeforeClass
	public static void createLogger() {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$
	}

	/**
	 * Checks for reflexivity
	 */
	@Test
	public void equals001() {

		assertTrue(Card.CJ.equals(Card.CJ));
	}

	/**
	 * Checks for symetry
	 */
	@Test
	public void equals002() {

		assertTrue(Card.CJ.equals(Card.CJ) == Card.CJ.equals(Card.CJ));
	}

	/**
	 * Checks for symetry
	 */
	@Test
	public void equals003() {

		assertTrue(Card.CJ.equals(Card.D7) == Card.D7.equals(Card.CJ));
	}

	/**
	 * Checks for null reference
	 */
	@Test
	public void equals007() {

		assertFalse(Card.CJ.equals(null));
	}

	/**
	 * Checks @see Card#isTrump(GameType)
	 */
	@Test
	public void isTrump001() {
		assertTrue(Card.HJ.isTrump(GameType.HEARTS));
	}

	/**
	 * Checks @see Card#isTrump(GameType)
	 */
	@Test
	public void isTrump002() {
		assertTrue(Card.HJ.isTrump(GameType.DIAMONDS));
	}

	/**
	 * Checks @see Card#isTrump(GameType)
	 */
	@Test
	public void isTrump003() {
		assertTrue(Card.DJ.isTrump(GameType.HEARTS));
	}

	/**
	 * Checks @see Card#isTrump(GameType)
	 */
	@Test
	public void isTrump004() {
		assertFalse(Card.HT.isTrump(GameType.DIAMONDS));
	}
}
