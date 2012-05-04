/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
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
package org.jskat.util.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for NullRule
 */
public class NullRuleTest extends AbstractJSkatTest {

	private SkatGameData data;
	private GameAnnouncementFactory factory;

	private static SkatRule nullRules = SkatRuleFactory.getSkatRules(GameType.NULL);

	/**
	 * @see Before
	 */
	@Before
	public void initialize() {

		data = new SkatGameData();
		factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.NULL);
		data.setDeclarer(Player.FOREHAND);
	}

	private void playWinningTricks() {
		data.addTrick(new Trick(0, Player.FOREHAND));
		data.setTrickCard(Player.FOREHAND, Card.C7);
		data.setTrickCard(Player.MIDDLEHAND, Card.C8);
		data.setTrickCard(Player.REARHAND, Card.C9);
		data.setTrickWinner(0, Player.REARHAND);
		data.addTrick(new Trick(1, Player.REARHAND));
		data.setTrickCard(Player.REARHAND, Card.S8);
		data.setTrickCard(Player.FOREHAND, Card.S7);
		data.setTrickCard(Player.MIDDLEHAND, Card.S9);
		data.setTrickWinner(1, Player.MIDDLEHAND);
	}

	/**
	 * Checks @see NullRule#calcGameWon()
	 */
	@Test
	public void calcGameWon() {

		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		data.calcResult();
		assertTrue(data.getResult().isWon());
	}

	/**
	 * Checks @see NullRule#calcGameWon()
	 */
	@Test
	public void calcGameLost() {

		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		playLoosingTrick();
		data.calcResult();
		assertFalse(data.getResult().isWon());
	}

	/**
	 * Checks @see NullRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWon() {

		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		data.calcResult();
		assertEquals(23, data.getResult().getGameValue());
	}

	/**
	 * Checks @see NullRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonHand() {

		factory.setHand(true);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		data.calcResult();
		assertEquals(35, data.getResult().getGameValue());
	}

	/**
	 * Checks @see NullRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonOuvert() {

		data.setDeclarerPickedUpSkat(true);
		factory.setOuvert(Boolean.TRUE);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		data.calcResult();
		assertEquals(46, data.getResult().getGameValue());
	}

	/**
	 * Checks @see NullRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonHandOuvert() {
		factory.setHand(true);
		factory.setOuvert(true);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		data.calcResult();
		assertEquals(59, data.getResult().getGameValue());
	}

	/**
	 * Checks @see NullRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameLost() {

		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		playLoosingTrick();
		data.calcResult();
		assertEquals(-46, data.getResult().getGameValue());
	}

	private void playLoosingTrick() {
		data.addTrick(new Trick(2, Player.MIDDLEHAND));
		data.setTrickCard(Player.MIDDLEHAND, Card.H7);
		data.setTrickCard(Player.REARHAND, Card.H8);
		data.setTrickCard(Player.FOREHAND, Card.H9);
		data.setTrickWinner(2, Player.FOREHAND);
		data.getGameResult().setWon(nullRules.isGameWon(data));
	}

	/**
	 * Checks @see NullRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameLostHand() {
		factory.setHand(true);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		playLoosingTrick();
		data.calcResult();
		assertEquals(-70, nullRules.calcGameResult(data));
	}

	/**
	 * Checks @see NullRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameLostOuvert() {

		data.setDeclarerPickedUpSkat(true);
		factory.setOuvert(Boolean.TRUE);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		playLoosingTrick();
		data.calcResult();
		assertEquals(-92, nullRules.calcGameResult(data));
	}

	/**
	 * Checks @see NullRule#calcGameResult()
	 */
	@Test
	public void calcGameResultGameLostHandOuvert() {
		factory.setHand(true);
		factory.setOuvert(true);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		playLoosingTrick();
		data.calcResult();
		assertEquals(-118, nullRules.calcGameResult(data));
	}

	/**
	 * Test for overbidding in null games
	 */
	@Test
	public void testOverbid() {

		data.setBidValue(24);
		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		playWinningTricks();
		data.calcResult();
		assertEquals(-46, data.getResult().getGameValue());
	}
}
