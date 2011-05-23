/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.BasicSkatRules;
import org.jskat.util.rule.SkatRuleFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests for Grand rules
 */
public class GrandRuleTest extends AbstractJSkatTest {

	private static SkatGameData data;
	private static GameAnnouncement ann;

	private static BasicSkatRules grandRules = SkatRuleFactory.getSkatRules(GameType.GRAND);

	/**
	 * @see BeforeClass
	 */
	@Before
	public void initialize() {

		data = new SkatGameData();
		ann = new GameAnnouncement();
		ann.setGameType(GameType.GRAND);
		data.setAnnouncement(ann);
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setGameWon(grandRules.calcGameWon(data));
	}

	/**
	 * Checks @see GrandRules#calcGameWon()
	 */
	@Test
	public void calcGameWon() {
		assertTrue(grandRules.calcGameWon(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameLost() {
		data.setDeclarerScore(60);
		assertFalse(grandRules.calcGameWon(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJack() {
		data.setClubJack(true);
		data.setGameWon(grandRules.calcGameWon(data));
		assertEquals(48, grandRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubAndSpadeJack() {
		data.setClubJack(true);
		data.setSpadeJack(true);
		data.setGameWon(grandRules.calcGameWon(data));
		assertEquals(72, grandRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubAndHeartJack() {
		data.setClubJack(true);
		data.setHeartJack(true);
		data.setGameWon(grandRules.calcGameWon(data));
		assertEquals(48, grandRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackHand() {
		data.setClubJack(true);
		data.setHand(true);
		data.setGameWon(grandRules.calcGameWon(data));
		assertEquals(72, grandRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneider() {
		data.setClubJack(true);
		data.setDeclarerScore(90);
		data.setSchneider(true);
		data.setGameWon(grandRules.calcGameWon(data));
		assertEquals(72, grandRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneiderSchwarz() {
		data.setClubJack(true);
		data.setDeclarerScore(120);
		data.setSchneider(true);
		data.setSchwarz(true);
		data.setGameWon(grandRules.calcGameWon(data));
		assertEquals(96, grandRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneiderAndAnnounced() {
		data.setClubJack(true);
		data.setDeclarerScore(90);
		data.setHand(true);
		data.setSchneider(true);
		data.getAnnoucement().setSchneider(true);
		data.setGameWon(grandRules.calcGameWon(data));
		assertEquals(120, grandRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchwarzAndAnnounced() {
		data.setClubJack(true);
		data.setDeclarerScore(120);
		data.setHand(true);
		data.setSchneider(true);
		data.getAnnoucement().setSchneider(true);
		data.setSchwarz(true);
		data.getAnnoucement().setSchwarz(true);
		data.setGameWon(grandRules.calcGameWon(data));
		assertEquals(168, grandRules.calcGameResult(data));
	}
}
