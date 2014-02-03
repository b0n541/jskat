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

import org.jskat.control.JSkatThread;
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

/**
 * Helper class for simulating games
 */
class GameSimulationThread extends JSkatThread {

	private static Logger log = LoggerFactory
			.getLogger(GameSimulationThread.class);

	private final GameType gameType;
	private final Player position;
	private final CardList cards;
	private final CardList skat;

	private Long maxEpisodes;
	private Long maxTimestamp;

	private long simulatedGames;
	private long wonGames;

	private final AIPlayerNN nnPlayer1;
	private final AIPlayerNN nnPlayer2;
	private final AIPlayerNN nnPlayer3;

	GameSimulationThread(final GameType pGameType, final Player playerPosition,
			final CardList playerHandCards, final CardList skatCards) {

		gameType = pGameType;
		position = playerPosition;
		cards = new CardList(playerHandCards);
		skat = new CardList(skatCards);

		nnPlayer1 = new AIPlayerNN();
		nnPlayer1.setIsLearning(false);
		nnPlayer1.setLogger(NOPLogger.NOP_LOGGER);
		nnPlayer2 = new AIPlayerNN();
		nnPlayer2.setIsLearning(false);
		nnPlayer2.setLogger(NOPLogger.NOP_LOGGER);
		nnPlayer3 = new AIPlayerNN();
		nnPlayer3.setIsLearning(false);
		nnPlayer3.setLogger(NOPLogger.NOP_LOGGER);
	}

	void startSimulationWithMaxEpidodes(final Long episodes) {
		maxEpisodes = episodes;
		start();
	}

	void startSimulationWithTimestamp(final Long timestamp) {
		maxTimestamp = timestamp;
		start();
	}

	@Override
	public void run() {
		simulateGames();
	}

	private void simulateGames() {

		simulatedGames = 0;
		wonGames = 0;

		while (!isAllSimulationsDone()) {
			if (simulateGame()) {
				wonGames++;
			}
			simulatedGames++;
		}
	}

	private boolean isAllSimulationsDone() {

		if (maxEpisodes != null) {
			if (simulatedGames < maxEpisodes.longValue()) {
				return false;
			}
		} else if (maxTimestamp != null) {
			if (System.currentTimeMillis() < maxTimestamp.longValue()) {
				return false;
			}
		}
		log.warn(simulatedGames + " episodes simulated for game type "
				+ gameType + ".");
		return true;
	}

	private boolean simulateGame() {

		SkatGame game = new SkatGame("table", GameVariant.STANDARD, nnPlayer1,
				nnPlayer2, nnPlayer3);
		game.setView(new NullView());
		game.setLogger(NOPLogger.NOP_LOGGER);

		CardDeck deck = CardDeckSimulator.simulateUnknownCards(position, cards,
				skat);
		log.debug("Card deck: " + deck); //$NON-NLS-1$
		game.setCardDeck(deck);
		game.dealCards();

		game.setDeclarer(position);

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

		return game.isGameWon();
	}

	long getEpisodes() {
		return simulatedGames;
	}

	double getWonRate() {
		double wonDouble = wonGames;
		double gameCountDouble = simulatedGames;

		return wonDouble / gameCountDouble;
	}

	GameType getGameType() {
		return gameType;
	}
}
