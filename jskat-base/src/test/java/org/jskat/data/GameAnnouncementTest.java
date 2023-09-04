package org.jskat.data;


import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link GameAnnouncement}
 */
public class GameAnnouncementTest extends AbstractJSkatTest {

    private static final CardList VALID_DISCARDED_CARDS = CardList.of(Card.CJ, Card.SJ);
    private static final CardList VALID_OUVERT_CARDS = CardList.of(
            Card.C7, Card.S7, Card.H7, Card.D7,
            Card.C8, Card.S8, Card.H8, Card.D8,
            Card.C9, Card.S9);

    private static final Set<GameAnnouncement> validAnnouncements = new HashSet<>();

    @BeforeAll
    public static void createAllValidGameAnnouncements() {
        validAnnouncements.add(createNullWithoutDiscardedCards());
        validAnnouncements.add(createNull());
        validAnnouncements.add(createNullHand());
        validAnnouncements.add(createNullOuvert());
        validAnnouncements.add(createNullOuvertWithoutDiscardedCards());
        validAnnouncements.add(createNullHandOuvert());

        for (final GameType suitGrand : List.of(GameType.GRAND, GameType.CLUBS, GameType.SPADES, GameType.HEARTS, GameType.DIAMONDS)) {
            validAnnouncements.add(createSuitGrandWithoutDiscardedCards(suitGrand));
            validAnnouncements.add(createSuitGrand(suitGrand));
            validAnnouncements.add(createSuitGrandHand(suitGrand));
            validAnnouncements.add(createSuitGrandHandSchneider(suitGrand));
            validAnnouncements.add(createSuitGrandHandSchneiderSchwarz(suitGrand));
            validAnnouncements.add(createSuitGrandOuvert(suitGrand));
        }

        validAnnouncements.add(createRamsch());
        validAnnouncements.add(createPassedIn());

        assertThat(validAnnouncements.size()).isEqualTo(38);
    }

    private static GameAnnouncement createRamsch() {
        return new GameAnnouncement(GameType.RAMSCH);
    }

    private static GameAnnouncement createPassedIn() {
        return new GameAnnouncement(GameType.PASSED_IN);
    }

    private static GameAnnouncement createSuitGrand(final GameType gameType) {
        final var result = new GameAnnouncement(gameType);
        builder.discardedCards = VALID_DISCARDED_CARDS;
        return result;
    }

    @Deprecated
    private static GameAnnouncement createSuitGrandWithoutDiscardedCards(final GameType gameType) {
        return new GameAnnouncement(gameType);
    }

    private static GameAnnouncement createSuitGrandHand(final GameType gameType) {
        return new GameAnnouncement(gameType, true);
    }

    private static GameAnnouncement createSuitGrandHandSchneider(final GameType gameType) {
        return new GameAnnouncement(gameType, true, true);
    }

    private static GameAnnouncement createSuitGrandHandSchneiderSchwarz(final GameType gameType) {
        return new GameAnnouncement(gameType, true, true, true);
    }

    private static GameAnnouncement createSuitGrandOuvert(final GameType gameType) {
        return new GameAnnouncement(gameType, true, true, true, true, VALID_OUVERT_CARDS);
    }

    @Deprecated
    private static GameAnnouncement createNullWithoutDiscardedCards() {
        return new GameAnnouncement(GameType.NULL);
    }

    private static GameAnnouncement createNull() {
        final GameAnnouncement result = new GameAnnouncement(GameType.NULL);
        result.discardedCards = VALID_DISCARDED_CARDS;
        return result;
    }

    private static GameAnnouncement createNullHand() {
        return new GameAnnouncement(GameType.NULL, true);
    }

    @Deprecated
    private static GameAnnouncement createNullOuvertWithoutDiscardedCards() {
        return new GameAnnouncement(GameType.NULL, true, VALID_OUVERT_CARDS);
    }

    private static GameAnnouncement createNullOuvert() {
        final GameAnnouncement result = new GameAnnouncement(GameType.NULL, true, VALID_OUVERT_CARDS);
        result.discardedCards = VALID_DISCARDED_CARDS;
        return result;
    }

