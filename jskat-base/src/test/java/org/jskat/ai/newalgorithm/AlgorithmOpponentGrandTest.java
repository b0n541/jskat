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
package org.jskat.ai.newalgorithm;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.test.UnitTestPlayer;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.SkatGame;
import org.jskat.control.event.skatgame.TrickCardPlayedEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatGameResult;
import org.jskat.gui.UnitTestView;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.GameType;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public class AlgorithmOpponentGrandTest extends AbstractJSkatTest {

	private static final String TABLE_NAME = "Issue 57 Bug";

	@Before
	public void setUp() {
		JSkatOptions.instance().resetToDefault();
		JSkatEventBus.TABLE_EVENT_BUSSES.put(TABLE_NAME, new EventBus());
	}

	@Test
	public void testOpponentPlayerFollowsJackInGrandGame() {
		final UnitTestPlayer deterministicPlayer = new UnitTestPlayer();
		deterministicPlayer.setCardsToPlay(Lists.newArrayList(Card.SJ));
		final AlgorithmAI faultyPlayer = new AlgorithmAI();
		final JSkatPlayer helperPlayer = new UnitTestPlayer();

		deterministicPlayer.newGame(Player.FOREHAND);
		faultyPlayer.newGame(Player.MIDDLEHAND);
		helperPlayer.newGame(Player.REARHAND);

		final SkatGame skatGame = new SkatGame(TABLE_NAME, GameVariant.STANDARD,
				deterministicPlayer,
				faultyPlayer,
				helperPlayer);
		skatGame.setView(new UnitTestView());

		skatGame.setCardDeck(new CardDeck("SJ DJ CA CT CK CQ C9 C8 C7 SA",
				"CJ ST SK SQ S9 S8 S7 HA HT HK",
				"HJ HQ H9 H8 H7 DA DT DK DQ D9",
				"D8 D7"));
		skatGame.dealCards();

		skatGame.setDeclarer(Player.FOREHAND);
		final GameAnnouncementFactory announcementFactory = GameAnnouncement.getFactory();
		announcementFactory.setGameType(GameType.GRAND);
		final GameAnnouncement announcement = announcementFactory.getAnnouncement();
		skatGame.setGameAnnouncement(announcement);
		deterministicPlayer.startGame(Player.FOREHAND, announcement);
		faultyPlayer.startGame(Player.FOREHAND, announcement);
		helperPlayer.startGame(Player.FOREHAND, announcement);

		skatGame.setGameState(GameState.TRICK_PLAYING);

		final SkatGameResult gameResult = skatGame.run();
		assertThat(gameResult.isWon(), is(true));
		assertAlgorithmAIPlayerFollowsJack(skatGame);
	}

	private void assertAlgorithmAIPlayerFollowsJack(final SkatGame skatGame) {
		// first two game moves are card dealing and game announcement
		assertThat(skatGame.getGameMoves().get(2), is(new TrickCardPlayedEvent(Player.FOREHAND, Card.SJ)));
		assertThat(skatGame.getGameMoves().get(3), is(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.CJ)));
	}
}