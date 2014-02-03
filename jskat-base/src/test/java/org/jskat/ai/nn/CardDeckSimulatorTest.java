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
package org.jskat.ai.nn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.Test;

/**
 * Tests for CardDeckSimulator
 */
public class CardDeckSimulatorTest extends AbstractJSkatTest {

	/**
	 * Checks simulation of unknown cards<br />
	 * all cards are unknown
	 */
	@Test
	public void simulateUnknownCards_AllCards() {

		CardList cards = new CardList();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.FOREHAND, cards);

		assertEquals(32, simCards.size());
	}

	/**
	 * Checks simulation of unknown cards<br />
	 * Fore hand cards are known
	 */
	@Test
	public void simulateUnknownCards_ForeHandKnown() {

		CardList cards = getKnownHandCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.FOREHAND, cards);

		assertEquals(32, simCards.size());
		assertTrue(cards.contains(simCards.get(0)));
		assertTrue(cards.contains(simCards.get(1)));
		assertTrue(cards.contains(simCards.get(2)));
		assertTrue(cards.contains(simCards.get(11)));
		assertTrue(cards.contains(simCards.get(12)));
		assertTrue(cards.contains(simCards.get(13)));
		assertTrue(cards.contains(simCards.get(14)));
		assertTrue(cards.contains(simCards.get(23)));
		assertTrue(cards.contains(simCards.get(24)));
		assertTrue(cards.contains(simCards.get(25)));
	}

	/**
	 * Checks simulation of unknown cards<br />
	 * Fore hand cards and skat are known
	 */
	@Test
	public void simulateUnknownCards_ForeHandAndSkatKnown() {

		CardList cards = getKnownHandCards();
		CardList skat = getKnownSkatCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.FOREHAND, cards, skat);

		assertEquals(32, simCards.size());
		assertTrue(cards.contains(simCards.get(0)));
		assertTrue(cards.contains(simCards.get(1)));
		assertTrue(cards.contains(simCards.get(2)));
		assertTrue(skat.contains(simCards.get(9)));
		assertTrue(skat.contains(simCards.get(10)));
		assertTrue(cards.contains(simCards.get(11)));
		assertTrue(cards.contains(simCards.get(12)));
		assertTrue(cards.contains(simCards.get(13)));
		assertTrue(cards.contains(simCards.get(14)));
		assertTrue(cards.contains(simCards.get(23)));
		assertTrue(cards.contains(simCards.get(24)));
		assertTrue(cards.contains(simCards.get(25)));
	}

	/**
	 * Checks simulation of unknown cards<br />
	 * Middle hand cards are known
	 */
	@Test
	public void simulateUnknownCards_MiddleHandKnown() {

		CardList cards = getKnownHandCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.MIDDLEHAND, cards);

		assertEquals(32, simCards.size());
		assertTrue(cards.contains(simCards.get(3)));
		assertTrue(cards.contains(simCards.get(4)));
		assertTrue(cards.contains(simCards.get(5)));
		assertTrue(cards.contains(simCards.get(15)));
		assertTrue(cards.contains(simCards.get(16)));
		assertTrue(cards.contains(simCards.get(17)));
		assertTrue(cards.contains(simCards.get(18)));
		assertTrue(cards.contains(simCards.get(26)));
		assertTrue(cards.contains(simCards.get(27)));
		assertTrue(cards.contains(simCards.get(28)));
	}

	/**
	 * Checks simulation of unknown cards<br />
	 * Middle hand cards and skat are known
	 */
	@Test
	public void simulateUnknownCards_MiddleHandAndSkatKnown() {

		CardList cards = getKnownHandCards();
		CardList skat = getKnownSkatCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.MIDDLEHAND, cards, skat);

		assertEquals(32, simCards.size());
		assertTrue(cards.contains(simCards.get(3)));
		assertTrue(cards.contains(simCards.get(4)));
		assertTrue(cards.contains(simCards.get(5)));
		assertTrue(skat.contains(simCards.get(9)));
		assertTrue(skat.contains(simCards.get(10)));
		assertTrue(cards.contains(simCards.get(15)));
		assertTrue(cards.contains(simCards.get(16)));
		assertTrue(cards.contains(simCards.get(17)));
		assertTrue(cards.contains(simCards.get(18)));
		assertTrue(cards.contains(simCards.get(26)));
		assertTrue(cards.contains(simCards.get(27)));
		assertTrue(cards.contains(simCards.get(28)));
	}

	/**
	 * Checks simulation of unknown cards<br />
	 * Rearhand cards are known
	 */
	@Test
	public void simulateUnknownCards_RearHandKnown() {

		CardList cards = getKnownHandCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.REARHAND, cards);

		assertEquals(32, simCards.size());
		assertTrue(cards.contains(simCards.get(6)));
		assertTrue(cards.contains(simCards.get(7)));
		assertTrue(cards.contains(simCards.get(8)));
		assertTrue(cards.contains(simCards.get(19)));
		assertTrue(cards.contains(simCards.get(20)));
		assertTrue(cards.contains(simCards.get(21)));
		assertTrue(cards.contains(simCards.get(22)));
		assertTrue(cards.contains(simCards.get(29)));
		assertTrue(cards.contains(simCards.get(30)));
		assertTrue(cards.contains(simCards.get(31)));
	}

	/**
	 * Checks simulation of unknown cards<br />
	 * Rearhand cards and skat are known
	 */
	@Test
	public void simulateUnknownCards_RearHandAndSkatKnown() {

		CardList cards = getKnownHandCards();
		CardList skat = getKnownSkatCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.REARHAND, cards, skat);

		assertEquals(32, simCards.size());
		assertTrue(cards.contains(simCards.get(6)));
		assertTrue(cards.contains(simCards.get(7)));
		assertTrue(cards.contains(simCards.get(8)));
		assertTrue(skat.contains(simCards.get(9)));
		assertTrue(skat.contains(simCards.get(10)));
		assertTrue(cards.contains(simCards.get(19)));
		assertTrue(cards.contains(simCards.get(20)));
		assertTrue(cards.contains(simCards.get(21)));
		assertTrue(cards.contains(simCards.get(22)));
		assertTrue(cards.contains(simCards.get(29)));
		assertTrue(cards.contains(simCards.get(30)));
		assertTrue(cards.contains(simCards.get(31)));
	}

	private CardList getKnownHandCards() {
		return new CardList(Card.CJ, Card.SJ, Card.HJ, Card.SJ, Card.CA,
				Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8);
	}

	private CardList getKnownSkatCards() {
		return new CardList(Card.C7, Card.S7);
	}
}
