/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jskat.AbstractJSkatTest;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.Trick;
import de.jskat.util.Card;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Tests for NullRules
 */
public class NullRuleTest extends AbstractJSkatTest {

	private static SkatGameData data;
	private static GameAnnouncement ann;

	private static BasicSkatRules nullRules = SkatRuleFactory.getSkatRules(GameType.NULL);

	/**
	 * @see BeforeClass
	 */
	@Before
	public void initialize() {

		data = new SkatGameData();
		ann = new GameAnnouncement();
		ann.setGameType(GameType.NULL);
		data.setAnnouncement(ann);
		data.setDeclarer(Player.FORE_HAND);
		data.addTrick(new Trick(0, Player.FORE_HAND));
		data.setTrickCard(Player.FORE_HAND, Card.C7);
		data.setTrickCard(Player.MIDDLE_HAND, Card.C8);
		data.setTrickCard(Player.HIND_HAND, Card.C9);
		data.setTrickWinner(0, Player.HIND_HAND);
		data.addTrick(new Trick(1, Player.HIND_HAND));
		data.setTrickCard(Player.HIND_HAND, Card.S8);
		data.setTrickCard(Player.FORE_HAND, Card.S7);
		data.setTrickCard(Player.MIDDLE_HAND, Card.S9);
		data.setTrickWinner(1, Player.MIDDLE_HAND);
		data.setGameWon(nullRules.calcGameWon(data));
	}

	/**
	 * Checks @see NullRules#calcGameWon()
	 */
	@Test
	public void calcGameWon() {
		assertTrue(nullRules.calcGameWon(data));
	}

	/**
	 * Checks @see NullRules#calcGameWon()
	 */
	@Test
	public void calcGameLost() {
		addLoosingTrick();
		assertFalse(nullRules.calcGameWon(data));
	}

	/**
	 * Checks @see NullRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWon() {
		assertEquals(23, nullRules.calcGameResult(data));
	}

	/**
	 * Checks @see NullRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonHand() {

		data.getAnnoucement().setHand(true);
		assertEquals(35, nullRules.calcGameResult(data));
	}

	/**
	 * Checks @see NullRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonOuvert() {

		data.getAnnoucement().setOuvert(true);
		assertEquals(46, nullRules.calcGameResult(data));
	}

	/**
	 * Checks @see NullRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameWonHandOuvert() {

		data.getAnnoucement().setHand(true);
		data.getAnnoucement().setOuvert(true);
		assertEquals(59, nullRules.calcGameResult(data));
	}

	/**
	 * Checks @see NullRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameLost() {

		addLoosingTrick();
		assertEquals(-46, nullRules.calcGameResult(data));
	}

	private void addLoosingTrick() {
		data.addTrick(new Trick(2, Player.MIDDLE_HAND));
		data.setTrickCard(Player.MIDDLE_HAND, Card.H7);
		data.setTrickCard(Player.HIND_HAND, Card.H8);
		data.setTrickCard(Player.FORE_HAND, Card.H9);
		data.setTrickWinner(2, Player.FORE_HAND);
		data.setGameWon(nullRules.calcGameWon(data));
	}

	/**
	 * Checks @see NullRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameLostHand() {

		addLoosingTrick();
		data.getAnnoucement().setHand(true);
		assertEquals(-70, nullRules.calcGameResult(data));
	}

	/**
	 * Checks @see NullRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameLostOuvert() {

		addLoosingTrick();
		data.getAnnoucement().setOuvert(true);
		assertEquals(-92, nullRules.calcGameResult(data));
	}

	/**
	 * Checks @see NullRules#calcGameResult()
	 */
	@Test
	public void calcGameResultGameLostHandOuvert() {

		addLoosingTrick();
		data.getAnnoucement().setHand(true);
		data.getAnnoucement().setOuvert(true);
		assertEquals(-118, nullRules.calcGameResult(data));
	}
}
