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
 * Test cases for class Card
 */
public class SkatConstantsTest extends AbstractJSkatTest {

	/**
	 * Tests calculation of game values after tournament rules
	 */
	@Test
	public void getTournamentGameValue001() {

		assertTrue(SkatConstants.getTournamentGameValue(true, 18, 3) == 68);
		assertTrue(SkatConstants.getTournamentGameValue(true, 18, 4) == 68);
		assertTrue(SkatConstants.getTournamentGameValue(true, -36, 3) == -86);
		assertTrue(SkatConstants.getTournamentGameValue(true, -36, 4) == -86);
		assertTrue(SkatConstants.getTournamentGameValue(true, 20, 3) == 70);
		assertTrue(SkatConstants.getTournamentGameValue(true, 20, 4) == 70);
		assertTrue(SkatConstants.getTournamentGameValue(true, -40, 3) == -90);
		assertTrue(SkatConstants.getTournamentGameValue(true, -40, 4) == -90);
		assertTrue(SkatConstants.getTournamentGameValue(false, 18, 3) == 0);
		assertTrue(SkatConstants.getTournamentGameValue(false, 18, 4) == 0);
		assertTrue(SkatConstants.getTournamentGameValue(false, -36, 3) == 40);
		assertTrue(SkatConstants.getTournamentGameValue(false, -36, 4) == 30);
		assertTrue(SkatConstants.getTournamentGameValue(false, 20, 3) == 0);
		assertTrue(SkatConstants.getTournamentGameValue(false, 20, 4) == 0);
		assertTrue(SkatConstants.getTournamentGameValue(false, -40, 3) == 40);
		assertTrue(SkatConstants.getTournamentGameValue(false, -40, 4) == 30);
	}
}
