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
package org.jskat.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link GameAnnouncement}
 */
public class GameAnnouncementTest extends AbstractJSkatTest {

	private static final CardList VALID_DISCARDED_CARDS = new CardList(Card.CJ,
			Card.SJ);

	private static Set<GameAnnouncement> validAnnouncements = new HashSet<GameAnnouncement>();

	@Before
	public void createAllValidGameAnnouncements() {
		validAnnouncements.add(createNullWithoutDiscardedCards());
		validAnnouncements.add(createNull());
		validAnnouncements.add(createNullHand());
		validAnnouncements.add(createNullOuvert());
		validAnnouncements.add(createNullOuvertWithoutDiscardedCards());
		validAnnouncements.add(createNullHandOuvert());

		for (final GameType suitGrand : Arrays.asList(GameType.GRAND,
				GameType.CLUBS, GameType.SPADES, GameType.HEARTS,
				GameType.DIAMONDS)) {
			validAnnouncements
					.add(createSuitGrandWithoutDiscardedCards(suitGrand));
			validAnnouncements.add(createSuitGrand(suitGrand));
			validAnnouncements.add(createSuitGrandHand(suitGrand));
			validAnnouncements.add(createSuitGrandHandSchneider(suitGrand));
			validAnnouncements
					.add(createSuitGrandHandSchneiderSchwarz(suitGrand));
			validAnnouncements.add(createSuitGrandOuvert(suitGrand));
		}

		validAnnouncements.add(createRamsch());
		validAnnouncements.add(createPassedIn());

		assertEquals(38, validAnnouncements.size());
	}

	private static GameAnnouncement createRamsch() {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.RAMSCH;
		return result;
	}

	private static GameAnnouncement createPassedIn() {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.PASSED_IN;
		return result;
	}

	private static GameAnnouncement createSuitGrand(final GameType gameType) {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.discardedCards = VALID_DISCARDED_CARDS;
		return result;
	}

	@Deprecated
	private static GameAnnouncement createSuitGrandWithoutDiscardedCards(
			final GameType gameType) {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		return result;
	}

	private static GameAnnouncement createSuitGrandHand(final GameType gameType) {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.hand = Boolean.TRUE;
		return result;
	}

	private static GameAnnouncement createSuitGrandHandSchneider(
			final GameType gameType) {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.hand = Boolean.TRUE;
		result.schneider = Boolean.TRUE;
		return result;
	}

	private static GameAnnouncement createSuitGrandHandSchneiderSchwarz(
			final GameType gameType) {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.hand = Boolean.TRUE;
		result.schneider = Boolean.TRUE;
		result.schwarz = Boolean.TRUE;
		return result;
	}

	private static GameAnnouncement createSuitGrandOuvert(
			final GameType gameType) {
		GameAnnouncement.getFactory();
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.hand = Boolean.TRUE;
		result.ouvert = Boolean.TRUE;
		result.schneider = Boolean.TRUE;
		result.schwarz = Boolean.TRUE;
		return result;
	}

	@Deprecated
	private static GameAnnouncement createNullWithoutDiscardedCards() {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		return result;
	}

	private static GameAnnouncement createNull() {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.discardedCards = VALID_DISCARDED_CARDS;
		return result;
	}

	private GameAnnouncement createNullHand() {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.hand = Boolean.TRUE;
		return result;
	}

	@Deprecated
	private GameAnnouncement createNullOuvertWithoutDiscardedCards() {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.ouvert = Boolean.TRUE;
		return result;
	}

	private GameAnnouncement createNullOuvert() {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.ouvert = Boolean.TRUE;
		result.discardedCards = VALID_DISCARDED_CARDS;
		return result;
	}

	private GameAnnouncement createNullHandOuvert() {
		final GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.hand = Boolean.TRUE;
		result.ouvert = Boolean.TRUE;
		return result;
	}

	@Test
	public void testEmptyGameType() {
		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		final GameAnnouncement announcement = factory.getAnnouncement();
		assertNull(announcement);
	}

	@Test
	public void testAllAnnouncementPermutations() {

		for (final GameType gameType : GameType.values()) {
			for (final Boolean isHand : getAllBooleanExpressions()) {
				for (final Boolean isOuvert : getAllBooleanExpressions()) {
					for (final Boolean isSchneider : getAllBooleanExpressions()) {
						for (final Boolean isSchwarz : getAllBooleanExpressions()) {
							for (final CardList discardedCards : getAllDiscardedCardsExpressions()) {

								final GameAnnouncement announcement = prepareAnnouncement(
										gameType, isHand, isOuvert,
										isSchneider, isSchwarz, discardedCards);

								testAnnouncement(announcement, gameType,
										isHand, isOuvert, isSchneider,
										isSchwarz, discardedCards);
							}
						}
					}
				}
			}
		}
	}

	private void testAnnouncement(final GameAnnouncement announcement,
			final GameType gameType, final Boolean isHand,
			final Boolean isOuvert, final Boolean isSchneider,
			final Boolean isSchwarz, final CardList discardedCards) {

		if (isValidAnnouncement(announcement)) {
			checkAnnouncement(announcement, gameType, isHand, isOuvert,
					isSchneider, isSchwarz, discardedCards);
		} else {
			// cross check
			final GameAnnouncement ann = new GameAnnouncement();
			ann.gameType = gameType;
			ann.hand = isHand;
			ann.ouvert = isOuvert;
			ann.schneider = isSchneider;
			ann.schwarz = isSchwarz;
			ann.discardedCards = discardedCards;

			assertFalse(
					"Not in valid set or created as null object: " + ann, validAnnouncements.contains(ann)); //$NON-NLS-1$

			assertNull("Wrong created announcement: " + announcement, //$NON-NLS-1$
					announcement);
		}
	}

	private GameAnnouncement prepareAnnouncement(final GameType gameType,
			final Boolean isHand, final Boolean isOuvert,
			final Boolean isSchneider, final Boolean isSchwarz,
			final CardList discardedCards) {

		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(gameType);
		factory.setHand(isHand);
		factory.setOuvert(isOuvert);
		factory.setSchneider(isSchneider);
		factory.setSchwarz(isSchwarz);
		factory.setDiscardedCards(discardedCards);

		final GameAnnouncement announcement = factory.getAnnouncement();
		return announcement;
	}

	private static void checkAnnouncement(final GameAnnouncement announcement,
			final GameType gameType, final Boolean isHand,
			final Boolean isOuvert, final Boolean isSchneider,
			final Boolean isSchwarz, final CardList discardedCards) {
		assertEquals(gameType, announcement.gameType);
		assertEquals(isHand, announcement.hand);
		assertEquals(isOuvert, announcement.ouvert);
		assertEquals(isSchneider, announcement.schneider);
		assertEquals(isSchwarz, announcement.schwarz);
		assertEquals(discardedCards, announcement.discardedCards);
	}

	private static boolean isValidAnnouncement(
			final GameAnnouncement announcement) {
		return validAnnouncements.contains(announcement);
	}

	private static List<CardList> getAllDiscardedCardsExpressions() {
		return Arrays.asList(new CardList(),
				new CardList(Arrays.asList(Card.CJ)), VALID_DISCARDED_CARDS,
				new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ)));
	}

	private static List<Boolean> getAllBooleanExpressions() {
		return Arrays.asList(Boolean.TRUE, Boolean.FALSE);
	}
}
