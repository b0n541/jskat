/**
 * Copyright (C) 2017 Jan Schäfer (jansch@users.sourceforge.net)
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
package org.jskat.ai.nn;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.jskat.control.SkatGame;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatGameResult;
import org.jskat.gui.NullView;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

class GameSimulation {

	private final static Logger LOG = LoggerFactory.getLogger(GameSimulation.class);

	private final GameType gameType;
	private final Player playerPosition;
	private final CardList playerCards;
	private final CardList skatCards;

	private final Statistics statistics = new Statistics();

	private final JSkatPlayer player1;
	private final JSkatPlayer player2;
	private final JSkatPlayer player3;

	GameSimulation(final GameType gameType, final Player playerPosition, final CardList playerCards) {

		this(gameType, playerPosition, playerCards, new CardList());
	}

	GameSimulation(final GameType gameType, final Player playerPosition, final CardList playerCards,
			final CardList skatCards) {

		this.gameType = gameType;
		this.playerPosition = playerPosition;
		this.playerCards = new CardList(playerCards);
		this.skatCards = new CardList(skatCards);

		player1 = new AIPlayerNN(NOPLogger.NOP_LOGGER);
		player2 = new AIPlayerNN(NOPLogger.NOP_LOGGER);
		player3 = new AIPlayerNN(NOPLogger.NOP_LOGGER);
	}

	SkatGameResult simulateGame(final String tableName) {

		final SkatGame game = new SkatGame(tableName, GameVariant.STANDARD, player1, player2, player3);
		game.setView(new NullView());
		game.setLogger(NOPLogger.NOP_LOGGER);

		final CardDeck deck = CardDeckSimulator.simulateUnknownCards(playerPosition, playerCards, skatCards);
		LOG.debug("Simulated card deck: " + deck); //$NON-NLS-1$
		game.setCardDeck(deck);
		game.dealCards();

		game.setDeclarer(playerPosition);

		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(gameType);
		game.setGameAnnouncement(factory.getAnnouncement());

		game.setGameState(GameState.TRICK_PLAYING);

		SkatGameResult gameResult = null;
		try {
			gameResult = CompletableFuture.supplyAsync(() -> game.run()).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		statistics.adjust(gameResult);

		return gameResult;
	}

	long getEpisodes() {
		return statistics.getEpisodes();
	}

	long getWonGames() {
		return statistics.getWonGames();
	}

	double getWonRate() {
		return statistics.getWonRate();
	}

	long getWonGamesWithSchneider() {
		return statistics.getWonGamesWithSchneider();
	}

	double getWonRateWithSchneider() {
		return statistics.getWonRateWithSchneider();
	}

	long getWonGamesWithSchwarz() {
		return statistics.getWonGamesWithSchwarz();
	}

	double getWonRateWithSchwarz() {
		return statistics.getWonRateWithSchwarz();
	}

	double getDeclarerPointsMedian() {
		return statistics.getDeclarerPointsMedian();
	}

	public GameType getGameType() {
		return gameType;
	}

	public CardList getSkatCards() {
		return new CardList(skatCards);
	}

	@Override
	public String toString() {
		return "Game simulation " + gameType + " " + statistics.getEpisodes() + " episodes";
	}

	class Statistics {

		private long episodes;
		private long wonGames;
		private double wonRate;
		private long wonGamesWithSchneider;
		private double wonRateWithSchneider;
		private long wonGamesWithSchwarz;
		private double wonRateWithSchwarz;
		private final SynchronizedDescriptiveStatistics declarerPointsStats = new SynchronizedDescriptiveStatistics();

		void adjust(final SkatGameResult gameResult) {
			episodes++;
			if (gameResult.isWon()) {
				wonGames++;
				if (gameResult.isSchneider()) {
					wonGamesWithSchneider++;
				}
				if (gameResult.isSchwarz()) {
					wonGamesWithSchwarz++;
				}
			}
			wonRate = ((double) wonGames) / ((double) episodes);
			wonRateWithSchneider = ((double) wonGamesWithSchneider) / ((double) episodes);
			wonRateWithSchwarz = ((double) wonGamesWithSchwarz) / ((double) episodes);
			declarerPointsStats.addValue(gameResult.getFinalDeclarerPoints());
		}

		long getEpisodes() {
			return episodes;
		}

		long getWonGames() {
			return wonGames;
		}

		double getWonRate() {
			return wonRate;
		}

		long getWonGamesWithSchneider() {
			return wonGamesWithSchneider;
		}

		double getWonRateWithSchneider() {
			return wonRateWithSchneider;
		}

		long getWonGamesWithSchwarz() {
			return wonGamesWithSchwarz;
		}

		double getWonRateWithSchwarz() {
			return wonRateWithSchwarz;
		}

		double getDeclarerPointsMedian() {
			return declarerPointsStats.getPercentile(50);
		}
	}
}
