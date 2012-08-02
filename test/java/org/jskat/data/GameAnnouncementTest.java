package org.jskat.data;

import static org.junit.Assert.assertEquals;
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
		validAnnouncements.add(createNull());
		validAnnouncements.add(createNullHand());
		validAnnouncements.add(createNullOuvert());
		validAnnouncements.add(createNullHandOuvert());

		for (GameType suitGrand : Arrays.asList(GameType.GRAND, GameType.CLUBS,
				GameType.SPADES, GameType.HEARTS, GameType.DIAMONDS)) {
			validAnnouncements.add(createSuitGrand(suitGrand));
			validAnnouncements.add(createSuitGrandHand(suitGrand));
			validAnnouncements.add(createSuitGrandHandSchneider(suitGrand));
			validAnnouncements
					.add(createSuitGrandHandSchneiderSchwarz(suitGrand));
			validAnnouncements.add(createSuitGrandOuvert(suitGrand));
		}

		validAnnouncements.add(createRamsch());

		assertEquals(30, validAnnouncements.size());
	}

	private static GameAnnouncement createRamsch() {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.RAMSCH;
		return result;
	}

	private static GameAnnouncement createSuitGrand(GameType gameType) {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.discardedCards = VALID_DISCARDED_CARDS;
		return result;
	}

	private static GameAnnouncement createSuitGrandHand(GameType gameType) {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.hand = Boolean.TRUE;
		return result;
	}

	private static GameAnnouncement createSuitGrandHandSchneider(
			GameType gameType) {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.hand = Boolean.TRUE;
		result.schneider = Boolean.TRUE;
		return result;
	}

	private static GameAnnouncement createSuitGrandHandSchneiderSchwarz(
			GameType gameType) {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.hand = Boolean.TRUE;
		result.schneider = Boolean.TRUE;
		result.schwarz = Boolean.TRUE;
		return result;
	}

	private static GameAnnouncement createSuitGrandOuvert(GameType gameType) {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		result.hand = Boolean.TRUE;
		result.ouvert = Boolean.TRUE;
		result.schneider = Boolean.TRUE;
		result.schwarz = Boolean.TRUE;
		return result;
	}

	private static GameAnnouncement createNull() {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.discardedCards = VALID_DISCARDED_CARDS;
		return result;
	}

	private GameAnnouncement createNullHand() {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.hand = Boolean.TRUE;
		return result;
	}

	private GameAnnouncement createNullOuvert() {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.ouvert = Boolean.TRUE;
		result.discardedCards = VALID_DISCARDED_CARDS;
		return result;
	}

	private GameAnnouncement createNullHandOuvert() {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = GameType.NULL;
		result.hand = Boolean.TRUE;
		result.ouvert = Boolean.TRUE;
		return result;
	}

	private GameAnnouncement createSimpleGameAnnouncement(GameType gameType) {
		GameAnnouncement result = new GameAnnouncement();
		result.gameType = gameType;
		return result;
	}

	@Test
	public void testEmptyGameType() {
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		GameAnnouncement announcement = factory.getAnnouncement();
		assertNull(announcement);
	}

	@Test
	public void testAllAnnouncementPermutations() {

		for (GameType gameType : GameType.values()) {
			for (Boolean isHand : getAllBooleanExpressions()) {
				for (Boolean isOuvert : getAllBooleanExpressions()) {
					for (Boolean isSchneider : getAllBooleanExpressions()) {
						for (Boolean isSchwarz : getAllBooleanExpressions()) {
							for (CardList discardedCards : getAllDiscardedCardsExpressions()) {

								GameAnnouncement announcement = prepareAnnouncement(
										gameType, isHand, isOuvert,
										isSchneider, isSchwarz, discardedCards);

								testAnnouncement(announcement, isHand,
										isOuvert, isSchneider, isSchwarz,
										discardedCards);
							}
						}
					}
				}
			}
		}
	}

	private void testAnnouncement(GameAnnouncement announcement,
			Boolean isHand, Boolean isOuvert, Boolean isSchneider,
			Boolean isSchwarz, CardList discardedCards) {

		if (isValidAnnouncement(announcement)) {
			checkAnnouncement(announcement, isHand, isOuvert, isSchneider,
					isSchwarz, discardedCards);
		} else {
			assertNull("Invalid: " + announcement, //$NON-NLS-1$
					announcement);
		}
	}

	private GameAnnouncement prepareAnnouncement(GameType gameType,
			Boolean isHand, Boolean isOuvert, Boolean isSchneider,
			Boolean isSchwarz, CardList discardedCards) {
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(gameType);
		factory.setHand(isHand);
		factory.setOuvert(isOuvert);
		factory.setSchneider(isSchneider);
		factory.setSchwarz(isSchwarz);
		factory.setDiscardedCards(discardedCards);

		GameAnnouncement announcement = factory.getAnnouncement();
		return announcement;
	}

	private static void checkAnnouncement(GameAnnouncement announcement,
			Boolean isHand, Boolean isOuvert, Boolean isSchneider,
			Boolean isSchwarz, CardList discardedCards) {
		assertEquals(isHand, announcement.isHand());
		assertEquals(isOuvert, announcement.isOuvert());
		assertEquals(isSchneider, announcement.isSchneider());
		assertEquals(isSchwarz, announcement.isSchwarz());
		assertEquals(discardedCards, announcement.getDiscardedCards());
	}

	private static boolean isValidAnnouncement(GameAnnouncement announcement) {
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
