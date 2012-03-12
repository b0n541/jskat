package org.jskat.ai.algorithmic;

import static org.junit.Assert.assertEquals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.junit.Test;

/**
 * Test cases for class Card
 */
public class BidEvaluatorTest extends AbstractJSkatTest {

	private static Log log = LogFactory.getLog(BidEvaluatorTest.class);

	/**
	 * Test double sorting
	 */
	@Test
	public void testGetMaxBid() {

		log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		CardList cards = new CardList(new Card[] {Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ, Card.HT, Card.H8, Card.D9});

		// sort cards
		BidEvaluator eval = new BidEvaluator(cards);
		
		assertEquals(24, eval.getMaxBid());
		
		cards.remove(Card.DJ);
		cards.add(Card.SJ);
		cards.sort(null);
		eval = new BidEvaluator(cards);
		assertEquals(36, eval.getMaxBid());
		log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

}
