package org.jskat.util;


import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test cases for class CardDeck
 */
public class CardDeckTest extends AbstractJSkatTest {

    /**
     * Checks method that returns all cards
     */
    @Test
    public void getAllCards001() {

        assertThat(CardDeck.getAllCards()).hasSize(32);
    }

    /**
     * Checks setting a card position to null
     */
    @Test
    public void setNullCard001() {

        final CardDeck simCards = new CardDeck();
        simCards.set(0, null);

        assertNull(simCards.get(0));
    }

    @Test
    public void addDoubleCard() {
        assertThrows(IllegalArgumentException.class, () -> {
            final CardDeck cards = new CardDeck();
            cards.remove(Card.CA);
            cards.add(Card.CJ);
        });
    }

    @Test
    public void addTooMuchCards() {
        assertThrows(IllegalStateException.class, () -> {
            final CardDeck cards = new CardDeck(
                    "CJ SJ HJ CK CQ SK C7 C8 S7 H7",
                    "D7 DJ CA CT C9 SQ HA HK HQ S8",
                    "H8 H9 HT SA ST S9 D8 D9 DT DA",
                    "DK DQ");
            cards.add(Card.CJ);
        });
    }
}