    private static GameAnnouncement createNullHandOuvert() {
        return new GameAnnouncement(GameType.NULL, true, true, VALID_OUVERT_CARDS);
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
                    for (final CardList ouvertCards : getAllOuvertCardsExpressions()) {
                        for (final Boolean isSchneider : getAllBooleanExpressions()) {
                            for (final Boolean isSchwarz : getAllBooleanExpressions()) {
                                for (final CardList discardedCards : getAllDiscardedCardsExpressions()) {

                                    final GameAnnouncement announcement = prepareAnnouncement(
                                            gameType, isHand, isOuvert, ouvertCards,
                                            isSchneider, isSchwarz, discardedCards);

                                    testAnnouncement(announcement, gameType,
                                            isHand, isOuvert, ouvertCards, isSchneider,
                                            isSchwarz, discardedCards);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void testAnnouncement(final GameAnnouncement announcement,
                                         final GameType gameType,
                                         final boolean isHand,
                                         final boolean isOuvert,
                                         final CardList ouvertCards,
                                         final boolean isSchneider,
                                         final boolean isSchwarz,
                                         final CardList discardedCards) {

        if (isValidAnnouncement(announcement)) {
            checkAnnouncement(announcement, gameType, isHand, isOuvert, ouvertCards, isSchneider, isSchwarz, discardedCards);
        } else {
            // cross-check
            final GameAnnouncement ann = new GameAnnouncement(gameType, isHand, isSchneider, isSchwarz, isOuvert, ouvertCards);

            assertFalse(validAnnouncements.contains(ann));
            assertNull(announcement);
        }
    }

    private static GameAnnouncement prepareAnnouncement(final GameType gameType,
                                                        final boolean isHand,
                                                        final boolean isOuvert,
                                                        final CardList ouvertCards,
                                                        final boolean isSchneider,
                                                        final boolean isSchwarz,
                                                        final CardList discardedCards) {

        final var builder = GameAnnouncement.builder(gameType);
        if (isHand) {
            builder.hand();
        }
        if (isSchneider) {
            builder.schneider();
        }
        if (isSchwarz) {
            builder.schwarz();
        }
        if (isOuvert) {
            builder.ouvert(ouvertCards);
        }
        factory.setDiscardedCards(discardedCards);

        return builder.build();
    }

    private static void checkAnnouncement(final GameAnnouncement announcement,
                                          final GameType gameType,
                                          final boolean isHand,
                                          final boolean isOuvert,
                                          final CardList ouvertCards,
                                          final boolean isSchneider,
                                          final boolean isSchwarz,
                                          final CardList discardedCards) {
        assertThat(announcement.gameType()).isEqualTo(gameType);
        assertThat(announcement.hand()).isEqualTo(isHand);
        assertThat(announcement.ouvert()).isEqualTo(isOuvert);
        if (isOuvert) {
            assertThat(announcement.ouvertCards()).containsExactlyElementsOf(ouvertCards);
        } else {
            assertThat(announcement.ouvertCards()).isEmpty();
        }
        assertThat(announcement.schneider()).isEqualTo(isSchneider);
        assertThat(announcement.schwarz()).isEqualTo(isSchwarz);
        assertThat(announcement.discardedCards()).containsExactlyElementsOf(discardedCards);
    }

    private static boolean isValidAnnouncement(final GameAnnouncement announcement) {
        return validAnnouncements.contains(announcement);
    }

    private static List<CardList> getAllDiscardedCardsExpressions() {
        return Arrays.asList(
                new CardList(),
                CardList.of(Card.CJ),
                VALID_DISCARDED_CARDS,
                CardList.of(Card.CJ, Card.SJ, Card.HJ));
    }

    private static List<CardList> getAllOuvertCardsExpressions() {
        return Arrays.asList(
                new CardList(),
                VALID_OUVERT_CARDS,
                CardList.of(Card.values()));
    }

    private static List<Boolean> getAllBooleanExpressions() {
        return Arrays.asList(Boolean.TRUE, Boolean.FALSE);
    }
}
