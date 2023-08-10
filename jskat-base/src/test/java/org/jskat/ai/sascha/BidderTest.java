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
import org.jskat.util.GameType;
import org.jskat.util.Player;

public class BidderTest extends AbstractJSkatTest {
    private static final Logger log = LoggerFactory.getLogger(BidEvaluatorTest.class);

    @Test
    public void testMediocoreHand() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
                Card.HT, Card.H8, Card.D9));

        Bidder cut = new Bidder(cards, Player.MIDDLEHAND);
        assertThat(cut.isGrand()).isFalse();
        assertThat(cut.getGameValue()).isEqualTo(24);
    }

    @Test
    public void testBidGrand() {
        CardList cards = new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.CA, Card.CT, Card.HA, Card.HK,
                Card.ST, Card.SK, Card.SQ));

        Bidder cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.isGrand()).isTrue();
        assertThat(cut.getGameValue()).isEqualTo(96);

        cards = new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.DJ, Card.C8, Card.ST, Card.SK, Card.SQ, Card.S8, Card.H9, Card.D8 ));
        cut = new Bidder(cards, Player.MIDDLEHAND);

        assertThat(cut.isGrand()).isTrue();
        assertThat(cut.getGameValue()).isEqualTo(72);


    }

    @Test
    public void testGarbageHands() {
        CardList cards = new CardList(Arrays.asList(Card.CJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
                Card.HT, Card.H8, Card.D9, Card.D8));

        Bidder cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.getGameValue()).isEqualTo(0);


    }

    @Test
    public void testClubsGame() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SA,
                Card.HT, Card.H8, Card.D9));

        Bidder cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.isGrand()).isFalse();
        assertThat(cut.getGameValue()).isEqualTo(24);
    }

    @Test
    public void testDiscard() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SA,
                Card.HT, Card.H8, Card.D9, Card.DA, Card.H7));

        Bidder cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.getGameValue()).isEqualTo(24);
        assertThat(cut.getCardsToDiscard()).contains(Card.HT);
    }

    @Test
    public void testDiscardBadHand() {
        final CardList cards = new CardList(Arrays.asList(Card.SJ, Card.D9, Card.CA, Card.SQ, Card.S8, Card.C7, Card.H8,
                Card.DT, Card.D7, Card.DA, Card.D8, Card.C8));
        Bidder cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.getGameValue()).isEqualTo(0);
        assertThat(cut.gameAnnouncement().getGameType()).isEqualTo(GameType.SPADES);
        assertThat(cut.getCardsToDiscard()).contains(Card.HT);
    }
}
