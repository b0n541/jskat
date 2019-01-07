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
package org.jskat.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test cases for class PlayerKnowledge
 */
public class PlayerKnowledgeTest extends AbstractJSkatTest {

	private static Logger log = LoggerFactory
			.getLogger(PlayerKnowledgeTest.class);

	PlayerKnowledge knowledge;
	Set<Card> playerCards;
	Set<Card> otherCards;

	/**
	 * Setting up all variables
	 */
	@Before
	public void setUp() {

		knowledge = new PlayerKnowledge();

		knowledge.setPlayerPosition(Player.MIDDLEHAND);

		// Cards for the player
		playerCards = new HashSet<>();
		playerCards.add(Card.CA);
		playerCards.add(Card.CQ);
		playerCards.add(Card.C8);
		playerCards.add(Card.ST);
		playerCards.add(Card.SQ);
		playerCards.add(Card.DT);
		playerCards.add(Card.DK);
		playerCards.add(Card.D7);
		playerCards.add(Card.HJ);
		playerCards.add(Card.HA);

		// other cards
		otherCards = new HashSet<Card>();
		for (Card card : Card.values()) {
			if (!playerCards.contains(card)) {
				otherCards.add(card);
			}
		}
	}

	/**
	 * Test the player knowledge after fresh initialization
	 */
	@Test
	public void testInitialization() {

		assertTrue(knowledge.getOwnCards().isEmpty());
		assertEquals(0, knowledge.getTrumpCount());

		for (Card card : playerCards) {
			assertTrue(Player.FOREHAND + " could have card " + card, //$NON-NLS-1$
					knowledge.couldHaveCard(Player.FOREHAND, card));
			assertTrue(Player.MIDDLEHAND + " could have card " + card, //$NON-NLS-1$
					knowledge.couldHaveCard(Player.MIDDLEHAND, card));
			assertTrue(Player.REARHAND + " could have card " + card, //$NON-NLS-1$
					knowledge.couldHaveCard(Player.REARHAND, card));
			assertFalse("No certain information about Card " + card + " on " //$NON-NLS-1$ //$NON-NLS-2$
					+ Player.FOREHAND,
					knowledge.hasCard(Player.FOREHAND, card));
			assertFalse("No certain information about Card " + card + " on " //$NON-NLS-1$ //$NON-NLS-2$
					+ Player.MIDDLEHAND,
					knowledge.hasCard(Player.MIDDLEHAND, card));
			assertFalse("No certain information about Card " + card + " on " //$NON-NLS-1$ //$NON-NLS-2$
					+ Player.REARHAND,
					knowledge.hasCard(Player.REARHAND, card));
			assertTrue("Card " + card + " could lie in skat.", //$NON-NLS-1$//$NON-NLS-2$
					knowledge.couldLieInSkat(card));
		}
		for (Card card : otherCards) {
			assertTrue("Card " + card + " should be on " + Player.FOREHAND, //$NON-NLS-1$ //$NON-NLS-2$
					knowledge.couldHaveCard(Player.FOREHAND, card));
			assertTrue("Card " + card + " should be on " + Player.MIDDLEHAND, //$NON-NLS-1$ //$NON-NLS-2$
					knowledge.couldHaveCard(Player.MIDDLEHAND, card));
			assertTrue("Card " + card + " should be on " + Player.REARHAND, //$NON-NLS-1$ //$NON-NLS-2$
					knowledge.couldHaveCard(Player.REARHAND, card));
			assertFalse("No certain information about Card " + card + " on " //$NON-NLS-1$ //$NON-NLS-2$
					+ Player.FOREHAND,
					knowledge.hasCard(Player.FOREHAND, card));
			assertFalse("No certain information about Card " + card + " on " //$NON-NLS-1$ //$NON-NLS-2$
					+ Player.MIDDLEHAND,
					knowledge.hasCard(Player.MIDDLEHAND, card));
			assertFalse("No certain information about Card " + card + " on " //$NON-NLS-1$ //$NON-NLS-2$
					+ Player.REARHAND,
					knowledge.hasCard(Player.REARHAND, card));
			assertTrue("Card " + card + " could lie in skat.", //$NON-NLS-1$//$NON-NLS-2$
					knowledge.couldLieInSkat(card));
		}
	}

