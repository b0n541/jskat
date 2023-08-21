package org.jskat.ai.sascha;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Suit;
import org.junit.jupiter.api.Test;

public class UtilTest extends AbstractJSkatTest {

    @Test
    public void testMediocoreHand() {
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
                Card.HT, Card.H8, Card.D9));

        assertThat(Util.suiteWeakness(cards, Suit.HEARTS)).isEqualTo(3);
        assertThat(Util.suiteWeakness(cards, Suit.SPADES)).isEqualTo(4);
        assertThat(Util.suiteWeakness(cards, Suit.DIAMONDS)).isEqualTo(4);
    }

}
