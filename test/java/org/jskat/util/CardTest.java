/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.junit.Test;


/**
 * Test cases for class Card
 */
public class CardTest extends AbstractJSkatTest {

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
