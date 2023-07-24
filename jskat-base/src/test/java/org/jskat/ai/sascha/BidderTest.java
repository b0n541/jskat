package org.jskat.ai.sascha;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.algorithmic.BidEvaluatorTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BidderTest extends AbstractJSkatTest {
    private static final Logger log = LoggerFactory.getLogger(BidEvaluatorTest.class);

    @Test
    public void testMediocoreHand() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
                Card.HT, Card.H8, Card.D9));

        Bidder cut = new Bidder(cards, 0, 0);
        assertThat(cut.isGrand()).isFalse();
        assertThat(cut.getGameValue()).isEqualTo(24);
    }

    @Test
    public void testStrongGrand() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.CA, Card.CT, Card.HA, Card.HK,
                Card.ST, Card.SK, Card.SQ));

        Bidder cut = new Bidder(cards, 0, 0);
        assertThat(cut.isGrand()).isTrue();
        assertThat(cut.getGameValue()).isEqualTo(96);
    }

    @Test
    public void testGarbageHand() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
                Card.HT, Card.H8, Card.D9, Card.D8));

        Bidder cut = new Bidder(cards, 0, 0);
        assertThat(cut.getGameValue()).isEqualTo(0);

        cut = new Bidder(cards, 0, 6);
        assertThat(cut.getGameValue()).isEqualTo(24);
    }

    @Test
    public void testClubsGame() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SA,
                Card.HT, Card.H8, Card.D9));

        Bidder cut = new Bidder(cards, 0, 0);
        assertThat(cut.isGrand()).isFalse();
        assertThat(cut.getGameValue()).isEqualTo(24);
    }

    @Test
    public void testDiscard() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SA,
                Card.HT, Card.H8, Card.D9, Card.DA, Card.H7 ));

        Bidder cut = new Bidder(cards, 0, 0);
        assertThat(cut.getGameValue()).isEqualTo(24);
        assertThat(cut.getCardsToDiscard()).contains(Card.HT);
    }
}
