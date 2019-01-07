/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.util;

import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

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

	@Test(expected = IllegalArgumentException.class)
	public void addDoubleCard() {
		CardDeck cards = new CardDeck();
		cards.remove(Card.CA);
		cards.add(Card.CJ);
	}

	@Test(expected = IllegalStateException.class)
	public void addTooMuchCards() {
		CardDeck cards = new CardDeck(
				"CJ SJ HJ CK CQ SK C7 C8 S7 H7 D7 DJ CA CT C9 SQ HA HK HQ S8 H8 H9 HT SA ST S9 D8 D9 DT DA DK DQ");
		cards.add(Card.CJ);
	}
}
