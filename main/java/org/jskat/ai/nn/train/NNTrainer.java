/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
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
package org.jskat.ai.nn.train;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jskat.ai.nn.AIPlayerNN;
import org.jskat.control.JSkatMaster;
import org.jskat.control.JSkatThread;
import org.jskat.control.SkatGame;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
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
 * Trains the neural networks
 */
public class NNTrainer extends JSkatThread {

	private static Logger log = LoggerFactory.getLogger(NNTrainer.class);

	final static String NEURAL_NETWORK_PLAYER_CLASS = "org.jskat.ai.nn.AIPlayerNN";

	private final JSkatMaster jskat;

	private final Random rand;

	private GameType gameType;

	private boolean stopTraining = false;

	/**
	 * Constructor
	 */
	public NNTrainer() {

		jskat = JSkatMaster.instance();

		rand = new Random();
	}

	/**
	 * Sets the game type to learn
	 * 
	 * @param newGameType
	 *            Game type
	 */
	public void setGameType(final GameType newGameType) {

		gameType = newGameType;
		setName("NNTrainer for " + gameType); //$NON-NLS-1$
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		trainNets();
	}

	/**
	 * Stops the training
	 * 
	 * @param isStopTraining
	 *            TRUE, if the training should be stopped
	 */
	public void stopTraining(boolean isStopTraining) {
		stopTraining = isStopTraining;
	}

	/**
	 * Trains the neural networks
	 */
	private void trainNets() {

		long totalGames = 0;
		long totalWonGames = 0;
		double declarerAvgNetworkErrorSum = 0.0;
		long declarerParticipations = 0;
		double opponentAvgNetworkErrorSum = 0.0;
		long opponentParticipations = 0;

		List<String> playerTypes = new ArrayList<String>();
		playerTypes.add(NEURAL_NETWORK_PLAYER_CLASS);
		Set<List<String>> playerPermutations = createPlayerPermutations(playerTypes);

		while (!stopTraining) {

			if (totalGames > 0) {
				if (opponentParticipations == 0) {
					// for ramsch games
					jskat.addTrainingResult(gameType, totalGames,
							totalWonGames, declarerAvgNetworkErrorSum
									/ declarerParticipations, 0.0);
				} else {
					jskat.addTrainingResult(gameType, totalGames,
							totalWonGames, declarerAvgNetworkErrorSum
									/ declarerParticipations,
							opponentAvgNetworkErrorSum / opponentParticipations);
				}
			}

			for (List<String> playerConstellation : playerPermutations) {

				for (Player declarer : Player.values()) {
					JSkatPlayer player1 = createPlayer(playerConstellation
							.get(0));
					JSkatPlayer player2 = createPlayer(playerConstellation
							.get(1));
					JSkatPlayer player3 = createPlayer(playerConstellation
							.get(2));

					SkatGame game = prepareGame(player1, player2, player3,
							declarer, null);
					// SkatGame game = prepareGame(player1, player2, player3,
					// Player.FOREHAND, getPerfectDistribution());

					runGame(game);

					if (isGameWon(declarer, game)) {
						log.debug("Game won.");
						totalWonGames++;
					} else {
						log.debug("Game lost.");
					}
					if (player1 instanceof AIPlayerNN) {
						if (player1.isDeclarer()) {
							declarerAvgNetworkErrorSum += ((AIPlayerNN) player1)
									.getLastAvgNetworkError();
							declarerParticipations++;
						} else {
							opponentAvgNetworkErrorSum += ((AIPlayerNN) player1)
									.getLastAvgNetworkError();
							opponentParticipations++;
						}
					}
					if (player2 instanceof AIPlayerNN) {
						if (player2.isDeclarer()) {
							declarerAvgNetworkErrorSum += ((AIPlayerNN) player2)
									.getLastAvgNetworkError();
							declarerParticipations++;
						} else {
							opponentAvgNetworkErrorSum += ((AIPlayerNN) player2)
									.getLastAvgNetworkError();
							opponentParticipations++;
						}
					}
					if (player3 instanceof AIPlayerNN) {
						if (player3.isDeclarer()) {
							declarerAvgNetworkErrorSum += ((AIPlayerNN) player3)
									.getLastAvgNetworkError();
							declarerParticipations++;
						} else {
							opponentAvgNetworkErrorSum += ((AIPlayerNN) player3)
									.getLastAvgNetworkError();
							opponentParticipations++;
						}
					}

					totalGames++;
				}
			}

			checkWaitCondition();
		}
	}

	private CardDeck getPerfectDistribution() {
		return new CardDeck(
				"CJ SJ HJ CK CQ SK C7 C8 S7 H7 D7 DJ CA CT C9 SQ HA HK HQ S8 H8 H9 HT SA ST S9 D8 D9 DT DA DK DQ");
	}

	private JSkatPlayer createPlayer(String playerType) {

		JSkatPlayer player = JSkatMaster.instance().createPlayer(playerType);

		if (NEURAL_NETWORK_PLAYER_CLASS.equals(playerType)) {
			AIPlayerNN nnPlayer = (AIPlayerNN) player;
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

	private void runGame(final SkatGame game) {
		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private SkatGame prepareGame(final JSkatPlayer player1,
			final JSkatPlayer player2, final JSkatPlayer player3,
			final Player declarer, final CardDeck cardDeck) {
		player1.newGame(Player.FOREHAND);
		player2.newGame(Player.MIDDLEHAND);
		player3.newGame(Player.REARHAND);
		SkatGame game = new SkatGame("table", GameVariant.STANDARD, player1,
				player2, player3);
		game.setView(new NullView());
		game.setLogger(NOPLogger.NOP_LOGGER);

		if (cardDeck != null) {
			game.setCardDeck(cardDeck);
		} else {
			CardDeck newCardDeck = new CardDeck();
			newCardDeck.shuffle();
			log.debug("Card deck: " + newCardDeck); //$NON-NLS-1$
			game.setCardDeck(newCardDeck);
		}

		game.dealCards();

		if (!GameType.RAMSCH.equals(gameType)) {
			game.setDeclarer(declarer);
		}

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(gameType);
		GameAnnouncement announcement = factory.getAnnouncement();
		game.setGameAnnouncement(announcement);

		player1.startGame(declarer, announcement);
		player2.startGame(declarer, announcement);
		player3.startGame(declarer, announcement);

		game.setGameState(GameState.TRICK_PLAYING);
		return game;
	}

	static Set<List<String>> createPlayerPermutations(List<String> playerTypes) {

		Set<List<String>> result = new HashSet<List<String>>();

		for (String player1 : playerTypes) {
			for (String player2 : playerTypes) {
				for (String player3 : playerTypes) {

					if (NEURAL_NETWORK_PLAYER_CLASS.equals(player1)
							|| NEURAL_NETWORK_PLAYER_CLASS.equals(player2)
							|| NEURAL_NETWORK_PLAYER_CLASS.equals(player3)) {

						List<String> playerPermutation = new ArrayList<String>();
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
	private static boolean isRamschGameWon(final GameSummary gameSummary,
			final Player currPlayer) {

		boolean ramschGameWon = false;
		int playerPoints = gameSummary.getPlayerPoints(currPlayer);
		int highestPlayerPoints = 0;
		for (Player player : Player.values()) {
			int currPlayerPoints = gameSummary.getPlayerPoints(player);

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
