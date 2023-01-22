package org.jskat.util;


import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for class Card
 */
public class CardListTest extends AbstractJSkatTest {
    private static Stream<Arguments> testCasesForSort() {
        return Stream.of(
                Arguments.of(
                        GameType.GRAND,
                        CardList.of(Card.D7, Card.DA, Card.H7, Card.HA, Card.S7, Card.SA, Card.C7, Card.CA, Card.DJ, Card.CJ),
                        CardList.of(Card.CJ, Card.DJ, Card.CA, Card.C7, Card.SA, Card.S7, Card.HA, Card.H7, Card.DA, Card.D7)),
                Arguments.of(
                        GameType.CLUBS,
                        CardList.of(Card.D7, Card.DA, Card.H7, Card.HA, Card.S7, Card.SA, Card.C7, Card.CA, Card.DJ, Card.CJ),
                        CardList.of(Card.CJ, Card.DJ, Card.CA, Card.C7, Card.SA, Card.S7, Card.HA, Card.H7, Card.DA, Card.D7)),
                Arguments.of(
                        GameType.SPADES,
                        CardList.of(Card.D7, Card.DA, Card.H7, Card.HA, Card.S7, Card.SA, Card.C7, Card.CA, Card.DJ, Card.CJ),
                        CardList.of(Card.CJ, Card.DJ, Card.SA, Card.S7, Card.CA, Card.C7, Card.HA, Card.H7, Card.DA, Card.D7)),
                Arguments.of(
                        GameType.HEARTS,
                        CardList.of(Card.D7, Card.DA, Card.H7, Card.HA, Card.S7, Card.SA, Card.C7, Card.CA, Card.DJ, Card.CJ),
                        CardList.of(Card.CJ, Card.DJ, Card.HA, Card.H7, Card.CA, Card.C7, Card.SA, Card.S7, Card.DA, Card.D7)),
                Arguments.of(
                        GameType.DIAMONDS,
                        CardList.of(Card.D7, Card.DA, Card.H7, Card.HA, Card.S7, Card.SA, Card.C7, Card.CA, Card.DJ, Card.CJ),
                        CardList.of(Card.CJ, Card.DJ, Card.DA, Card.D7, Card.CA, Card.C7, Card.SA, Card.S7, Card.HA, Card.H7)),
                Arguments.of(
                        GameType.NULL,
                        CardList.of(Card.CJ, Card.SJ, Card.D7, Card.DT, Card.DJ, Card.DK, Card.DA, Card.HA, Card.SA, Card.CA),
                        CardList.of(Card.CA, Card.CJ, Card.SA, Card.SJ, Card.HA, Card.DA, Card.DK, Card.DJ, Card.DT, Card.D7)),
                Arguments.of(
                        GameType.RAMSCH,
                        CardList.of(Card.D7, Card.DA, Card.H7, Card.HA, Card.S7, Card.SA, Card.C7, Card.CA, Card.DJ, Card.CJ),
                        CardList.of(Card.CJ, Card.DJ, Card.CA, Card.C7, Card.SA, Card.S7, Card.HA, Card.H7, Card.DA, Card.D7))
        );
    }

    @ParameterizedTest
    @MethodSource("testCasesForSort")
    public void sort(GameType gameType, CardList unsortedCards, CardList expectedSortedCardList) {

        unsortedCards.sort(gameType);

        assertThat(unsortedCards).containsExactlyElementsOf(expectedSortedCardList);
    }

    @Test
    public void sortTwoTimes() {
        CardList cards = CardList.getPerfectGrandSuitHand();

        cards.sort(GameType.GRAND);

        assertThat(cards).containsExactly(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.SA, Card.ST, Card.HA, Card.DA);

        cards.sort(GameType.GRAND);

        assertThat(cards).containsExactly(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.SA, Card.ST, Card.HA, Card.DA);
    }

    /**
     * Test card finding
     */
    @Test
    public void testGetFirstIndexOfSuit() {

        final CardList cards = new CardList();

        cards.add(Card.CJ);
        cards.add(Card.CA);

        assertThat(cards.getFirstIndexOfSuit(Suit.CLUBS)).isEqualTo(0);
        assertThat(cards.getFirstIndexOfSuit(Suit.CLUBS, true)).isEqualTo(0);
        assertThat(cards.getFirstIndexOfSuit(Suit.CLUBS, false)).isEqualTo(1);
        assertThat(cards.getFirstIndexOfSuit(Suit.HEARTS)).isEqualTo(-1);
    }

    /**
     * Test card finding
     */
    @Test
    public void testGetLastIndexOfSuit() {

        final CardList cards = new CardList();

        cards.add(Card.CA);
        cards.add(Card.CJ);

        assertThat(cards.getLastIndexOfSuit(Suit.CLUBS)).isEqualTo(1);
        assertThat(cards.getLastIndexOfSuit(Suit.CLUBS, true)).isEqualTo(1);
        assertThat(cards.getLastIndexOfSuit(Suit.CLUBS, false)).isEqualTo(0);
        assertThat(cards.getLastIndexOfSuit(Suit.HEARTS)).isEqualTo(-1);
    }

    @Test
    public void testPerfectGrandSuitHand() {
        final CardList cards = CardList.getPerfectGrandSuitHand();

        assertThat(cards).containsExactlyInAnyOrder(
                Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA,
                Card.SA, Card.HA, Card.DA, Card.CT, Card.ST);
    }

    @Test
    public void testRandomCards() {
        final CardList cards = CardList.getRandomCards(10);
        assertThat(cards).hasSize(10);
    }
}
