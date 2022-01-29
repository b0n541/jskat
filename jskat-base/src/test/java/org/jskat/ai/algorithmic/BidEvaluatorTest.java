
package org.jskat.ai.algorithmic;


import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for class Card
 */
public class BidEvaluatorTest extends AbstractJSkatTest {

    private static final Logger log = LoggerFactory.getLogger(BidEvaluatorTest.class);

    /**
     * Test double sorting
     */
    @Test
    public void testGetMaxBid() {

        log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        final CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
                Card.HT, Card.H8, Card.D9));

        // sort cards
        BidEvaluator eval = new BidEvaluator(cards);

        assertThat(eval.getMaxBid()).isEqualTo(24);

        cards.remove(Card.DJ);
        cards.add(Card.SJ);
        cards.sort(null);
        eval = new BidEvaluator(cards);
        assertThat(eval.getMaxBid()).isEqualTo(36);
        log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

}
