/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.algorithmic;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test cases for class Card
 */
public class BidEvaluatorTest extends AbstractJSkatTest {

	private static Logger log = LoggerFactory.getLogger(BidEvaluatorTest.class);

	/**
	 * Test double sorting
	 */
	@Test
	public void testGetMaxBid() {

		log.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		CardList cards = new CardList(Arrays.asList(Card.CJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.C8, Card.SQ,
				Card.HT, Card.H8, Card.D9));

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
