/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.ai.nn.train;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.NoOpLog;
import org.jskat.ai.PlayerType;
import org.jskat.ai.nn.AIPlayerNN;
import org.jskat.control.JSkatMaster;
import org.jskat.control.JSkatPlayer;
import org.jskat.control.JSkatThread;
import org.jskat.control.SkatGame;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.gui.NullView;
import org.jskat.util.CardDeck;
import org.jskat.util.GameType;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;

/**
 * Trains the neural networks
 */
public class NNTrainer extends JSkatThread {

	private static Log log = LogFactory.getLog(NNTrainer.class);

	private final JSkatMaster jskat;

	private final Random rand;
	private final List<StringBuffer> nullGames;

	private GameType gameType;

	/**
	 * Constructor
	 */
	public NNTrainer() {

		jskat = JSkatMaster.instance();

		rand = new Random();
		nullGames = new ArrayList<StringBuffer>();

		initLearningPatterns();
	}

	private void initLearningPatterns() {

		// test a perfect null game
		StringBuffer buffer = new StringBuffer();
		buffer.append("CA CT CK "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("C9 ST SK "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("H7 DA DT "); // 3 cards hind hand //$NON-NLS-1$
		buffer.append("SA HA "); // skat //$NON-NLS-1$
		buffer.append("CQ CJ C8 C7 "); // 4 cards fore hand //$NON-NLS-1$
		buffer.append("SQ SJ HK HQ "); // 4 cards middle hand //$NON-NLS-1$
		buffer.append("DK DQ D9 D8 "); // 4 cards hind hand //$NON-NLS-1$
		buffer.append("S9 S8 S7 "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("HJ H9 H8 "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("D7 HT DJ"); // 3 cards hind hand //$NON-NLS-1$
		nullGames.add(buffer);

		buffer = new StringBuffer();
		buffer.append("CA CT CK "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("S9 ST SK "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("H7 DA DT "); // 3 cards hind hand //$NON-NLS-1$
		buffer.append("SA HA "); // skat //$NON-NLS-1$
		buffer.append("CQ CJ C8 C7 "); // 4 cards fore hand //$NON-NLS-1$
		buffer.append("SQ SJ HK HQ "); // 4 cards middle hand //$NON-NLS-1$
		buffer.append("DK DQ D9 D8 "); // 4 cards hind hand //$NON-NLS-1$
		buffer.append("C9 S8 S7 "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("HJ H9 H8 "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("D7 HT DJ"); // 3 cards hind hand //$NON-NLS-1$
		nullGames.add(buffer);

		buffer = new StringBuffer();
		buffer.append("C9 C8 C7 "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("CA CT CK "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("CQ CJ SA "); // 3 cards hind hand //$NON-NLS-1$
		buffer.append("SJ SK "); // skat //$NON-NLS-1$
		buffer.append("ST S9 S8 S7 "); // 4 cards fore hand //$NON-NLS-1$
		buffer.append("SQ HA HT HK "); // 4 cards middle hand //$NON-NLS-1$
		buffer.append("HQ DA DT DK "); // 4 cards hind hand //$NON-NLS-1$
		buffer.append("H9 H8 H7 "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("DQ DJ D9 "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("D8 D7 HJ"); // 3 cards hind hand //$NON-NLS-1$
		nullGames.add(buffer);

		buffer = new StringBuffer();
		buffer.append("SA ST SK "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("S9 CT CK "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("H7 DA DT "); // 3 cards hind hand //$NON-NLS-1$
		buffer.append("CA HA "); // skat //$NON-NLS-1$
		buffer.append("SQ SJ S8 S7 "); // 4 cards fore hand //$NON-NLS-1$
		buffer.append("CQ CJ HK HQ "); // 4 cards middle hand //$NON-NLS-1$
		buffer.append("DK DQ D9 D8 "); // 4 cards hind hand //$NON-NLS-1$
		buffer.append("C9 C8 C7 "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("HJ H9 H8 "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("D7 HT DJ"); // 3 cards hind hand //$NON-NLS-1$
		nullGames.add(buffer);

		buffer = new StringBuffer();
		buffer.append("HA HT HK "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("H9 CT CK "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("S7 DA DT "); // 3 cards hind hand //$NON-NLS-1$
		buffer.append("CA SA "); // skat //$NON-NLS-1$
		buffer.append("HQ HJ H8 H7 "); // 4 cards fore hand //$NON-NLS-1$
		buffer.append("CQ CJ SK SQ "); // 4 cards middle hand //$NON-NLS-1$
		buffer.append("DK DQ D9 D8 "); // 4 cards hind hand //$NON-NLS-1$
		buffer.append("S9 S8 S7 "); // 3 cards fore hand //$NON-NLS-1$
		buffer.append("SJ S9 S8 "); // 3 cards middle hand //$NON-NLS-1$
		buffer.append("D7 ST DJ"); // 3 cards hind hand //$NON-NLS-1$
		nullGames.add(buffer);
	}

	/**
	 * Sets the game type to learn
	 * 
	 * @param newGameType
	 *            Game type
	 */
	public void setGameType(final GameType newGameType) {

		gameType = newGameType;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		trainNets();
	}

	/**
	 * Trains the neural networks
	 */
	private void trainNets() {

		long episodes = 0;
		long episodesWonGames = 0;
		long totalWonGames = 0;
		long totalGames = 0;
		int episodeSteps = 100;

		while (true) {

			if (totalGames > 0) {
				jskat.addTrainingResult(gameType, totalGames, totalWonGames, episodesWonGames, 0.0);
			}

			List<PlayerType> playerTypes = new ArrayList<PlayerType>();
			// playerTypes.add(PlayerType.ALGORITHMIC);
			// playerTypes.add(PlayerType.RANDOM);
			playerTypes.add(PlayerType.NEURAL_NETWORK);

			for (List<PlayerType> playerConstellation : createPlayerPermutations(playerTypes)) {

				for (Player currPlayer : Player.values()) {

					JSkatPlayer player1 = createPlayer(playerConstellation.get(0));
					JSkatPlayer player2 = createPlayer(playerConstellation.get(1));
					JSkatPlayer player3 = createPlayer(playerConstellation.get(2));

					SkatGame game = prepareGame(player1, player2, player3, currPlayer);

					runGame(game);

					if (isGameWon(currPlayer, game)) {
						episodesWonGames++;
						totalWonGames++;
					}

					totalGames++;
				}
			}

			checkWaitCondition();
		}
	}

	private JSkatPlayer createPlayer(final PlayerType playerType) {
		JSkatPlayer player = PlayerType.getPlayerInstance(playerType);

		if (PlayerType.NEURAL_NETWORK.equals(playerType)) {
			AIPlayerNN nnPlayer = (AIPlayerNN) player;
			nnPlayer.setIsLearning(true);
			nnPlayer.setLogger(new NoOpLog());
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

	private SkatGame prepareGame(final JSkatPlayer player1, final JSkatPlayer player2, final JSkatPlayer player3,
			final Player declarer) {
		player1.newGame(Player.FOREHAND);
		player2.newGame(Player.MIDDLEHAND);
		player3.newGame(Player.REARHAND);
		SkatGame game = new SkatGame("table", GameVariant.STANDARD, player1, player2, player3);
		game.setView(new NullView());
		game.setLogger(new NoOpLog());

		CardDeck deck = new CardDeck();
		deck.shuffle();
		log.debug("Card deck: " + deck); //$NON-NLS-1$
		game.setCardDeck(deck);
		game.dealCards();

		if (!GameType.RAMSCH.equals(gameType)) {
			game.setDeclarer(declarer);
		}

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(gameType);
		GameAnnouncement announcement = factory.getAnnouncement();
		game.setGameAnnouncement(announcement);

		game.setGameState(GameState.TRICK_PLAYING);
		return game;
	}

	static Set<List<PlayerType>> createPlayerPermutations(final List<PlayerType> playerTypes) {

		Set<List<PlayerType>> result = new HashSet<List<PlayerType>>();

		for (PlayerType player1 : playerTypes) {
			for (PlayerType player2 : playerTypes) {
				for (PlayerType player3 : playerTypes) {

					if (player1 == PlayerType.NEURAL_NETWORK || player2 == PlayerType.NEURAL_NETWORK
							|| player3 == PlayerType.NEURAL_NETWORK) {

						List<PlayerType> playerPermutation = new ArrayList<PlayerType>();
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
