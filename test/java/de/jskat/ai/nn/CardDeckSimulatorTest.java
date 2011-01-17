/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

 */

package de.jskat.ai.nn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jskat.util.Card;
import de.jskat.util.CardDeck;
import de.jskat.util.CardList;
import de.jskat.util.Player;

/**
 * Tests for CardDeckSimulator
 */
public class CardDeckSimulatorTest {

	/**
	 * Checks simulation of unknown cards<br />
	 * all cards are unknown
	 */
	@Test
	public void simulateUnknownCards_AllCards() {

		CardList cards = new CardList();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.FORE_HAND, cards);

		assertEquals(32, simCards.size());
	}

	/**
	 * Checks simulation of unknown cards<br />
	 * Fore hand cards are known
	 */
	@Test
	public void simulateUnknownCards_ForeHandKnown() {

		CardList cards = getKnownCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.FORE_HAND, cards);

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
	 * Middle hand cards are known
	 */
	@Test
	public void simulateUnknownCards_MiddleHandKnown() {

		CardList cards = getKnownCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.MIDDLE_HAND, cards);

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
	 * Hind hand cards are known
	 */
	@Test
	public void simulateUnknownCards_HindHandKnown() {

		CardList cards = getKnownCards();

		CardDeck simCards = CardDeckSimulator.simulateUnknownCards(
				Player.HIND_HAND, cards);

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

	private CardList getKnownCards() {
		CardList cards = new CardList();
		cards.add(Card.CJ);
		cards.add(Card.SJ);
		cards.add(Card.HJ);
		cards.add(Card.SJ);
		cards.add(Card.CA);
		cards.add(Card.CT);
		cards.add(Card.CK);
		cards.add(Card.CQ);
		cards.add(Card.C9);
		cards.add(Card.C8);
		return cards;
	}
}
