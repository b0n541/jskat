/**
 * Copyright (C) 2018 Jan Schäfer (jansch@users.sourceforge.net)
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
package org.jskat.ai.nn.train;

import java.util.ArrayList;
import java.util.List;

import org.jskat.ai.nn.AIPlayerNN;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.SkatGame;
import org.jskat.control.command.table.CreateTableCommand;
import org.jskat.control.command.table.RemoveTableCommand;
import org.jskat.control.event.nntraining.TrainingResultEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.data.JSkatViewType;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.NullView;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.CardDeck;
import org.jskat.util.GameType;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

/**
 * Trains the neural networks.
 */
public class NNTrainer {

	private static Logger log = LoggerFactory.getLogger(NNTrainer.class);

	private static final Integer MAX_TRAINING_EPISODES = 1000000;
	private static final Integer MAX_TRAINING_EPISODES_WITHOUT_SAVE = 100000;

	static final String NEURAL_NETWORK_PLAYER_CLASS = "org.jskat.ai.nn.AIPlayerNN";
	private static final String RANDOM_PLAYER_CLASS = "org.jskat.ai.rnd.AIPlayerRND";
	private static final String ALGORITHMIC_PLAYER_CLASS = "org.jskat.ai.newalgorithm.AlgorithmAI";

	private static final List<String> PLAYER_TYPES = new ArrayList<>();

	static {
		PLAYER_TYPES.add(NEURAL_NETWORK_PLAYER_CLASS);
		// PLAYER_TYPES.add(RANDOM_PLAYER_CLASS);
		// PLAYER_TYPES.add(ALGORITHMIC_PLAYER_CLASS);
	}

	private GameType gameType;

	private boolean stopTraining = false;

	/**
	 * Sets the game type to learn
	 *
	 * @param newGameType
	 *            Game type
	 */
	public void setGameType(final GameType newGameType) {

		gameType = newGameType;
		JSkatEventBus.INSTANCE.post(new CreateTableCommand(JSkatViewType.TRAINING_TABLE, "TRAIN" + gameType.name()));
	}

	/**
	 * Stops the training
	 *
	 * @param isStopTraining
	 *            TRUE, if the training should be stopped
	 */
	public void stopTraining(final boolean isStopTraining) {
		stopTraining = isStopTraining;
		JSkatEventBus.INSTANCE.post(new RemoveTableCommand(JSkatViewType.TRAINING_TABLE, "TRAIN" + gameType.name()));
	}

	private JSkatPlayer createPlayer(final String playerType) {

		final JSkatPlayer player = JSkatMaster.INSTANCE.createPlayer(playerType);

		if (NEURAL_NETWORK_PLAYER_CLASS.equals(playerType)) {
			final AIPlayerNN nnPlayer = (AIPlayerNN) player;
			nnPlayer.setIsLearning(true);
			nnPlayer.setLogger(NOPLogger.NOP_LOGGER);
		}

		return player;
	}

	private boolean isGameWon(final Player currPlayer, final SkatGame game) {

		// FIXME (jansch 28.06.2011) have to call getGameResult() to get
		// the result
		game.getGameResult();

		boolean gameWon = false;
		if (gameType.equals(GameType.RAMSCH)) {
			gameWon = isRamschGameWon(game.getGameSummary(), currPlayer);
		} else {
			gameWon = game.isGameWon();
		}
		return gameWon;
	}

	private SkatGame prepareGame(final JSkatPlayer player1, final JSkatPlayer player2, final JSkatPlayer player3,
			final Player declarer, final CardDeck cardDeck) {
		player1.newGame(Player.FOREHAND);
		player2.newGame(Player.MIDDLEHAND);
		player3.newGame(Player.REARHAND);
		final SkatGame game = new SkatGame("TRAIN" + gameType.name(), GameVariant.STANDARD, player1, player2, player3);
		game.setView(new NullView());
		game.setLogger(NOPLogger.NOP_LOGGER);

		if (cardDeck != null) {
			game.setCardDeck(cardDeck);
		} else {
			final CardDeck newCardDeck = new CardDeck();
			newCardDeck.shuffle();
			log.debug("Card deck: " + newCardDeck); //$NON-NLS-1$
			game.setCardDeck(newCardDeck);
		}

		game.dealCards();

		if (!GameType.RAMSCH.equals(gameType)) {
			game.setDeclarer(declarer);
		}

		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(gameType);
		final GameAnnouncement announcement = factory.getAnnouncement();
		game.setGameAnnouncement(announcement);

		player1.startGame(declarer, announcement);
		player2.startGame(declarer, announcement);
		player3.startGame(declarer, announcement);

		game.setGameState(GameState.TRICK_PLAYING);
		return game;
	}

