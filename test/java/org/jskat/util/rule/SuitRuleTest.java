/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.1
 * Copyright (C) 2013-05-10
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
 * Tests for suit games
 */
public class SuitRuleTest extends AbstractJSkatTest {

	private GameAnnouncementFactory factory;

	private static SkatRule clubsRules = SkatRuleFactory
			.getSkatRules(GameType.CLUBS);

	/**
	 * @see BeforeClass
	 */
	@Before
	public void initialize() {

		factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.CLUBS);
	}

	/**
	 * Checks @see SuitGrandRule#calcGameWon()
	 */
	@Test
	public void calcGameWon() {
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		assertTrue(clubsRules.isGameWon(data));
	}

	@Test
	public void calcGameWonSchneiderAnnounced() {
		factory.setHand(Boolean.TRUE);
		factory.setSchneider(Boolean.TRUE);
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(90);
		assertTrue(clubsRules.isGameWon(data));
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
		assertTrue(clubsRules.isGameWon(data));
	}

	/**
	 * Checks @see SuitGrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonWithoutJacks() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setDealtCard(Player.FOREHAND, Card.CA);
		data.calcResult();
		assertEquals(60, data.getResult().getGameValue());
		assertEquals(60, clubsRules.calcGameResult(data));
	}

	/**
	 * Checks @see SuitGrandRule#calcGameResult()
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
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.setDealtCard(Player.FOREHAND, Card.CA);
		data.calcResult();
		assertEquals(24, data.getResult().getGameValue());
		assertEquals(24, clubsRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubSpadeHeartJack() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.SJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDealtCard(Player.FOREHAND, Card.CA);
		data.calcResult();
		assertEquals(48, data.getResult().getGameValue());
		assertEquals(48, clubsRules.calcGameResult(data));
	}

	/**
	 * Checks @see SuitGrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubSpadeHeartDiamondJack() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.SJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.setDealtCard(Player.FOREHAND, Card.CT);
		data.calcResult();
		assertEquals(60, data.getResult().getGameValue());
		assertEquals(60, clubsRules.calcGameResult(data));
	}

	/**
	 * Checks @see SuitGrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonMoreTops() {
		SkatGameData data = new SkatGameData();
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setDealtCard(Player.FOREHAND, Card.CJ);
		data.setDealtCard(Player.FOREHAND, Card.SJ);
		data.setDealtCard(Player.FOREHAND, Card.HJ);
		data.setDealtCard(Player.FOREHAND, Card.DJ);
		data.setDealtCard(Player.FOREHAND, Card.CA);
		data.setDealtCard(Player.FOREHAND, Card.CT);
		data.setDealtCard(Player.FOREHAND, Card.CQ);
		data.calcResult();
		assertEquals(84, data.getResult().getGameValue());
		assertEquals(84, clubsRules.calcGameResult(data));
	}

	/**
	 * Test for overbidding<br />
	 * Middle hand wins the bidding with 22<br/>
	 * Picks up skat<br />
	 * Announces Diamonds without one (has SJ, HJ, DJ)<br />
	 * Middle hand makes 89 points<br />
	 * Game is lost because of overbidding
	 */
	@Test
	public void testOverbid() {
		SkatGameData data = new SkatGameData();
		data.setDealtCard(Player.MIDDLEHAND, Card.SJ);
		data.setDealtCard(Player.MIDDLEHAND, Card.HJ);
		data.setDealtCard(Player.MIDDLEHAND, Card.DJ);
		data.setBidValue(22);
		data.setDeclarer(Player.MIDDLEHAND);
		data.setDeclarerPickedUpSkat(true);
		factory.setGameType(GameType.DIAMONDS);
		data.setAnnouncement(factory.getAnnouncement());

		data.setDeclarerScore(89);

		data.calcResult();

		assertFalse(data.getResult().isWon());
		assertEquals(-36, data.getResult().getGameValue());
		assertTrue(data.getResult().isOverBidded());
	}

	/**
	 * Test for overbidding with schneider<br />
	 * Same as before but this time the declarer played the other player
	 * schneider, game is won
	 */
	@Test
	public void testOverbidWithSchneider() {
		SkatGameData data = new SkatGameData();
		data.setDealtCard(Player.MIDDLEHAND, Card.SJ);
		data.setDealtCard(Player.MIDDLEHAND, Card.HJ);
		data.setDealtCard(Player.MIDDLEHAND, Card.DJ);
		data.setBidValue(22);
		data.setDeclarer(Player.MIDDLEHAND);
		data.setDeclarerPickedUpSkat(true);
		factory.setGameType(GameType.DIAMONDS);
		data.setAnnouncement(factory.getAnnouncement());

		data.setDeclarerScore(90);
		data.getResult().setSchneider(true);

		data.calcResult();

		assertTrue(data.getResult().isWon());
		assertEquals(27, data.getResult().getGameValue());
	}
}
