/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20 21:16:11
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

package org.jskat.util;

import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.CardDeck;
import org.junit.Test;


/**
 * Test cases for class CardDeck
 */
public class CardDeckTest extends AbstractJSkatTest {

	/**
	 * Checks method that returns all cards
	 */
	@Test
	public void getAllCards001() {

		assertTrue(CardDeck.getAllCards().size() == 32);
	}

	/**
	 * Checks setting a card position to null
	 */
	@Test
	public void setNullCard001() {

		CardDeck simCards = new CardDeck();
		simCards.set(0, null);

		assertTrue(simCards.get(0) == null);
	}
}