	/**
	 * Trains the neural networks
	 */
	public void trainNets() {

		long totalGames = 0;
		long totalWonGames = 0;
		double declarerAvgNetworkError = 0.0;
		double opponentAvgNetworkError = 0.0;

		final List<List<String>> playerPermutations = createPlayerPermutations(PLAYER_TYPES);

		while (!stopTraining /* && totalGames < MAX_TRAINING_EPISODES */) {

			if (totalGames > 0) {

				if (totalGames % MAX_TRAINING_EPISODES_WITHOUT_SAVE == 0) {
					JSkatMaster.INSTANCE.saveNeuralNetworks(gameType);
				}

				if (GameType.RAMSCH.equals(gameType)) {
					JSkatEventBus.INSTANCE.post(new TrainingResultEvent(gameType, totalGames, totalWonGames,
							declarerAvgNetworkError, 0.0));
				} else {
					JSkatEventBus.INSTANCE.post(new TrainingResultEvent(gameType, totalGames, totalWonGames,
							declarerAvgNetworkError, opponentAvgNetworkError));
				}
			}

			for (final List<String> playerConstellation : playerPermutations) {

				for (final Player declarer : Player.values()) {
					final JSkatPlayer player1 = createPlayer(playerConstellation.get(0));
					final JSkatPlayer player2 = createPlayer(playerConstellation.get(1));
					final JSkatPlayer player3 = createPlayer(playerConstellation.get(2));

					final SkatGame game = prepareGame(player1, player2, player3, declarer, null);
					// final SkatGame game = prepareGame(player1, player2, player3,
					// Player.FOREHAND,
					// CardDeck.getPerfectDistribution());

					game.run();

					if (isGameWon(declarer, game)) {
						log.debug("Game won.");
						totalWonGames++;
					} else {
						log.debug("Game lost.");
					}

					if (player1 instanceof AIPlayerNN) {
						if (player1.isDeclarer()) {
							declarerAvgNetworkError = ((AIPlayerNN) player1).getLastAvgDeclarerNetworkError();
						} else {
							opponentAvgNetworkError = ((AIPlayerNN) player1).getLastAvgOpponentNetworkError();
						}
					}
					if (player2 instanceof AIPlayerNN) {
						if (player2.isDeclarer()) {
							declarerAvgNetworkError = ((AIPlayerNN) player2).getLastAvgDeclarerNetworkError();
						} else {
							opponentAvgNetworkError = ((AIPlayerNN) player2).getLastAvgOpponentNetworkError();
						}
					}
					if (player3 instanceof AIPlayerNN) {
						if (player3.isDeclarer()) {
							declarerAvgNetworkError = ((AIPlayerNN) player3).getLastAvgDeclarerNetworkError();
						} else {
							opponentAvgNetworkError = ((AIPlayerNN) player3).getLastAvgOpponentNetworkError();
						}
					}

					totalGames++;
				}
			}
		}
	}

	static List<List<String>> createPlayerPermutations(final List<String> playerTypes) {

		final List<List<String>> result = new ArrayList<>();

		for (final String player1 : playerTypes) {
			for (final String player2 : playerTypes) {
				for (final String player3 : playerTypes) {

					if (NEURAL_NETWORK_PLAYER_CLASS.equals(player1) || NEURAL_NETWORK_PLAYER_CLASS.equals(player2)
							|| NEURAL_NETWORK_PLAYER_CLASS.equals(player3)) {

						final List<String> playerPermutation = new ArrayList<>();
						playerPermutation.add(player1);
						playerPermutation.add(player2);
						playerPermutation.add(player3);

						result.add(playerPermutation);
					}
				}
			}
		}

		return result;
	}

	// FIXME (jan 10.03.2012) code duplication with AIPlayerNN
	private static boolean isRamschGameWon(final GameSummary gameSummary, final Player currPlayer) {

		boolean ramschGameWon = false;
		final int playerPoints = gameSummary.getPlayerPoints(currPlayer);
		int highestPlayerPoints = 0;
		for (final Player player : Player.values()) {
			final int currPlayerPoints = gameSummary.getPlayerPoints(player);

			if (currPlayerPoints > highestPlayerPoints) {
				highestPlayerPoints = currPlayerPoints;
			}
		}

		if (highestPlayerPoints > playerPoints) {
			ramschGameWon = true;
		}

		return ramschGameWon;
	}
}