	/**
	 * Test player knowledge after dealing
	 */
	@Test
	public void testDealing() {

		dealPlayerCards();

		assertEquals(10, knowledge.getOwnCards().size());
		assertEquals(0, knowledge.getTrumpCount());

		for (Card card : playerCards) {
			assertFalse(Player.FOREHAND + " could not have card " + card, //$NON-NLS-1$
					knowledge.couldHaveCard(Player.FOREHAND, card));
			assertTrue(Player.MIDDLEHAND + " could have card " + card, //$NON-NLS-1$
					knowledge.couldHaveCard(Player.MIDDLEHAND, card));
			assertFalse(Player.REARHAND + " could not have card " + card, //$NON-NLS-1$
					knowledge.couldHaveCard(Player.REARHAND, card));
			assertFalse("No certain information about Card " + card + " on " //$NON-NLS-1$ //$NON-NLS-2$
					+ Player.FOREHAND,
					knowledge.hasCard(Player.FOREHAND, card));
			assertTrue(Player.MIDDLEHAND + " could have card " + card, //$NON-NLS-1$
					knowledge.hasCard(Player.MIDDLEHAND, card));
			assertFalse("No certain information about Card " + card + " on " //$NON-NLS-1$ //$NON-NLS-2$
					+ Player.REARHAND,
					knowledge.hasCard(Player.REARHAND, card));
			assertFalse("Card " + card + " could not lie in skat.", //$NON-NLS-1$//$NON-NLS-2$
					knowledge.couldLieInSkat(card));
		}
		for (Card card : otherCards) {
			assertCouldHaveCard(Player.FOREHAND, card);
			assertCouldNotHaveCard(Player.MIDDLEHAND, card);
			assertCouldHaveCard(Player.REARHAND, card);
			assertHasNotCard(Player.FOREHAND, card);
			assertHasNotCard(Player.MIDDLEHAND, card);
			assertHasNotCard(Player.REARHAND, card);
			assertTrue("Card " + card + " could lie in skat.", //$NON-NLS-1$//$NON-NLS-2$
					knowledge.couldLieInSkat(card));
		}
	}

	@Test
	public void testCardPlay_SuitGame() {

		dealPlayerCards();

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.CLUBS);
		knowledge.setGame(factory.getAnnouncement());
		knowledge.setDeclarer(Player.FOREHAND);

		knowledge.setNextTrick(0, Player.FOREHAND);

		Card foreHandCard = Card.SA;
		Card middleHandCard = Card.SQ;
		Card rearHandCard = Card.H7;

		// Fore hand plays
		assertCouldHaveCard(Player.FOREHAND, Card.SA);
		assertCouldNotHaveCard(Player.FOREHAND, Card.ST);
		assertCouldHaveCard(Player.FOREHAND, Card.SK);
		assertCouldNotHaveCard(Player.FOREHAND, Card.SQ);
		assertCouldHaveCard(Player.FOREHAND, Card.S9);
		assertCouldHaveCard(Player.FOREHAND, Card.S8);
		assertCouldHaveCard(Player.FOREHAND, Card.S7);

		knowledge.setCardPlayed(Player.FOREHAND, foreHandCard);

		assertCouldNotHaveCard(Player.FOREHAND, Card.SA);
		assertCouldNotHaveCard(Player.FOREHAND, Card.ST);
		assertCouldHaveCard(Player.FOREHAND, Card.SK);
		assertCouldNotHaveCard(Player.FOREHAND, Card.SQ);
		assertCouldHaveCard(Player.FOREHAND, Card.S9);
		assertCouldHaveCard(Player.FOREHAND, Card.S8);
		assertCouldHaveCard(Player.FOREHAND, Card.S7);

		assertCouldNotHaveCard(Player.MIDDLEHAND, foreHandCard);
		assertCouldNotHaveCard(Player.REARHAND, foreHandCard);

		// Middle hand plays
		assertCouldNotHaveCard(Player.MIDDLEHAND, Card.SA);
		assertCouldHaveCard(Player.MIDDLEHAND, Card.ST);
		assertCouldNotHaveCard(Player.MIDDLEHAND, Card.SK);
		assertCouldHaveCard(Player.MIDDLEHAND, Card.SQ);
		assertCouldNotHaveCard(Player.MIDDLEHAND, Card.S9);
		assertCouldNotHaveCard(Player.MIDDLEHAND, Card.S8);
		assertCouldNotHaveCard(Player.MIDDLEHAND, Card.S7);

