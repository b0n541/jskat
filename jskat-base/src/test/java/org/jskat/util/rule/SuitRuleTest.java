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
package org.jskat.util.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
	public void calcGameLostSchneiderAnnounced() {
		factory.setHand(Boolean.TRUE);
		factory.setSchneider(Boolean.TRUE);
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(89);
		assertFalse(clubsRules.isGameWon(data));
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

	@Test
	public void calcGameLostSchwarzAnnounced() {
		factory.setHand(Boolean.TRUE);
		factory.setSchneider(Boolean.TRUE);
		factory.setSchwarz(Boolean.TRUE);
		SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(119);
		assertFalse(clubsRules.isGameWon(data));
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
		assertThat(data.getResult().getGameValue(), is(24));
	}

	@Test
	public void calcGameResultGameWonClubJackContra() {
		SkatGameData data = new SkatGameData();
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setContra(true);
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ,
				Card.DJ, Card.CA));
		data.calcResult();
		assertThat(data.getResult().getGameValue(), is(48));
	}

	@Test
	public void calcGameResultGameWonClubJackContraRe() {
		SkatGameData data = new SkatGameData();
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setContra(true);
		data.setRe(true);
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ,
				Card.DJ, Card.CA));
		data.calcResult();
		assertThat(data.getResult().getGameValue(), is(96));
	}

	@Test
	public void calcGameResultGameLostClubJack() {
		SkatGameData data = new SkatGameData();
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(60);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ,
				Card.DJ, Card.CA));
		data.calcResult();
		assertThat(data.getResult().getGameValue(), is(-48));
	}

	@Test
	public void calcGameResultGameLostClubJackContra() {
		SkatGameData data = new SkatGameData();
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setContra(true);
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(60);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ,
				Card.DJ, Card.CA));
		data.calcResult();
		assertThat(data.getResult().getGameValue(), is(-96));
	}

	@Test
	public void calcGameResultGameLostClubJackContraRe() {
		SkatGameData data = new SkatGameData();
		factory.setHand(false);
		data.setAnnouncement(factory.getAnnouncement());
		data.setContra(true);
		data.setRe(true);
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(60);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ,
				Card.DJ, Card.CA));
		data.calcResult();
		assertThat(data.getResult().getGameValue(), is(-192));
	}

	/**
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneider() {
		final SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
		data.setDeclarerScore(90);
		data.calcResult();
		assertEquals(36, data.getResult().getGameValue());
	}

	/**
	 * Checks @see GrandRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJackSchneiderSchwarz() {
		final SkatGameData data = new SkatGameData();
		data.setAnnouncement(factory.getAnnouncement());
		data.setDeclarer(Player.FOREHAND);
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
		data.setDeclarerScore(120);
		data.calcResult();
		assertEquals(48, data.getResult().getGameValue());
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
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
		data.setDeclarerScore(90);
		data.getGameResult().setSchneider(true);
		data.calcResult();
		assertEquals(60, data.getResult().getGameValue());
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
		data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
		data.setDeclarerScore(120);
		data.getGameResult().setSchneider(true);
		data.getGameResult().setSchwarz(true);
		data.calcResult();
		assertEquals(84, data.getResult().getGameValue());
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
