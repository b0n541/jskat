/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.util;

import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

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
