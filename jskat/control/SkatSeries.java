/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.control;

import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import jskat.data.JSkatDataModel;
import jskat.data.SkatGameData;
import jskat.data.SkatSeriesData;
import jskat.gui.main.JSkatFrame;
import jskat.gui.main.LastTricksDialog;
import jskat.player.JSkatPlayer;

/**
 * Controls a skat series
 */
public class SkatSeries extends Observable implements Observer {

	private static final Logger log = Logger
			.getLogger(jskat.control.SkatSeries.class);

	/**
	 * Constructor
	 * 
	 * @param dataModel
	 * 			The data model of JSkat
	 * @param players
	 *          All players
	 * @param roundsToPlay
	 *          number of rounds to play in the series
	 */
	public SkatSeries(JSkatDataModel dataModel,
			JSkatPlayer[] players,
			long roundsToPlay) {

		this.dataModel = dataModel;
		jskatStrings = dataModel.getResourceBundle();
		this.mainWindow = dataModel.getMainWindow();
		
		skatSeriesData = new SkatSeriesData(players, roundsToPlay);
		
		setState(SeriesStates.NEW_SERIES);
	}

	/**
	 * Gets the data for the skat series
	 * 
	 * @return The data for the skat series
	 */
	public SkatSeriesData getSkatSeriesData() {

		return skatSeriesData;
	}

	/**
	 * Gets the current skat game
	 * 
	 * @return The current skat game
	 */
	public SkatGame getCurrSkatGame() {

		return skatSeriesData.getCurrGame();
	}

	/**
	 * Gets the data for the current skat game
	 * 
	 * @return The data for the current skat game
	 */
	public SkatGameData getCurrSkatGameData() {

		return skatSeriesData.getCurrGameData();
	}

	/**
	 * Starts the skat series
	 * 
	 * @return TRUE if the start was successful
	 */
	public boolean startPlaying() {
		
		boolean result = false;
		
		if (state == SeriesStates.NEW_SERIES) {
			// first dealer is player
			// first fore hand is upper left player
			result = startNewGame(2);
		}
		
		return result;
	}
	
	/**
	 * Starts a new game
	 */
	private boolean startNewGame(int dealer) {

		boolean result = false;
		
		// Check how many games have been played and if there are games
		// to be played
		int gamesPlayed = skatSeriesData.countGames();

		log.debug("Games played so far: " + gamesPlayed);
		log.debug("a=" + (gamesPlayed % 3) + ", b=" + (gamesPlayed / 3)
				+ " of " + skatSeriesData.getMaxRounds());

		if (skatSeriesData.isGameLeft()) {

			// Add a new game to the data model
			SkatGame newGame = new SkatGame(dataModel, this, dealer);
			newGame.addObserver(this);
			skatSeriesData.addNewGame(newGame);

			setState(SeriesStates.NEW_GAME);
			
			// 21.05.07 mjl: notify observers of new game
			setChanged();
			notifyObservers(newGame);

			// 28.05.07 mjl: set up LastTricksDialog for this game
			if (mainWindow != null) {
				mainWindow.initForNewGame(this);
				newGame.addObserver(mainWindow);
			}

			// TODO do this in SkatGame
			skatSeriesData.getCurrGame().dealCards();
			
			// TODO rework the method in SkatSeries first
			// dataModel.getCurrentRound().checkBock();
			// Are there any ramsch games to play
			// TODO do this in SkatGame
			/*
			 * if (skatSeriesData.isRamschGamesRemaining()) {
			 *  // play a ramsch game
			 * setState(JSkatStates.DOING_SCHIEBERRAMSCH); prepareRamschGame();
			 *  } else {
			 *  // start bidding setState(JSkatStates.BIDDING);
			 * bidThread.start(); }
			 */
			
			result = true;
			
		} else {
			
			setState(SeriesStates.SERIES_FINISHED);

			JOptionPane.showMessageDialog(mainWindow, jskatStrings.getString("skatseries_finished"),
					jskatStrings.getString("skatseries_finished_title"), JOptionPane.INFORMATION_MESSAGE);

		}
		
		return result;
	}

	/**
	 * Sets the state for the JSkatSeries
	 * 
	 * @param newState
	 */
	private void setState(SeriesStates newState) {

		state = newState;
		
		setChanged();
		notifyObservers(newState);
	}

	private JSkatFrame mainWindow;
	
	private JSkatDataModel dataModel;
	private ResourceBundle jskatStrings;
	
	/**
	 * All skat series states
	 *
	 */
	public enum SeriesStates {
		/**
		 * Series started
		 */
		NEW_SERIES, 
		/**
		 * New game started
		 */
		NEW_GAME, 
		/**
		 * Series finished
		 */
		SERIES_FINISHED
	}
	
	private SeriesStates state;

	private SkatSeriesData skatSeriesData;

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable observ, Object obj) {
		
		// log.debug("UPDATE " + observ + ": " + obj + " has changed...");

		if (observ instanceof SkatGame && obj instanceof SkatGame.GameState) {
			
			if (((SkatGame.GameState) obj) == SkatGame.GameState.WAIT_FOR_NEXT_GAME) {
				
				int dealer = (((SkatGame)observ).getDealer() + 1) % 3;
				// Game finished --> start next game
				startNewGame(dealer);
			}
		}
		
	}
}