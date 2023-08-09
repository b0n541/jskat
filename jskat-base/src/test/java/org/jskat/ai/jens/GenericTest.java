package org.jskat.ai.jens;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.algorithmic.BidEvaluatorTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;

public class GenericTest extends AbstractJSkatTest {
    @Test
    public void test01() {
        CardList cards = new CardList(Arrays.asList(
            Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ, Card.HT, Card.H8, Card.D9)
        );

        // var jens = new AIPlayerJens();

        cards.sort(GameType.DIAMONDS);
        assertThat(cards.get(0)).isEqualTo(Card.H8);
    }
}
