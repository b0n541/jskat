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
package org.jskat.control;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.gui.JSkatView;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls a series of skat games
 */
public class SkatSeries extends JSkatThread {

	private static Logger log = LoggerFactory.getLogger(SkatSeries.class);

	private int maxSleep = 0;
	private final SkatSeriesData data;
	private int roundsToGo = 0;
	private boolean unlimitedRounds = false;
	private boolean onlyPlayRamsch = false;
	private final Map<Player, JSkatPlayer> player;
	private SkatGame currSkatGame;

	private JSkatView view;

	/**
	 * Constructor
	 * 
	 * @param tableName
	 *            Table name
	 */
	public SkatSeries(final String tableName) {

		data = new SkatSeriesData();
		data.setState(SeriesState.WAITING);
		data.setTableName(tableName);
		setName("SkatSeries on table " + tableName); //$NON-NLS-1$
		player = new HashMap<Player, JSkatPlayer>();
	}

	/**
	 * Sets the skat players
	 * 
	 * @param newPlayer
	 *            New skat series player
	 */
	public void setPlayer(final List<JSkatPlayer> newPlayer) {

		if (newPlayer.size() != 3) {
			throw new IllegalArgumentException(
					"Only three players are allowed at the moment."); //$NON-NLS-1$
		}

		view.setPlayerNames(data.getTableName(), newPlayer.get(0)
				.getPlayerName(), newPlayer.get(1).getPlayerName(), newPlayer
				.get(2).getPlayerName());

		// memorize third player to find it again after shuffling the players
		JSkatPlayer thirdPlayer = newPlayer.get(2);

		// set players in random order
		// simple Collection.shuffle doesn't work here, because the order of
		// players should be the same like in start skat series dialog
		Random rand = new Random();
		int startPlayer = rand.nextInt(3);
		player.put(Player.FOREHAND, newPlayer.get(startPlayer));
		player.put(Player.MIDDLEHAND, newPlayer.get((startPlayer + 1) % 3));
		player.put(Player.REARHAND, newPlayer.get((startPlayer + 2) % 3));

		// if an human player is playing, always show him/her at the bottom
		// FIXME (jansch 09.05.2012) this is GUI logic, move it to the GUI
		// package
		for (Player hand : Player.values()) {
			if (player.get(hand).isHumanPlayer()
					|| player.get(hand) == thirdPlayer) {
				data.setBottomPlayer(hand);
			}
		}

		log.debug("Player order: " + player); //$NON-NLS-1$
	}

	/**
	 * Checks whether a series is running
	 * 
	 * @return TRUE if the series is running
	 */
	public boolean isRunning() {

		return data.getState() == SeriesState.RUNNING;
	}

