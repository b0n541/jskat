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
package org.jskat.control;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jskat.control.event.table.SkatSeriesStartedEvent;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.SkatTableOptions;
import org.jskat.player.JSkatPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls a table for playing a series of skat games
 */
public class SkatTable {

	private static Logger log = LoggerFactory.getLogger(SkatTable.class);

	private final String tableName;
	private final SkatTableOptions options;
	private SkatSeries series;
	private final List<JSkatPlayer> player = new ArrayList<JSkatPlayer>();

	/**
	 * Constructor
	 *
	 * @param tableName
	 *            Table name
	 * @param options
	 *            Preferences for the table
	 */
	public SkatTable(final String tableName, final SkatTableOptions options) {

		this.tableName = tableName;
		this.options = options;

		log.debug("SkatTable created with max. " + options.getMaxPlayerCount() + " players."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Checks whether a skat series is running or not
	 *
	 * @return TRUE if the series is running
	 */
	public boolean isSeriesRunning() {

		return series != null && series.isRunning();
	}

	/**
	 * Starts a skat series
	 *
	 * @param rounds
	 *            Number of rounds to be played
	 * @param unlimitedRounds
	 *            TRUE, if the number of rounds is not limited
	 * @param onlyPlayRamsch
	 *            TRUE, if only Ramsch games should be played
	 * @param sleepBetweenMoves
	 *            Number of miliseconds to sleep between moves
	 */
	public void startSkatSeries(final int rounds,
			final boolean unlimitedRounds, final boolean onlyPlayRamsch,
			final int sleepBetweenMoves) {

		if (!isSeriesRunning()) {

			series = new SkatSeries(tableName);

			if (sleepBetweenMoves > 0) {
				// set max sleep time only when using GUI
				series.setMaxSleep(sleepBetweenMoves);
			}
		}

		if (player.size() >= 3) {

			series.setPlayers(player);
			series.setOnlyPlayRamsch(onlyPlayRamsch);
			series.setMaxRounds(rounds, unlimitedRounds);
			CompletableFuture.runAsync(() -> series.run());
		}

		JSkatEventBus.INSTANCE.post(new SkatSeriesStartedEvent(tableName));
	}

	/**
	 * Gets the maximal number of players allowed at the table
	 *
	 * @return Maximal number of players
	 */
	public int getMaxPlayerCount() {

		return options.getMaxPlayerCount();
	}

	/**
	 * Gets the current number of players sitting at the table
	 *
	 * @return Current number of players
	 */
	public int getPlayerCount() {

		return player.size();
	}

	/**
	 * Places a player at the table
	 *
	 * @param newPlayer
	 *            New Player
	 * @return TRUE if the player was placed correctly
	 */
	public boolean placePlayer(final JSkatPlayer newPlayer) {

		boolean result = false;

		if (player.size() < options.getMaxPlayerCount()) {

			player.add(newPlayer);

			result = true;
		}

		return result;
	}

	/**
	 * Removes all players from the table
	 */
	public void removePlayers() {

		player.clear();
	}

	/**
	 * Gets the state of the skat series
	 *
	 * @return State of the skat series
	 */
	public SeriesState getSeriesState() {

		return series.getSeriesState();
	}

	/**
	 * Gets the game state of the current game
	 *
	 * @return Game state
	 */
	public GameState getGameState() {
		if (series != null) {
			return series.getGameState();
		}
		return GameState.GAME_START;
	}

	/**
	 * Gets the ID of the current game
	 *
	 * @return Game ID of the current game
	 */
	public int getCurrentGameID() {

		return series.getCurrentGameID();
	}

	/**
	 * Gets table name
	 *
	 * @return Table name
	 */
	public String getName() {

		return tableName;
	}
}
