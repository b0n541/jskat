
package org.jskat.util;


import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for class Card
 */
public class CardListTest extends AbstractJSkatTest {

    private static final Random RANDOM = new Random();

    /**
     * Test double sorting
     */
    @Test
    public void testSort001() {

        final CardList cards = new CardList();
        cards.add(Card.CA);
        cards.add(Card.HA);
        cards.add(Card.DA);
        cards.add(Card.HT);
        cards.add(Card.CJ);
        cards.add(Card.D7);

        // sort cards
        cards.sort(GameType.DIAMONDS);
        // check order
        assertThat(cards.get(0)).isEqualTo(Card.CJ);
        assertThat(cards.get(1)).isEqualTo(Card.DA);
        assertThat(cards.get(2)).isEqualTo(Card.D7);
        assertThat(cards.get(3)).isEqualTo(Card.CA);
        assertThat(cards.get(4)).isEqualTo(Card.HA);
        assertThat(cards.get(5)).isEqualTo(Card.HT);

        // sort cards again
        cards.sort(GameType.DIAMONDS);
        // check order
        assertThat(cards.get(0)).isEqualTo(Card.CJ);
        assertThat(cards.get(1)).isEqualTo(Card.DA);
        assertThat(cards.get(2)).isEqualTo(Card.D7);
        assertThat(cards.get(3)).isEqualTo(Card.CA);
        assertThat(cards.get(4)).isEqualTo(Card.HA);
        assertThat(cards.get(5)).isEqualTo(Card.HT);
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