		knowledge.setCardPlayed(Player.MIDDLEHAND, middleHandCard);

		assertCouldNotHaveCard(Player.FOREHAND, middleHandCard);
		assertCouldNotHaveCard(Player.MIDDLEHAND, middleHandCard);
		assertCouldNotHaveCard(Player.REARHAND, middleHandCard);

		// Rear hand plays
		assertCouldHaveCard(Player.REARHAND, Card.CJ);
		assertCouldHaveCard(Player.REARHAND, Card.SJ);
		assertCouldNotHaveCard(Player.REARHAND, Card.HJ);
		assertCouldHaveCard(Player.REARHAND, Card.DJ);
		assertCouldNotHaveCard(Player.REARHAND, Card.SA);
		assertCouldNotHaveCard(Player.REARHAND, Card.ST);
		assertCouldHaveCard(Player.REARHAND, Card.SK);
		assertCouldNotHaveCard(Player.REARHAND, Card.SQ);
		assertCouldHaveCard(Player.REARHAND, Card.S9);
		assertCouldHaveCard(Player.REARHAND, Card.S8);
		assertCouldHaveCard(Player.REARHAND, Card.S7);

		knowledge.setCardPlayed(Player.REARHAND, rearHandCard);

		assertCouldNotHaveCard(Player.FOREHAND, rearHandCard);
		assertCouldNotHaveCard(Player.MIDDLEHAND, rearHandCard);
		assertCouldNotHaveCard(Player.REARHAND, rearHandCard);

		// Rear hand has not followed suit
		assertCouldHaveCard(Player.REARHAND, Card.CJ);
		assertCouldHaveCard(Player.REARHAND, Card.SJ);
		assertCouldNotHaveCard(Player.REARHAND, Card.HJ);
		assertCouldHaveCard(Player.REARHAND, Card.DJ);
		assertCouldNotHaveCard(Player.REARHAND, Card.SA);
		assertCouldNotHaveCard(Player.REARHAND, Card.ST);
		assertCouldNotHaveCard(Player.REARHAND, Card.SK);
		assertCouldNotHaveCard(Player.REARHAND, Card.SQ);
		assertCouldNotHaveCard(Player.REARHAND, Card.S9);
		assertCouldNotHaveCard(Player.REARHAND, Card.S8);
		assertCouldNotHaveCard(Player.REARHAND, Card.S7);
	}

	private void dealPlayerCards() {

		// set up player cards
		knowledge.setPlayerPosition(Player.MIDDLEHAND);
		knowledge.addOwnCards(playerCards);
	}

	private void assertCouldHaveCard(final Player player, final Card card) {
		assertTrue(player + " could have card " + card, //$NON-NLS-1$
				knowledge.couldHaveCard(player, card));
	}

	private void assertCouldNotHaveCard(final Player player, final Card card) {
		assertFalse(player + " could not have card " + card, //$NON-NLS-1$
				knowledge.couldHaveCard(player, card));
	}

	private void assertHasCard(final Player player, final Card card) {
		assertTrue(player + " should have card " + card, //$NON-NLS-1$
				knowledge.hasCard(player, card));
	}

	private void assertHasNotCard(final Player player, final Card card) {
		assertFalse(player + " should not have card " + card, //$NON-NLS-1$
				knowledge.hasCard(player, card));
	}

	private void assertCouldLieInSkat(final Card card) {
		assertTrue(card + " could lie in skat", //$NON-NLS-1$
				knowledge.couldLieInSkat(card));
	}

	private void assertCouldNotLieInSkat(final Card card) {
		assertFalse(card + " could not lie in skat", //$NON-NLS-1$
				knowledge.couldLieInSkat(card));
	}

	/**
	 * Tests setting of tricks
	 */
	@Test
	public void testSetTrick() {

		knowledge.setNextTrick(0, Player.FOREHAND);

		assertEquals(0, knowledge.getCompletedTricks().size());

		knowledge.addCompletedTrick(new Trick(0, Player.FOREHAND));

		assertEquals(1, knowledge.getCompletedTricks().size());
	}
}
