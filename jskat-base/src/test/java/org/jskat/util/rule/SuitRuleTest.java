/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
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
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CA));
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
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ,
				Card.DJ, Card.CA));
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
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ,
				Card.HJ, Card.CA));
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
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ,
				Card.HJ, Card.DJ, Card.CT));
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
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ,
				Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CQ));
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
		data.addDealtCards(Player.MIDDLEHAND, new CardList(Card.SJ, Card.HJ,
				Card.DJ));
		data.addPlayerBid(Player.MIDDLEHAND, 22);
		data.setDeclarer(Player.MIDDLEHAND);
		factory.setHand(false);
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
		data.addDealtCards(Player.MIDDLEHAND, new CardList(Card.SJ, Card.HJ,
				Card.DJ));
		data.addPlayerBid(Player.MIDDLEHAND, 22);
		data.setDeclarer(Player.MIDDLEHAND);
		factory.setHand(false);
		factory.setGameType(GameType.DIAMONDS);
		data.setAnnouncement(factory.getAnnouncement());

		data.setDeclarerScore(90);
		data.getResult().setSchneider(true);

		data.calcResult();

		assertTrue(data.getResult().isWon());
		assertEquals(27, data.getResult().getGameValue());
	}
}
