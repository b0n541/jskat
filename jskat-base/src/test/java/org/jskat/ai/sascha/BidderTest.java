package org.jskat.ai.sascha;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.algorithmic.BidEvaluatorTest;
import org.jskat.ai.sascha.bidder.Bidder;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Suit;

public class BidderTest extends AbstractJSkatTest {
        private static final Logger log = LoggerFactory.getLogger(BidEvaluatorTest.class);

        @Test
        public void testMediocoreHand() {
                final CardList cards = new CardList(
                                Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
                                                Card.HT, Card.H8, Card.D9));

                Bidder cut = new Bidder(cards, Player.MIDDLEHAND);
                assertThat(cut.isGrand()).isFalse();
                assertThat(cut.getGameValue()).isEqualTo(24);
        }

        @Test
        public void testBidGrand() {
                CardList cards = new CardList(
                                Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.CA, Card.CT, Card.HA, Card.HK,
                                                Card.ST, Card.SK, Card.SQ));

                Bidder cut = new Bidder(cards, Player.FOREHAND);
                assertThat(cut.isGrand()).isTrue();
                assertThat(cut.getGameValue()).isEqualTo(96);

                cards = new CardList(
                                Arrays.asList(Card.CJ, Card.SJ, Card.DJ, Card.C8, Card.ST, Card.SK, Card.SQ, Card.S8,
                                                Card.H9, Card.D8));
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

                cards = new CardList(
                                Arrays.asList(Card.CJ, Card.CA, Card.CK, Card.C7, Card.SQ, Card.S8, Card.H9, Card.H8,
                                                Card.H7, Card.DQ));
                cut = new Bidder(cards, Player.FOREHAND);
                assertThat(cut.checkSuit(Suit.HEARTS)).isFalse();
                assertThat(cut.getGameValue()).isEqualTo(0);

                cards = new CardList(
                                Arrays.asList(Card.SJ, Card.CK, Card.C8, Card.C7, Card.SK, Card.HA, Card.HK, Card.H7,
                                                Card.DK, Card.D7));
                cut = new Bidder(cards, Player.FOREHAND);
                assertThat(cut.getGameValue()).isEqualTo(0);

        }

        @Test
        public void testHeartsHands() {
                CardList cards = new CardList(
                                Arrays.asList(Card.SJ, Card.HJ, Card.DJ, Card.CT, Card.S7, Card.HK, Card.HQ,
                                                Card.H9, Card.DT, Card.D9));
                Bidder cut = new Bidder(cards, Player.MIDDLEHAND);
                assertThat(cut.getGameValue()).isEqualTo(20);

                cards = new CardList(Arrays.asList(Card.CJ, Card.HJ, Card.SA, Card.ST, Card.SQ, Card.HK, Card.H8,
                                Card.H7, Card.DQ, Card.D9));
                cut = new Bidder(cards, Player.MIDDLEHAND);
                assertThat(cut.bestTrumpSuit()).isEqualTo(Suit.HEARTS);
                assertThat(cut.checkSuit(Suit.HEARTS)).isTrue();

                // cards = new CardList(Arrays.asList(Card.CT, Card.SA, Card.SQ, Card.HA,
                // Card.HT, Card.HK, Card.HQ,
                // Card.H7, Card.DK, Card.D8));
                // cut = new Bidder(cards, Player.MIDDLEHAND);
                // assertThat(cut.getGameValue()).isEqualTo(20);

        }

        @Test
        public void testSpadesHands() {
                CardList cards;
                Bidder cut;

                cards = new CardList(
                                Arrays.asList(Card.CJ, Card.SJ, Card.ST, Card.SK, Card.SQ, Card.S8, Card.S7, Card.DK,
                                                Card.DQ, Card.D9));
                cut = new Bidder(cards, Player.MIDDLEHAND);
                assertThat(cut.isGrand()).isFalse();
                assertThat(cut.getGameValue()).isEqualTo(33);
        }

        @Test
        public void testClubsGame() {
                CardList cards = new CardList(
                                Arrays.asList(Card.CJ, Card.DJ, Card.C9, Card.CK, Card.CQ, Card.C8, Card.SA,
                                                Card.ST, Card.H8, Card.D9));

                Bidder cut = new Bidder(cards, Player.MIDDLEHAND);
                assertThat(cut.isGrand()).isFalse();
                assertThat(cut.getGameValue()).isEqualTo(24);

                cards = new CardList(Arrays.asList(Card.SJ, Card.DJ, Card.CA, Card.C9, Card.C8, Card.SQ, Card.S8,
                                Card.HT, Card.DA, Card.DK));
                cut = new Bidder(cards, Player.MIDDLEHAND);

                assertThat(cut.checkSuit(Suit.CLUBS)).isTrue();

                // cards = new CardList(Arrays.asList(Card.DJ, Card.CA, Card.CK, Card.CQ,
                // Card.C9, Card.ST, Card.SK, Card.HA,
                // Card.DT, Card.D7));
                // cut = new Bidder(cards, Player.MIDDLEHAND);
                // assertThat(cut.isGrand()).isFalse();
                // assertThat(cut.getGameValue()).isEqualTo(24);
        }

        @Test
        public void testGrandAnnouncement() {
                CardList cards = new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CT, Card.CK,
                                Card.SA, Card.ST, Card.S8, Card.HK, Card.H7, Card.DQ));

                Bidder cut = new Bidder(cards, Player.MIDDLEHAND);
                assertThat(cut.isGrand()).isTrue();
                assertThat(cut.getCardsToDiscard()).contains(Card.HK, Card.DQ);

                cards = new CardList(Arrays.asList(Card.CJ, Card.HJ, Card.CQ, Card.C7, Card.ST, Card.HA,
                                Card.HT, Card.HK, Card.HQ, Card.DA, Card.D9, Card.D7));

                cut = new Bidder(cards, Player.MIDDLEHAND);
                assertThat(cut.isGrand()).isTrue();
                assertThat(cut.getTrumpSuit()).isEqualTo(null);
                assertThat(cut.getCardsToDiscard()).contains(Card.ST, Card.CQ);
        }

        @Test
        public void testSpadesAnnouncement() {
                // CardList cards = new CardList(Arrays.asList(Card.HJ, Card.DJ, Card.CA,
                // Card.C7, Card.SA, Card.SK,
                // Card.SQ, Card.S9, Card.HK, Card.H8, Card.DT, Card.D7));

                // Bidder cut = new Bidder(cards, Player.MIDDLEHAND);
                // assertThat(cut.getTrumpSuit()).isEqualTo(Suit.SPADES);
                // assertThat(cut.getCardsToDiscard()).contains(Card.HK, Card.DT);
        }

        @Test
        public void testHeartsAnnouncement() {
                // CardList cards = new CardList(Arrays.asList(Card.CJ, Card.HJ, Card.CQ,
                // Card.C7, Card.ST, Card.HA,
                // Card.HT, Card.HK, Card.HQ, Card.DA, Card.D9, Card.D7));

                // Bidder cut = new Bidder(cards, Player.MIDDLEHAND);
                // assertThat(cut.isGrand()).isFalse();
                // assertThat(cut.getTrumpSuit()).isEqualTo(Suit.HEARTS);
                // assertThat(cut.getCardsToDiscard()).contains(Card.ST, Card.CK);
        }

}
