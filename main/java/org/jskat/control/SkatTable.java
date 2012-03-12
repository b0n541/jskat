/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
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
package org.jskat.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.JSkatPlayer;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.SkatTableOptions;
import org.jskat.gui.JSkatView;
import org.jskat.gui.JSkatViewImpl;

/**
 * Controls a table for playing a series of skat games
 */
public class SkatTable {

	private static Log log = LogFactory.getLog(SkatTable.class);

	private String tableName;
	private SkatTableOptions options;
	private SkatSeries series;
	private List<JSkatPlayer> player = new ArrayList<JSkatPlayer>();

	private JSkatView view;

	/**
	 * Constructor
	 * 
	 * @param tableOptions
	 *            Preferences for the table
	 */
	public SkatTable(SkatTableOptions tableOptions) {

		options = tableOptions;

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
	 * @param onlyPlayRamsch
	 */
	public void startSkatSeries(int rounds, boolean unlimitedRounds, boolean onlyPlayRamsch) {

		if (!isSeriesRunning()) {
			// TODO save old series data?
			series = new SkatSeries(tableName);
			series.setView(view);

			if (view instanceof JSkatViewImpl) {
				// set max sleep time only when using GUI
				series.setMaxSleep(100);
			}
		}

		if (player.size() >= 3) {

			view.startSeries(tableName);
			series.setPlayer(player);
			series.setOnlyPlayRamsch(onlyPlayRamsch);
			series.setMaxRounds(rounds, unlimitedRounds);
			series.start();
		}
	}

	/**
	 * Pauses a skat series
	 */
	public void pauseSkatSeries() {

		synchronized (series) {

			series.startWaiting();
		}
	}

	/**
	 * Resumes a paused skat series
	 */
	public void resumeSkatSeries() {

		synchronized (series) {

			series.stopWaiting();
			series.notify();
		}
	}

	/**
	 * Pauses the current skat game
	 */
	public void pauseSkatGame() {

		series.pauseSkatGame();
	}

	/**
	 * Resumes the current paused skat game
	 */
	public void resumeSkatGame() {

		series.resumeSkatGame();
	}

	/**
	 * Checks whether the current skat game is waiting or not
	 * 
	 * @return TRUE if the skat game is waiting
	 */
	public boolean isSkatGameWaiting() {

		return series.isSkatGameWaiting();
	}

	/**
	 * Checks whether the skat series is waiting or not
	 * 
	 * @return TRUE if the skat series is waiting
	 */
	public boolean isSkatSeriesWaiting() {

		return series.isWaiting();
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
	public boolean placePlayer(JSkatPlayer newPlayer) {

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
	 * Gets the ID of the current game
	 * 
	 * @return Game ID of the current game
	 */
	public int getCurrentGameID() {

		return series.getCurrentGameID();
	}

	/**
	 * Sets the view for the skat table
	 * 
	 * @param newView
	 *            View
	 */
	public void setView(JSkatView newView) {

		view = newView;
	}

	/**
	 * Gets table name
	 * 
	 * @return Table name
	 */
	public String getName() {

		return tableName;
	}

	/**
	 * Sets table name
	 * 
	 * @param newTableName
	 *            Table name
	 */
	public void setName(String newTableName) {

		tableName = newTableName;
	}
}
