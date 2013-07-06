/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
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
package org.jskat.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.ai.test.NoBiddingTestPlayer;
import org.jskat.ai.test.RamschTestPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatGameResult;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.data.Trick;
import org.jskat.gui.UnitTestView;
import org.jskat.player.JSkatPlayer;
import org.jskat.player.UnitTestPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.GameType;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;
import org.junit.Ignore;
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

		SkatGame game = new SkatGame(
				"Table 1", GameVariant.STANDARD, new NoBiddingTestPlayer(), //$NON-NLS-1$
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

		SkatGame game = new SkatGame(
				"Table 1", GameVariant.STANDARD, getNoBiddingPlayer(), //$NON-NLS-1$
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

		SkatGame game = new SkatGame(
				"Table 1", GameVariant.STANDARD, new NoBiddingTestPlayer(), //$NON-NLS-1$
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

		SkatGame game = new SkatGame(
				"Table 1", GameVariant.STANDARD, new RamschTestPlayer(), //$NON-NLS-1$
				new RamschTestPlayer(), new RamschTestPlayer());
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

		SkatGame game = new SkatGame(
				"Table 1", GameVariant.RAMSCH, new RamschTestPlayer(), //$NON-NLS-1$
				new RamschTestPlayer(), new RamschTestPlayer());
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

		SkatGame game = new SkatGame(
				"Table 1", GameVariant.RAMSCH, grandHandPlayer, //$NON-NLS-1$
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
				new RamschTestPlayer(), grandHandPlayer, new RamschTestPlayer());
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
				new RamschTestPlayer(), new RamschTestPlayer(), grandHandPlayer);
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

	@Test
	public void testCompleteGame() {
		SkatGame game = new SkatGame("Table 1", GameVariant.STANDARD, //$NON-NLS-1$
				new AIPlayerRND(), new AIPlayerRND(), new AIPlayerRND());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Player declarer = game.getDeclarer();
		SkatGameResult result = game.getGameResult();
		if (declarer != null && result.getGameValue() > 0) {
			assertEquals(
					120,
					result.getFinalDeclarerPoints()
							+ result.getFinalOpponentPoints());
			GameSummary summary = game.getGameSummary();

			Map<Player, Integer> playerPointsInTricks = new HashMap<Player, Integer>();
			for (Trick trick : summary.getTricks()) {

				Integer playerPoints = playerPointsInTricks.get(trick
						.getTrickWinner());
				if (playerPoints == null) {
					playerPoints = 0;
				}
				playerPoints = playerPoints + trick.getValue();
				playerPointsInTricks.put(trick.getTrickWinner(), playerPoints);
			}
			assertEquals(
					result.getFinalOpponentPoints(),
					playerPointsInTricks.get(declarer.getRightNeighbor())
							+ playerPointsInTricks.get(declarer
									.getLeftNeighbor()));
		}
	}

	@Test
	@Ignore
	public void testPredefinedCardPlaying() {
		UnitTestPlayer foreHand = new UnitTestPlayer();
		foreHand.setCardsToPlay(Arrays.asList(Card.C7, Card.SJ, Card.C9,
				Card.H8, Card.DQ, Card.D7, Card.CT, Card.DK, Card.CA, Card.CK));

		UnitTestPlayer middleHand = new UnitTestPlayer();
		foreHand.setCardsToPlay(Arrays.asList(Card.CQ, Card.C8, Card.SQ,
				Card.ST, Card.S9, Card.HT, Card.HA, Card.DA, Card.HK, Card.D9));

		UnitTestPlayer rearHand = new UnitTestPlayer();
		foreHand.setCardsToPlay(Arrays.asList(Card.DJ, Card.CJ, Card.HJ,
				Card.SA, Card.S8, Card.H7, Card.H9, Card.D8, Card.HQ, Card.DT));

		SkatGame game = new SkatGame("Table 1", GameVariant.STANDARD, //$NON-NLS-1$
				foreHand, middleHand, rearHand);
		game.setView(new UnitTestView());

		CardDeck deck = new CardDeck(
				"SJ CA CT CQ C8 ST CJ HJ DJ SK S7 CK C9 C7 H8 SQ S9 HA HT SA S8 HQ H9 DK DO D7 HK DA D9 H7 DT D8");
		game.setCardDeck(deck);
		game.dealCards();
		game.setDeclarer(Player.MIDDLEHAND);
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.CLUBS);
		game.setGameAnnouncement(factory.getAnnouncement());
		game.setGameState(GameState.TRICK_PLAYING);

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SkatGameResult result = game.getGameResult();
		assertEquals(28, result.getFinalDeclarerPoints());
		assertEquals(28, result.getFinalOpponentPoints());
	}
}
