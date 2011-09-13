/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for Grand rules
 */
public class GrandRuleTest extends AbstractJSkatTest {

	private GameAnnouncementFactory factory;

	private static BasicSkatRules grandRules = SkatRuleFactory
			.getSkatRules(GameType.GRAND);

	/**
	 * @see BeforeClass
	 */
	@Before
	public void initialize() {

		factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.GRAND);
	}

	/**
	 * Checks @see GrandRules#calcGameWon()
	 */
	@Test
	public void calcGameWon() {
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.calcResult();
		assertTrue(data.getResult().isWon());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameLost() {
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(60);
		data.calcResult();
		assertFalse(data.getResult().isWon());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJack() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.calcResult();
		assertEquals(48, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubAndSpadeJack() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.SJ);
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.calcResult();
		assertEquals(72, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubAndHeartJack() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.calcResult();
		assertEquals(48, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackHand() {
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.getGameResult().setWon(grandRules.calcGameWon(data));
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.calcResult();
		assertEquals(72, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneider() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDeclarerScore(90);
		data.getGameResult().setSchneider(true);
		data.calcResult();
		assertEquals(72, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneiderSchwarz() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDeclarerScore(120);
		data.getGameResult().setSchneider(true);
		data.getGameResult().setSchwarz(true);
		data.calcResult();
		assertEquals(96, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneiderAndAnnounced() {
		SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		factory.setSchneider(Boolean.TRUE);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDeclarerScore(90);
		data.getGameResult().setSchneider(true);
		data.calcResult();
		assertEquals(120, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchwarzAndAnnounced() {
		SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		factory.setSchneider(Boolean.TRUE);
		factory.setSchwarz(Boolean.TRUE);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDeclarerScore(120);
		data.getGameResult().setSchneider(true);
		data.getGameResult().setSchwarz(true);
		data.calcResult();
		assertEquals(168, data.getResult().getGameValue());
	}

	/**
	 * Tests a grand game without 2
	 */
	@Test
	public void testGrandWithout2And89PointsForDeclarer() {
		SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		factory.setSchneider(Boolean.FALSE);
		factory.setSchwarz(Boolean.FALSE);
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarerScore(91);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.calcResult();
		assertEquals(72, data.getResult().getGameValue());
	}
}
