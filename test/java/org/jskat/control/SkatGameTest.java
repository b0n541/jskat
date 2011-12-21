package org.jskat.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.ai.test.NoBiddingTestPlayer;
import org.jskat.ai.test.RamschTestPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameResult;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.gui.UnitTestView;
import org.jskat.util.GameType;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;
import org.junit.Test;

/**
 * Test class for {@link SkatGame}
 */
public class SkatGameTest extends AbstractJSkatTest {

	/**
	 * When no player bids, game is passed in
	 */
	@Test
	public void testPassIn_NoBids() {

		JSkatOptions options = JSkatOptions.instance();
		options.setRules(RuleSet.ISPA);

		SkatGame game = new SkatGame("Table 1", GameVariant.STANDARD, new NoBiddingTestPlayer(), //$NON-NLS-1$
				new NoBiddingTestPlayer(), new NoBiddingTestPlayer());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.PASSED_IN, summary.getGameType());

		SkatGameResult result = game.getGameResult();
		assertFalse(result.isWon());
		assertEquals(0, result.getGameValue());
	}

	/**
	 * When no player bids, game is passed in even the options for playing
	 * ramsch are set but the active ruleset is ISPA rules
	 */
	@Test
	public void testPassIn_NoBids2() {

		JSkatOptions options = JSkatOptions.instance();
		options.setRules(RuleSet.ISPA);
		options.setPlayRamsch(true);
		options.setRamschEventNoBid(true);

		SkatGame game = new SkatGame("Table 1", GameVariant.STANDARD, new NoBiddingTestPlayer(), //$NON-NLS-1$
				new NoBiddingTestPlayer(), new NoBiddingTestPlayer());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.PASSED_IN, summary.getGameType());

		SkatGameResult result = game.getGameResult();
		assertFalse(result.isWon());
		assertEquals(0, result.getGameValue());
	}

	/**
	 * When no player bids and pub rules are acitvated, ramsch is played
	 */
	@Test
	public void testRamsch_NoBids() {

		JSkatOptions options = JSkatOptions.instance();
		options.setRules(RuleSet.PUB);
		options.setPlayRamsch(true);
		options.setRamschEventNoBid(true);

		SkatGame game = new SkatGame("Table 1", GameVariant.STANDARD, new NoBiddingTestPlayer(), //$NON-NLS-1$
				new NoBiddingTestPlayer(), new NoBiddingTestPlayer());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.RAMSCH, summary.getGameType());

		SkatGameResult result = game.getGameResult();
		assertFalse(result.isWon());
		assertTrue(result.getGameValue() < 0);
	}

	/**
	 * Forced ramsch game
	 */
	@Test
	public void testRamsch_Forced() {

		SkatGame game = new SkatGame("Table 1", GameVariant.RAMSCH, new AIPlayerRND(), //$NON-NLS-1$
				new AIPlayerRND(), new AIPlayerRND());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.RAMSCH, summary.getGameType());

		SkatGameResult result = game.getGameResult();
		assertFalse(result.isWon());
		assertTrue(result.getGameValue() < 0);
	}

	/**
	 * Forced ramsch game, grand hand is played if fore hand wants to
	 */
	@Test
	public void testRamsch_ForcedForeHandGrandHand() {

		RamschTestPlayer grandHandPlayer = new RamschTestPlayer();
		grandHandPlayer.setPlayGrandHand(true);

		SkatGame game = new SkatGame("Table 1", GameVariant.RAMSCH, grandHandPlayer, //$NON-NLS-1$
				new AIPlayerRND(), new AIPlayerRND());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(Player.FOREHAND, game.getDeclarer());
		GameAnnouncement announcement = game.getGameAnnouncement();
		assertEquals(GameType.GRAND, announcement.getGameType());
		assertTrue(announcement.isHand());

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.GRAND, summary.getGameType());
	}

	/**
	 * Forced ramsch game, grand hand is played if middle hand wants to
	 */
	@Test
	public void testRamsch_ForcedMiddleHandGrandHand() {

		RamschTestPlayer grandHandPlayer = new RamschTestPlayer();
		grandHandPlayer.setPlayGrandHand(true);

		SkatGame game = new SkatGame("Table 1", GameVariant.RAMSCH, //$NON-NLS-1$
				new AIPlayerRND(), grandHandPlayer, new AIPlayerRND());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(Player.MIDDLEHAND, game.getDeclarer());
		GameAnnouncement announcement = game.getGameAnnouncement();
		assertEquals(GameType.GRAND, announcement.getGameType());
		assertTrue(announcement.isHand());

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.GRAND, summary.getGameType());
	}

	/**
	 * Forced ramsch game, grand hand is played if rear hand wants to
	 */
	@Test
	public void testRamsch_ForcedRearHandGrandHand() {

		RamschTestPlayer grandHandPlayer = new RamschTestPlayer();
		grandHandPlayer.setPlayGrandHand(true);

		SkatGame game = new SkatGame("Table 1", GameVariant.RAMSCH, //$NON-NLS-1$
				new AIPlayerRND(), new AIPlayerRND(), grandHandPlayer);
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(Player.REARHAND, game.getDeclarer());
		GameAnnouncement announcement = game.getGameAnnouncement();
		assertEquals(GameType.GRAND, announcement.getGameType());
		assertTrue(announcement.isHand());

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.GRAND, summary.getGameType());
	}
}
