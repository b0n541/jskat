/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

/**
 * Test cases for class Card
 */
public class CardListTest extends AbstractJSkatTest {

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

	/**
	 * Test card finding
	 */
	@Test
	public void testGetFirstIndexOfSuit() {

		CardList cards = new CardList();

		cards.add(Card.CJ);
		cards.add(Card.CA);

		assertEquals(0, cards.getFirstIndexOfSuit(Suit.CLUBS));
		assertEquals(0, cards.getFirstIndexOfSuit(Suit.CLUBS, true));
		assertEquals(1, cards.getFirstIndexOfSuit(Suit.CLUBS, false));
		assertEquals(-1, cards.getFirstIndexOfSuit(Suit.HEARTS));
	}

	/**
	 * Test card finding
	 */
	@Test
	public void testGetLastIndexOfSuit() {

		CardList cards = new CardList();

		cards.add(Card.CA);
		cards.add(Card.CJ);

		assertEquals(1, cards.getLastIndexOfSuit(Suit.CLUBS));
		assertEquals(1, cards.getLastIndexOfSuit(Suit.CLUBS, true));
		assertEquals(0, cards.getLastIndexOfSuit(Suit.CLUBS, false));
		assertEquals(-1, cards.getLastIndexOfSuit(Suit.HEARTS));
	}
}
