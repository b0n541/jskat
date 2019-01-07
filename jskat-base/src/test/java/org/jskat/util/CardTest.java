/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

/**
 * Test cases for class Card
 */
public class CardTest extends AbstractJSkatTest {

	/**
	 * Checks for reflexivity
	 */
	@Test
	public void equals001() {

		assertTrue(Card.CJ.equals(Card.CJ));
	}

	/**
	 * Checks for symetry
	 */
	@Test
	public void equals002() {

		assertTrue(Card.CJ.equals(Card.CJ) == Card.CJ.equals(Card.CJ));
	}

	/**
	 * Checks for symetry
	 */
	@Test
	public void equals003() {

		assertTrue(Card.CJ.equals(Card.D7) == Card.D7.equals(Card.CJ));
	}

	/**
	 * Checks for null reference
	 */
	@Test
	public void equals007() {

		assertFalse(Card.CJ.equals(null));
	}

	/**
	 * Checks @see Card#isTrump(GameType)
	 */
	@Test
	public void isTrump001() {
		assertTrue(Card.HJ.isTrump(GameType.HEARTS));
	}

	/**
	 * Checks @see Card#isTrump(GameType)
	 */
	@Test
	public void isTrump002() {
		assertTrue(Card.HJ.isTrump(GameType.DIAMONDS));
	}

	/**
	 * Checks @see Card#isTrump(GameType)
	 */
	@Test
	public void isTrump003() {
		assertTrue(Card.DJ.isTrump(GameType.HEARTS));
	}

	/**
	 * Checks @see Card#isTrump(GameType)
	 */
	@Test
	public void isTrump004() {
		assertFalse(Card.HT.isTrump(GameType.DIAMONDS));
	}
}
