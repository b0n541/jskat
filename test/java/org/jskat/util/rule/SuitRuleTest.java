package org.jskat.util.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for suit games
 */
public class SuitRuleTest extends AbstractJSkatTest {

	private static SkatGameData data;
	private static GameAnnouncement ann;

	private static BasicSkatRules clubsRules = SkatRuleFactory
			.getSkatRules(GameType.CLUBS);

	/**
	 * @see BeforeClass
	 */
	@Before
	public void initialize() {

		data = new SkatGameData();
		ann = new GameAnnouncement();
		ann.setGameType(GameType.CLUBS);
		data.setAnnouncement(ann);
		data.setDeclarer(Player.FOREHAND);
		data.setDeclarerScore(61);
		data.setGameWon(clubsRules.calcGameWon(data));
	}

	/**
	 * Checks @see GrandRules#calcGameWon()
	 */
	@Test
	public void calcGameWon() {
		assertTrue(clubsRules.calcGameWon(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubJack() {
		data.setClubJack(true);
		data.setGameWon(clubsRules.calcGameWon(data));
		data.calcResult();
		assertEquals(24, data.getResult());
		assertEquals(24, clubsRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubSpadeHeartJack() {
		data.setClubJack(true);
		data.setSpadeJack(true);
		data.setHeartJack(true);
		data.setGameWon(clubsRules.calcGameWon(data));
		data.calcResult();
		assertEquals(48, data.getResult());
		assertEquals(48, clubsRules.calcGameResult(data));
	}

	/**
	 * Checks @see GrandRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonClubSpadeHeartDiamondJack() {
		data.setClubJack(true);
		data.setSpadeJack(true);
		data.setHeartJack(true);
		data.setDiamondJack(true);
		data.setGameWon(clubsRules.calcGameWon(data));
		data.calcResult();
		assertEquals(60, data.getResult());
		assertEquals(60, clubsRules.calcGameResult(data));
	}
}
