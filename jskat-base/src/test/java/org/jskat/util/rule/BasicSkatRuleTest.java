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
package org.jskat.util.rule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.junit.Test;

/**
 * Test suite for all skat rule tests
 */
public class BasicSkatRuleTest extends AbstractJSkatTest {

	private CardList hand = new CardList();

	private static SkatRule clubRules = SkatRuleFactory.getSkatRules(GameType.CLUBS);
	private static SkatRule spadeRules = SkatRuleFactory.getSkatRules(GameType.SPADES);
	private static SkatRule heartRules = SkatRuleFactory.getSkatRules(GameType.HEARTS);
	private static SkatRule diamondRules = SkatRuleFactory.getSkatRules(GameType.DIAMONDS);
	private static SkatRule grandRules = SkatRuleFactory.getSkatRules(GameType.GRAND);
	private static SkatRule nullRules = SkatRuleFactory.getSkatRules(GameType.NULL);
	private static SkatRule ramschRules = SkatRuleFactory.getSkatRules(GameType.RAMSCH);

	/**
	 * Checks @see NullRule#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedNull001() {
		assertTrue(nullRules.isCardAllowed(GameType.NULL, Card.CA, this.hand, Card.CK));
	}

	/**
	 * Checks @see GrandRule#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedGrand001() {
		assertTrue(grandRules.isCardAllowed(GameType.GRAND, Card.CA, this.hand, Card.CK));
	}

	/**
	 * Checks @see SuitRule#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedSuit001() {
		assertTrue(clubRules.isCardAllowed(GameType.CLUBS, Card.CA, this.hand, Card.CK));
	}

	/**
	 * Checks @see SuitRule#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedSuit002() {

		this.hand.clear();
		this.hand.add(Card.C7);
		this.hand.add(Card.HK);
		this.hand.add(Card.HJ);

		assertFalse(diamondRules.isCardAllowed(GameType.DIAMONDS, Card.HT, this.hand, Card.HJ));
	}

	/**
	 * Checks @see SuitRule#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedSuit003() {

		this.hand.clear();
		this.hand.add(Card.C7);
		this.hand.add(Card.HK);
		this.hand.add(Card.HJ);

		assertTrue(diamondRules.isCardAllowed(GameType.DIAMONDS, Card.HT, this.hand, Card.HK));
	}

	/**
	 * Checks @see RamschRule#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedRamsch001() {
		assertTrue(ramschRules.isCardAllowed(GameType.RAMSCH, Card.CA, this.hand, Card.CK));
	}

	/**
	 * Checks @see NullRule#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardNull001() {
		assertTrue(nullRules.isCardBeatsCard(GameType.NULL, Card.C7, Card.C8));
	}

	/**
	 * Checks @see GrandRule#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardGrand001() {
		assertTrue(grandRules.isCardBeatsCard(GameType.GRAND, Card.C7, Card.C8));
	}

	/**
	 * Checks @see SuitRule#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardSuit001() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.C7, Card.C8));
	}

	/**
	 * Checks @see SuitRule#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardSuit002() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.C7, Card.HJ));
	}

	/**
	 * Checks @see SuitRule#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardSuit003() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.CA, Card.CJ));
	}

	/**
	 * Checks @see SuitRule#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardSuit004() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.SJ, Card.CJ));
	}

	/**
	 * Checks @see RamschRule#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardRamsch001() {
		assertTrue(ramschRules.isCardBeatsCard(GameType.RAMSCH, Card.C7, Card.C8));
	}
}
