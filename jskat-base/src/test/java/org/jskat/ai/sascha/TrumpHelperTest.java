package org.jskat.ai.sascha;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

import org.jskat.ai.sascha.bidder.Bidder;
import org.jskat.ai.sascha.solo.TrumpHelper;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.jskat.util.Suit;
import org.junit.jupiter.api.Test;

public class TrumpHelperTest {
        @Test
    public void testSpadesHands() {
        CardList cards = new CardList(Arrays.asList(Card.SJ, Card.CA, Card.CT, Card.C7, Card.SA, Card.ST, Card.S9,
                Card.S8, Card.HA, Card.HQ));
        TrumpHelper cut = new TrumpHelper(Suit.SPADES, cards);
        assertThat(cut.comebacks()).isEqualTo(1);
        assertThat(cut.getNeededClears()).isEqualTo(3);
    }
}
