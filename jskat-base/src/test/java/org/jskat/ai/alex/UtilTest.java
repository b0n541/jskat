package org.jskat.ai.alex;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.algorithmic.BidEvaluatorTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Suit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilTest extends AbstractJSkatTest {
    private static final Logger log = LoggerFactory.getLogger(BidEvaluatorTest.class);

    @Test
    public void testIsGoodGrandHand() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.SA, Card.HA, Card.SQ,
                Card.HT, Card.H8, Card.D9));
        assertThat(Util.isGoodGrandHand(cards)).isTrue();
    }

    @Test
    public void testNoGoodGrandHand() {
        final CardList cards = new CardList(Arrays.asList(Card.C7, Card.SJ, Card.HJ, Card.DJ, Card.SA, Card.HA, Card.SQ,
                Card.HT, Card.H8, Card.D9));
        assertThat(Util.isGoodGrandHand(cards)).isFalse();
    }

    @Test
    public void testCreateState() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.H9, Card.D7, Card.SA, Card.HA, Card.SQ,
                Card.HT, Card.H8, Card.D9));
        MyState state = Util.CreateState(cards);
        assertThat(state.game).isEqualTo(GameType.HEARTS);
        assertThat(state.maxBid == 30).isTrue();
        assertThat(state.pickUpSkat).isTrue();
        assertThat(state.hand).isFalse();
        assertThat(state.schneider).isFalse();
    }

    @Test
    public void getLeastFrequentSuit() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.H9, Card.D7, Card.SA, Card.HA, Card.SQ,
                Card.HT, Card.H8, Card.S9));
        assertThat(Util.getLeastFrequentSuit(cards, false)).isEqualTo(Suit.CLUBS);
        assertThat(Util.getLeastFrequentSuit(cards, true)).isEqualTo(Suit.DIAMONDS);
    }
}
