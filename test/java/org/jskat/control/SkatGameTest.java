/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
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
package org.jskat.control;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.jskat.player.JSkatPlayer;
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
	 * When no player bids, game is passed in
	 */
	@Test
	public void testPassIn_NoBidsMockito() {

		JSkatOptions options = JSkatOptions.instance();
		options.setRules(RuleSet.ISPA);

		SkatGame game = new SkatGame("Table 1", GameVariant.STANDARD, getNoBiddingPlayer(), //$NON-NLS-1$
				getNoBiddingPlayer(), getNoBiddingPlayer());
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

	private JSkatPlayer getNoBiddingPlayer() {
		JSkatPlayer player = mock(JSkatPlayer.class);
		when(player.bidMore(anyInt())).thenReturn(-1);
		when(player.holdBid(anyInt())).thenReturn(false);
		return player;
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
