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

	private static SkatRule grandRules = SkatRuleFactory
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
	 * Checks @see GrandRule#calcGameWon()
	 */
	@Test
	public void calcGameWon() {
		final SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.calcResult();
		assertTrue(data.getResult().isWon());
	}

	@Test
	public void calcGameWonSchneiderAnnounced() {
		factory.setHand(Boolean.TRUE);
		factory.setSchneider(Boolean.TRUE);
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(90);
		data.calcResult();
		assertTrue(data.getResult().isWon());
	}

	@Test
	public void calcGameWonSchwarzAnnounced() {
		factory.setHand(Boolean.TRUE);
		factory.setSchneider(Boolean.TRUE);
		factory.setSchwarz(Boolean.TRUE);
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(120);
		data.calcResult();
		assertTrue(data.getResult().isWon());
	}

	/**
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameLost() {
		final SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(60);
		data.calcResult();
		assertFalse(data.getResult().isWon());
	}

	/**
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJack() {
		final SkatGameData data = new SkatGameData();
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
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubAndSpadeJack() {
		final SkatGameData data = new SkatGameData();
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
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubAndHeartJack() {
		final SkatGameData data = new SkatGameData();
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
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackHand() {
		final SkatGameData data = new SkatGameData();
		factory.setHand(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.getGameResult().setWon(grandRules.isGameWon(data));
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.calcResult();
		assertEquals(72, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneider() {
		final SkatGameData data = new SkatGameData();
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
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneiderSchwarz() {
		final SkatGameData data = new SkatGameData();
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
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneiderAndAnnounced() {
		final SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		factory.setHand(true);
		factory.setSchneider(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDeclarerScore(90);
		data.getGameResult().setSchneider(true);
		data.calcResult();
		assertEquals(120, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchwarzAndAnnounced() {
		final SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		factory.setHand(true);
		factory.setSchneider(true);
		factory.setSchwarz(true);
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
		final SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		factory.setSchneider(Boolean.FALSE);
		factory.setSchwarz(Boolean.FALSE);
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarerScore(89);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.calcResult();
		assertEquals(72, data.getResult().getGameValue());
	}

	/**
	 * Test grand with four
	 */
	@Test
	public void testGrandWithFour() {
		final SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarerScore(61);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.SJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.calcResult();
		assertEquals(120, data.getResult().getGameValue());
	}

	/**
	 * Test grand with four schneider
	 */
	@Test
	public void testGrandWithFourSchneider() {
		final SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarerScore(90);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.SJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.calcResult();
		assertEquals(144, data.getResult().getGameValue());
	}

	/**
	 * Test grand with four schneider and schwarz
	 */
	@Test
	public void testGrandWithFourSchneiderSchwarz() {
		final SkatGameData data = new SkatGameData();
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarerScore(120);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.SJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.calcResult();
		assertEquals(168, data.getResult().getGameValue());
	}
}
