/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
