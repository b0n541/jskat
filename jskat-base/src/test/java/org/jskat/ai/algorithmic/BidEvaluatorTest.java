/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
