/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn;

import org.jskat.control.SkatGame;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.NullView;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

public class GameSimulation {

	private final static Logger LOG = LoggerFactory
			.getLogger(GameSimulation.class);

	final GameType gameType;
	private final Player playerPosition;
	private final CardList playerCards;
	private final CardList skatCards;

	private long simulatedGames;
	private long wonGames;
	private double wonRate;

	GameSimulation(GameType gameType, Player playerPosition,
			CardList playerCards) {

		this(gameType, playerPosition, playerCards, new CardList());
	}

	GameSimulation(GameType gameType, Player playerPosition,
			CardList playerCards, CardList skatCards) {

		this.gameType = gameType;
		this.playerPosition = playerPosition;
		this.playerCards = new CardList(playerCards);
		this.skatCards = new CardList(skatCards);
	}

	void simulateGame(String tableName, AIPlayerNN nnPlayer1,
			AIPlayerNN nnPlayer2, AIPlayerNN nnPlayer3) {

		SkatGame game = new SkatGame(tableName, GameVariant.STANDARD, nnPlayer1,
				nnPlayer2, nnPlayer3);
		game.setView(new NullView());
		game.setLogger(NOPLogger.NOP_LOGGER);

		CardDeck deck = CardDeckSimulator.simulateUnknownCards(playerPosition,
				playerCards, skatCards);
		LOG.debug("Card deck: " + deck); //$NON-NLS-1$
		game.setCardDeck(deck);
		game.dealCards();

		game.setDeclarer(playerPosition);

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(gameType);
		game.setGameAnnouncement(factory.getAnnouncement());

		game.setGameState(GameState.TRICK_PLAYING);

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// FIXME (jansch 28.06.2011) have to call getGameResult() for result
		// calculation
		game.getGameResult();

		simulatedGames++;
		if (game.isGameWon()) {
			wonGames++;
		}
		wonRate = ((double) wonGames) / ((double) simulatedGames);
	}

	long getSimulatedGames() {
		return simulatedGames;
	}

	double getWonRate() {
		return wonRate;
	}
}
