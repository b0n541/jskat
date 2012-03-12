/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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
package org.jskat.ai.nn;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.junit.Test;

/**
 * Test class for {@link AIPlayerNN}
 */
public class AIPlayerNNTest extends AbstractJSkatTest {

	List<Card> nullOrder = Arrays.asList(Card.D7, Card.D8, Card.D9, Card.DT, Card.DJ, Card.DQ, Card.DK, Card.DA,
			Card.H7, Card.H8, Card.H9, Card.HT, Card.HJ, Card.HQ, Card.HK, Card.HA, Card.S7, Card.S8, Card.S9, Card.ST,
			Card.SJ, Card.SQ, Card.SK, Card.SA, Card.C7, Card.C8, Card.C9, Card.CT, Card.CJ, Card.CQ, Card.CK, Card.CA);

	List<Card> grandOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.D7, Card.D8, Card.D9, Card.DQ,
			Card.DK, Card.DT, Card.DA, Card.H7, Card.H8, Card.H9, Card.HQ, Card.HK, Card.HT, Card.HA, Card.S7, Card.S8,
			Card.S9, Card.SQ, Card.SK, Card.ST, Card.SA, Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT, Card.CA);

	List<Card> clubsOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.C7, Card.C8, Card.C9, Card.CQ,
			Card.CK, Card.CT, Card.CA, Card.D7, Card.D8, Card.D9, Card.DQ, Card.DK, Card.DT, Card.DA, Card.H7, Card.H8,
			Card.H9, Card.HQ, Card.HK, Card.HT, Card.HA, Card.S7, Card.S8, Card.S9, Card.SQ, Card.SK, Card.ST, Card.SA);

	List<Card> spadesOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.S7, Card.S8, Card.S9, Card.SQ,
			Card.SK, Card.ST, Card.SA, Card.D7, Card.D8, Card.D9, Card.DQ, Card.DK, Card.DT, Card.DA, Card.H7, Card.H8,
			Card.H9, Card.HQ, Card.HK, Card.HT, Card.HA, Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT, Card.CA);

	List<Card> heartsOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.H7, Card.H8, Card.H9, Card.HQ,
			Card.HK, Card.HT, Card.HA, Card.D7, Card.D8, Card.D9, Card.DQ, Card.DK, Card.DT, Card.DA, Card.S7, Card.S8,
			Card.S9, Card.SQ, Card.SK, Card.ST, Card.SA, Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT, Card.CA);

	List<Card> diamondsOrder = Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.D7, Card.D8, Card.D9, Card.DQ,
			Card.DK, Card.DT, Card.DA, Card.H7, Card.H8, Card.H9, Card.HQ, Card.HK, Card.HT, Card.HA, Card.S7, Card.S8,
			Card.S9, Card.SQ, Card.SK, Card.ST, Card.SA, Card.C7, Card.C8, Card.C9, Card.CQ, Card.CK, Card.CT, Card.CA);

	/**
	 * Tests the calculation of the net input index
	 */
	@Test
	public void testGetNetInputIndexForNullGame() {

		for (Card card : Card.values()) {

			int index = AIPlayerNN.getNetInputIndex(GameType.NULL, card);
			assertEquals("Wrong index for " + card + ": ", nullOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/**
	 * Tests the calculation of the net input index
	 */
	@Test
	public void testGetNetInputIndexForGrandGame() {

		for (Card card : Card.values()) {

			int index = AIPlayerNN.getNetInputIndex(GameType.GRAND, card);
			assertEquals("Wrong index for " + card + ": ", grandOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/**
	 * Tests the calculation of the net input index
	 */
	@Test
	public void testGetNetInputIndexForRamschGame() {

		for (Card card : Card.values()) {

			int index = AIPlayerNN.getNetInputIndex(GameType.RAMSCH, card);
			assertEquals("Wrong index for " + card + ": ", grandOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/**
	 * Tests the calculation of the net input index
	 */
	@Test
	public void testGetNetInputIndexForClubsGame() {

		for (Card card : Card.values()) {

			int index = AIPlayerNN.getNetInputIndex(GameType.CLUBS, card);
			assertEquals("Wrong index for " + card + ": ", clubsOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/**
	 * Tests the calculation of the net input index
	 */
	@Test
	public void testGetNetInputIndexForSpadesGame() {

		for (Card card : Card.values()) {

			int index = AIPlayerNN.getNetInputIndex(GameType.SPADES, card);
			assertEquals("Wrong index for " + card + ": ", spadesOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/**
	 * Tests the calculation of the net input index
	 */
	@Test
	public void testGetNetInputIndexForHeartsGame() {

		for (Card card : Card.values()) {

			int index = AIPlayerNN.getNetInputIndex(GameType.HEARTS, card);
			assertEquals("Wrong index for " + card + ": ", heartsOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/**
	 * Tests the calculation of the net input index
	 */
	@Test
	public void testGetNetInputIndexForDiamondssGame() {

		for (Card card : Card.values()) {

			int index = AIPlayerNN.getNetInputIndex(GameType.DIAMONDS, card);
			assertEquals("Wrong index for " + card + ": ", diamondsOrder.indexOf(card), index); //$NON-NLS-1$//$NON-NLS-2$
		}
	}
}
