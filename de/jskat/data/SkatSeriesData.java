/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data;

import java.util.ArrayList;
import java.util.List;

import de.jskat.control.SkatGame;

/**
 * Data class for skat series
 */
public class SkatSeriesData {

	/**
	 * Series states
	 */
	public enum SeriesStates {
		
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
	
	private SeriesStates state;
	private List<SkatGame> games;
	private String tableName;

	/**
	 * Constructor
	 */
	public SkatSeriesData() {
		
		this.games = new ArrayList<SkatGame>();
		setState(SeriesStates.WAITING);
	}
	
	/**
	 * Gets the state of the series
	 * 
	 * @return State of the series
	 */
	public SeriesStates getState() {
		
		return this.state;
	}

	/**
	 * Sets the state of the series
	 * 
	 * @param newState New state
	 */
	public void setState(SeriesStates newState) {
		
		this.state = newState;
	}

	/**
	 * Adds a game to the series
	 * 
	 * @param newGame The game to be added
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
}
