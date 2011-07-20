/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-20
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
/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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
package org.jskat.data;

import java.util.ArrayList;
import java.util.List;

import org.jskat.control.SkatGame;
import org.jskat.util.Player;


/**
 * Data class for skat series
 */
public class SkatSeriesData {

	/**
	 * Series states
	 */
	public enum SeriesState {

		/**
		 * Series waits for the start of the game
		 */
		WAITING,
		/**
		 * Series is running
		 */
		RUNNING,
		/**
		 * Series is finished
		 */
		SERIES_FINISHED;
	}

	private SeriesState state;
	private List<SkatGame> games;
	private String tableName;
	private Player bottomPlayer;

	/**
	 * Constructor
	 */
	public SkatSeriesData() {

		this.games = new ArrayList<SkatGame>();
		setState(SeriesState.WAITING);
	}

	/**
	 * Gets the state of the series
	 * 
	 * @return State of the series
	 */
	public SeriesState getState() {

		return this.state;
	}

	/**
	 * Sets the state of the series
	 * 
	 * @param newState
	 *            New state
	 */
	public void setState(SeriesState newState) {

		this.state = newState;
	}

	/**
	 * Adds a game to the series
	 * 
	 * @param newGame
	 *            The game to be added
	 */
	public void addGame(SkatGame newGame) {

		this.games.add(newGame);
	}

	/**
	 * Gets the current game ID
	 * 
	 * @return ID of the current game
	 */
	public int getCurrentGameID() {

		return this.games.size() - 1;
	}

	public void setTableName(String newTableName) {

		this.tableName = newTableName;
	}

	public String getTableName() {

		return this.tableName;
	}

	/**
	 * Sets the player that is shown at the bottom of the playground panel
	 * 
	 * @param bottomPlayer
	 *            Player that is shown at the bottom of the playground panel
	 */
	public void setBottomPlayer(Player bottomPlayer) {
		this.bottomPlayer = bottomPlayer;
	}

	/**
	 * Gets the player that is shown at the bottom of the playground panel
	 * 
	 * @return Player that is shown at the bottom of the playground panel
	 */
	public Player getBottomPlayer() {
		return bottomPlayer;
	}
}
