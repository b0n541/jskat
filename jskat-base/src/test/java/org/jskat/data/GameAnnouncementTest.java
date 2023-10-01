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

/**
 * Tests for {@link GameContract}
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
        validAnnouncements.add(createNull());
        validAnnouncements.add(createNullHand());
        validAnnouncements.add(createNullOuvert());
        validAnnouncements.add(createNullHandOuvert());

        for (final GameType suitGrand : GameType.GRAND_SUIT) {
            validAnnouncements.add(createSuitGrand(suitGrand));
            validAnnouncements.add(createSuitGrandHand(suitGrand));
            validAnnouncements.add(createSuitGrandHandSchneider(suitGrand));
            validAnnouncements.add(createSuitGrandHandSchneiderSchwarz(suitGrand));
            validAnnouncements.add(createSuitGrandOuvert(suitGrand));
        }

        validAnnouncements.add(createRamsch());
        validAnnouncements.add(createPassedIn());

        assertThat(validAnnouncements.size()).isEqualTo(31);
    }

    private static GameAnnouncement createRamsch() {
        return new GameAnnouncement(new GameContract(GameType.RAMSCH));
    }

    private static GameAnnouncement createPassedIn() {
        return new GameAnnouncement(new GameContract(GameType.PASSED_IN));
    }

    private static GameAnnouncement createSuitGrand(final GameType gameType) {
        return new GameAnnouncement(new GameContract(gameType), VALID_DISCARDED_CARDS);
    }

    private static GameAnnouncement createSuitGrandHand(final GameType gameType) {
        return new GameAnnouncement(new GameContract(gameType, true));
    }

    private static GameAnnouncement createSuitGrandHandSchneider(final GameType gameType) {
        return new GameAnnouncement(new GameContract(gameType, true, false));
    }

    private static GameAnnouncement createSuitGrandHandSchneiderSchwarz(final GameType gameType) {
        return new GameAnnouncement(new GameContract(gameType, true, true));
    }

    private static GameAnnouncement createSuitGrandOuvert(final GameType gameType) {
        return new GameAnnouncement(new GameContract(gameType, true, VALID_OUVERT_CARDS));
    }

    private static GameAnnouncement createNull() {
        return new GameAnnouncement(new GameContract(GameType.NULL), VALID_DISCARDED_CARDS);
    }

    private static GameAnnouncement createNullHand() {
        return new GameAnnouncement(new GameContract(GameType.NULL, true));
    }

    private static GameAnnouncement createNullOuvert() {
        return new GameAnnouncement(new GameContract(GameType.NULL, true, VALID_OUVERT_CARDS), VALID_DISCARDED_CARDS);
    }

    private static GameAnnouncement createNullHandOuvert() {
        return new GameAnnouncement(new GameContract(GameType.NULL, true, false, false, true, VALID_OUVERT_CARDS));
    }

    @Test
    public void handGameWithoutDiscardedCards() {
        assertThat(new GameAnnouncement(new GameContract(GameType.GRAND, true)))
                .extracting("discardedCards")
                .isEqualTo(CardList.empty());
    }

    @Test
    public void testAllAnnouncementPermutations() {

        for (final GameType gameType : GameType.values()) {
            for (final Boolean hand : getAllBooleanExpressions()) {
                for (final Boolean schneider : getAllBooleanExpressions()) {
                    for (final Boolean schwarz : getAllBooleanExpressions()) {
                        for (final Boolean ouvert : getAllBooleanExpressions()) {
                            for (final CardList ouvertCards : getAllOuvertCardsExpressions()) {
                                for (final CardList discardedCards : getAllDiscardedCardsExpressions()) {

                                    try {
                                        System.out.println("Testing arguments: "
                                                + gameType + " "
                                                + hand + " "
                                                + schneider + " "
                                                + schwarz + " "
                                                + ouvert + " "
                                                + ouvertCards + " "
                                                + discardedCards);

                                        final GameAnnouncement announcement = new GameAnnouncement(
                                                new GameContract(
                                                        gameType,
                                                        hand,
                                                        schneider,
                                                        schwarz,
                                                        ouvert,
                                                        ouvertCards),
                                                discardedCards);

                                        assertThat(validAnnouncements).contains(announcement);

                                        checkAnnouncement(
                                                announcement,
                                                gameType,
                                                hand,
                                                schneider,
                                                schwarz,
                                                ouvert,
                                                ouvertCards,
                                                discardedCards);
                                    } catch (final IllegalArgumentException exception) {
                                        System.out.println("Invalid arguments: "
                                                + gameType + " "
                                                + hand + " "
                                                + schneider + " "
                                                + schwarz + " "
                                                + ouvert + " "
                                                + ouvertCards + " "
                                                + discardedCards);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void checkAnnouncement(final GameAnnouncement announcement,
                                          final GameType gameType,
                                          final boolean hand,
                                          final boolean schneider,
                                          final boolean schwarz,
                                          final boolean ouvert,
                                          final CardList ouvertCards,
                                          final CardList discardedCards) {
        assertThat(announcement.contract().gameType()).isEqualTo(gameType);
        assertThat(announcement.contract().hand()).isEqualTo(hand);
        assertThat(announcement.contract().schneider()).isEqualTo(schneider);
        assertThat(announcement.contract().schwarz()).isEqualTo(schwarz);
        assertThat(announcement.contract().ouvert()).isEqualTo(ouvert);
        if (ouvert) {
            assertThat(announcement.contract().ouvertCards()).containsExactlyElementsOf(ouvertCards);
        } else {
            assertThat(announcement.contract().ouvertCards()).isEmpty();
        }
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
