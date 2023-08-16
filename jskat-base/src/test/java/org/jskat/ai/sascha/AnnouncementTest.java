package org.jskat.ai.sascha;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.jskat.ai.sascha.bidder.Bidder;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.jskat.util.Suit;
import org.junit.jupiter.api.Test;

public class AnnouncementTest {
    @Test
    public void testSpadesAnnouncement() {
        Bidder cut;
        CardList cards;

        cards = new CardList(Arrays.asList(Card.CA, Card.CT, Card.C7, Card.ST, Card.S9, Card.S8, Card.S7, Card.HA,
                Card.HT, Card.H8, Card.DT, Card.D9));

        cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.getTrumpSuit()).isEqualTo(Suit.SPADES);
        assertThat(cut.getCardsToDiscard()).contains(Card.DT, Card.D9);

        cards = new CardList(Arrays.asList(Card.CJ, Card.C9, Card.C7, Card.SK, Card.S9, Card.S8, Card.S7, Card.HT,
                Card.HK, Card.DT, Card.DK, Card.D9));
        cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.getTrumpSuit()).isEqualTo(Suit.SPADES);
        assertThat(cut.getCardsToDiscard()).contains(Card.C9, Card.C7);

        cards = new CardList(Arrays.asList(Card.DJ, Card.C7, Card.ST, Card.SK, Card.SQ, Card.S9, Card.HA, Card.HK,
                Card.H9, Card.DK, Card.D9, Card.D8));
        cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.isGrand()).isFalse();
        assertThat(cut.getTrumpSuit()).isEqualTo(Suit.SPADES);
        assertThat(cut.getCardsToDiscard()).contains(Card.C7);

        // todo: assertThat(cut.getCardsToDiscard()).contains(Card.C7, Card.DK);
    }

    @Test
    public void testGrandAnnouncement() {
        Bidder cut;
        CardList cards;
        cards = new CardList(Arrays.asList(Card.SJ, Card.HJ, Card.CA, Card.SA, Card.ST, Card.SK, Card.H8, Card.DA,
                Card.DQ, Card.D9, Card.D8, Card.D7));
        cut = new Bidder(cards, Player.MIDDLEHAND);
        assertThat(cut.isGrand()).isTrue();
        assertThat(cut.getTrumpSuit()).isEqualTo(null);
        // TODO: discard points
        assertThat(cut.getCardsToDiscard()).contains(Card.H8);

        cards = new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.CA, Card.CT, Card.CK, Card.C9, Card.S9, Card.S8,
                Card.HA, Card.HK, Card.DA, Card.D8));
        cut = new Bidder(cards, Player.MIDDLEHAND);
        assertThat(cut.isGrand()).isTrue();
        assertThat(cut.getTrumpSuit()).isEqualTo(null);
        assertThat(cut.getCardsToDiscard()).contains(Card.S9, Card.S8);

    }

    @Test
    public void testHeartsAnnouncement() {
        Bidder cut;
        CardList cards;
        cards = new CardList(Arrays.asList(Card.SJ, Card.ST, Card.SK, Card.SQ, Card.S8, Card.HQ, Card.H9, Card.H8,
                Card.H7, Card.DT, Card.DQ, Card.D7));
        cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.isGrand()).isFalse();
        assertThat(cut.getTrumpSuit()).isEqualTo(Suit.HEARTS);

        cards = new CardList(Arrays.asList(Card.HJ, Card.CK, Card.C7, Card.S7, Card.HA, Card.HT, Card.HK, Card.HQ,
                Card.H8, Card.H7, Card.DT, Card.DK));
        cut = new Bidder(cards, Player.FOREHAND);
        assertThat(cut.isGrand()).isFalse();
        assertThat(cut.getTrumpSuit()).isEqualTo(Suit.HEARTS);
        assertThat(cut.getCardsToDiscard()).contains(Card.CK, Card.S7);

    }

    @Test
    public void testDiamonds() {
        Bidder cut;
        CardList cards;

        // assertThat(cut.getTrumpSuit()).isEqualTo(Suit.SPADES);
        // assertThat(cut.getCardsToDiscard()).contains(Card.DT, Card.D9);

    }

    @Test
    public void testClubs() {
        Bidder cut;
        CardList cards;
        // assertThat(cut.getTrumpSuit()).isEqualTo(Suit.SPADES);
        // assertThat(cut.getCardsToDiscard()).contains(Card.DT, Card.D9);

    }
}