	/**
	 * Starts the series
	 * 
	 * @param rounds
	 *            Number of rounds to be played
	 */
	public void setMaxRounds(final int rounds, final boolean newUnlimitedRound) {

		roundsToGo = rounds;
		unlimitedRounds = newUnlimitedRound;
		data.setState(SeriesState.RUNNING);
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {

		int roundsPlayed = 0;
		int gameNumber = 0;

		while ((roundsToGo > 0 || unlimitedRounds) && !isTerminated()) {

			log.debug("Playing round " + (roundsPlayed + 1)); //$NON-NLS-1$

			for (int j = 0; j < 3; j++) {

				if (j > 0 || roundsPlayed > 0) {
					// change player positions after first game
					JSkatPlayer helper = player.get(Player.REARHAND);
					player.put(Player.REARHAND, player.get(Player.FOREHAND));
					player.put(Player.FOREHAND, player.get(Player.MIDDLEHAND));
					player.put(Player.MIDDLEHAND, helper);

					data.setBottomPlayer(data.getBottomPlayer()
							.getRightNeighbor());
				}

				gameNumber++;
				view.setGameNumber(data.getTableName(), gameNumber);

				if (onlyPlayRamsch) {
					currSkatGame = new SkatGame(data.getTableName(),
							GameVariant.FORCED_RAMSCH,
							player.get(Player.FOREHAND),
							player.get(Player.MIDDLEHAND),
							player.get(Player.REARHAND));
				} else {
					currSkatGame = new SkatGame(data.getTableName(),
							GameVariant.STANDARD, player.get(Player.FOREHAND),
							player.get(Player.MIDDLEHAND),
							player.get(Player.REARHAND));
				}

				setViewPositions();

				currSkatGame.setView(view);
				currSkatGame.setMaxSleep(maxSleep);

				log.debug("Playing game " + (j + 1)); //$NON-NLS-1$

				data.addGame(currSkatGame);
				currSkatGame.start();
				try {
					currSkatGame.join();

					log.debug("Game ended: join"); //$NON-NLS-1$

					sleep(maxSleep);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (isHumanPlayerInvolved()) {
					// wait for human to start next game
					startWaiting();
				}

				checkWaitCondition();

				// Breaks on termination -
				// the thread has to come to the end
				if (isTerminated()) {
					break;
				}
			}

			roundsToGo--;
			roundsPlayed++;

			checkWaitCondition();
		}

		data.setState(SeriesState.SERIES_FINISHED);
		view.setSeriesState(data.getTableName(), SeriesState.SERIES_FINISHED);

		log.debug(data.getState().name());
	}

	private CardDeck getGrandGame() {
		CardList cards = new CardList();

		cards.addAll(Arrays.asList(Card.CT, Card.CQ, Card.C9)); // forehand
		cards.addAll(Arrays.asList(Card.CJ, Card.SJ, Card.CA)); // middlehand
		cards.addAll(Arrays.asList(Card.HJ, Card.DJ, Card.C7)); // rearhand
		cards.addAll(Arrays.asList(Card.H7, Card.D9)); // skat
		cards.addAll(Arrays.asList(Card.C8, Card.S7, Card.HT, Card.HQ)); // forehand
		cards.addAll(Arrays.asList(Card.CK, Card.ST, Card.SQ, Card.S9)); // middlehand
		cards.addAll(Arrays.asList(Card.SA, Card.SK, Card.S8, Card.HK)); // rearhand
		cards.addAll(Arrays.asList(Card.H8, Card.DT, Card.DK)); // forehand
		cards.addAll(Arrays.asList(Card.HA, Card.DA, Card.DQ)); // middlehand
		cards.addAll(Arrays.asList(Card.H9, Card.D8, Card.D7)); // rearhand

		return new CardDeck(cards);
	}

	private void setViewPositions() {

		String tableName = data.getTableName();

		if (Player.FOREHAND.equals(data.getBottomPlayer())) {

			view.setPositions(tableName, Player.MIDDLEHAND, Player.REARHAND,
					Player.FOREHAND);

		} else if (Player.MIDDLEHAND.equals(data.getBottomPlayer())) {

			view.setPositions(tableName, Player.REARHAND, Player.FOREHAND,
					Player.MIDDLEHAND);

		} else {

			view.setPositions(tableName, Player.FOREHAND, Player.MIDDLEHAND,
					Player.REARHAND);
		}
	}

	private boolean isHumanPlayerInvolved() {

		boolean result = false;

		for (JSkatPlayer currPlayer : player.values()) {
			if (currPlayer.isHumanPlayer()) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Gets the state of the series
	 * 
	 * @return State of the series
	 */
	public SeriesState getSeriesState() {

		return data.getState();
	}

	/**
	 * Gets the game state of the current game
	 * 
	 * @return Game state
	 */
	public GameState getGameState() {
		return data.getGameState();
	}

	/**
	 * Gets the ID of the current game
	 * 
	 * @return ID of the current game
	 */
	public int getCurrentGameID() {

		return data.getCurrentGameID();
	}

	/**
	 * Pauses the current game
	 */
	public void pauseSkatGame() {

		synchronized (currSkatGame) {

			currSkatGame.startWaiting();
		}
	}

	/**
	 * Resumes the current game
	 */
	public void resumeSkatGame() {

		synchronized (currSkatGame) {

			currSkatGame.stopWaiting();
			currSkatGame.notify();
		}
	}

	/**
	 * Checks whether the current skat game is paused
	 * 
	 * @return TRUE if the current skat game is paused
	 */
	public boolean isSkatGameWaiting() {

		return currSkatGame.isWaiting();
	}

	/**
	 * Sets the view for the series
	 * 
	 * @param newView
	 *            View
	 */
	public void setView(final JSkatView newView) {

		view = newView;
	}

	/**
	 * Sets whether only ramsch games are played or not
	 * 
	 * @param isOnlyPlayRamsch
	 *            TRUE, if only ramsch games should be played
	 */
	public void setOnlyPlayRamsch(final boolean isOnlyPlayRamsch) {
		onlyPlayRamsch = isOnlyPlayRamsch;
	}

	/**
	 * Sets max sleep between actions during the skat series, this must only be
	 * set in skat series that are run with a GUI, otherwise the default value
	 * of 0 is used
	 * 
	 * @param newMaxSleep
	 *            New value for maximum sleep time in milliseconds
	 */
	public void setMaxSleep(final int newMaxSleep) {

		maxSleep = newMaxSleep;
	}
}
