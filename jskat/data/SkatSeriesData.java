/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import java.util.Observable;
import java.util.Vector;

import org.apache.log4j.Logger;

import jskat.control.SkatGame;
import jskat.player.JSkatPlayer;

/**
 * Data class for a Skat series
 */
public class SkatSeriesData extends Observable {

	static Logger log = Logger.getLogger(jskat.data.SkatSeriesData.class);

	/**
	 * Constructor
	 * 
	 * @param players Array of Skat players
	 * @param roundsToPlay Number of rounds to be played in the series
	 */
	public SkatSeriesData(JSkatPlayer[] players, long roundsToPlay) {

		this.players = players;
		playerCount = this.players.length;

		if (roundsToPlay > 0) {

			maxRounds = roundsToPlay;

		} else {

			maxRounds = -1;
			unlimitedNumberOfRounds = true;
		}

		currRound = 0;

		games = new Vector<SkatGame>();

		log.debug("Skat series created");
	}

	/**
	 * Gets the state of the series
	 * 
	 * @return The state of the series
	 */
	public int getState() {

		return seriesState;
	}

	/**
	 * Adds a new Skat game
	 * 
	 * @param newGame
	 *            The new game
	 */
	public void addNewGame(SkatGame newGame) {

		games.add(newGame);
		currGameID = games.size() - 1;
		currGameData = newGame.getSkatGameData();

		setChanged();
		notifyObservers(newGame);
	}

	/**
	 * Gets the data of the current skat game
	 * 
	 * @return Data of the current skat game
	 */
	public SkatGameData getCurrGameData() {

		return currGameData;
	}

	/**
	 * Gets the Skat game currently played
	 * 
	 * @return Skat game
	 */
	public SkatGame getCurrGame() {

		return (SkatGame) games.get(currGameID);
	}

	/**
	 * Gets the players as ArrayList
	 * 
	 * @return The players
	 */
	public JSkatPlayer[] getPlayers() {

		return players;
	}

	/**
	 * Set the players
	 * 
	 * @param newPlayers
	 */
	public void setPlayers(JSkatPlayer[] newPlayers) {

		players = newPlayers;
	}

	/**
	 * Returns the number of ramsch games still to be played
	 * 
	 * @return The number of ramsch games still to be played
	 */
	public int getRamschGamesToPlay() {

		return ramschGamesToPlay;
	}

	/**
	 * Notifies the Skat series that a ramsch game was finished
	 *  
	 */
	public void ramschGameFinished() {

		if (ramschGamesToPlay > 0) {

			ramschGamesToPlay--;
		}
	}

	/**
	 * Adds a new round of ramsch games
	 */
	public void addRoundOfRamschGames() {

		// add a round of ramsch games
		// it depends on the number of players how many games
		// will be added
		ramschGamesToPlay += players.length;
	}

	/**
	 * Adds a new ramsch game
	 * 
	 */
	public void addRamschGame() {

		ramschGamesToPlay++;
	}

	/**
	 * Adds an additional bock round to be played
	 * 
	 * @param multipleRoundsAllowed
	 *            the option whether multiple bock rounds are allowed, i.e. can
	 *            another bock round be triggered within an already ongoing bock
	 *            round
	 */
	/*
	 * public void addBockRound(boolean multipleRoundsAllowed) {
	 * 
	 * if (bockGamesToPlay > -1 && multipleRoundsAllowed) {
	 * 
	 * bockRoundsToPlay++; } else if (bockGamesToPlay < 0) {
	 * 
	 * bockRoundsToPlay++; } }
	 */

	/**
	 * Checks the current state of remaining bock and ramsch games and rounds
	 */
	/*
	 * public void checkBock() { // TODO this must be reworked!!!
	 * log.debug("Game state: " + bockGamesToPlay + " Bock games remaining, " +
	 * ramschGamesToPlay + " Ramsch games remaining, " + bockRoundsToPlay + "
	 * Bock rounds remaining");
	 * 
	 * if(bockGamesToPlay > -1) { bockGamesToPlay--; ramschGamesToPlay =
	 * players.length; } else if(ramschGamesToPlay > -1) { if(bockRoundsToPlay >
	 * 0) { bockRoundsToPlay--; bockGamesToPlay = players.length; } } else
	 * if(bockRoundsToPlay > 0) { bockRoundsToPlay--; bockGamesToPlay =
	 * players.length; } }
	 */
	
	/**
	 * Checks, whether there are any ramsch games left to be played
	 * 
	 * @return TRUE, if there are ramsch games left to be played
	 */
	public boolean isRamschGamesRemaining() {

		return (ramschGamesToPlay > 0);
	}

	/**
	 * Gets the number of bock games still to be played
	 * 
	 * @return Number of bock games still to be played
	 */
	public int getBockGamesToPlay() {

		return bockGamesToPlay;
	}

	/**
	 * Gets the number of bock rounds still to be played
	 * 
	 * @return Number of bock rounds still to be played
	 */
	public int getBockRoundsToPlay() {

		return bockRoundsToPlay;
	}

	/**
	 * @param bockGamesToPlay
	 *            The bockGamesToPlay to set.
	 */
	public void setBockGamesToPlay(int bockGamesToPlay) {

		this.bockGamesToPlay = bockGamesToPlay;
	}

	/**
	 * @param bockRoundsToPlay
	 *            The bockRoundsToPlay to set.
	 */
	public void setBockRoundsToPlay(int bockRoundsToPlay) {

		this.bockRoundsToPlay = bockRoundsToPlay;
	}

	/**
	 * Returns the number of games already played
	 * 
	 * @return number of games that have been played
	 */
	public int countGames() {

		return games.size();
	}

	/**
	 * Checks whether there are still rounds to be played
	 * 
	 * @return TRUE when there are still round to be played
	 */
	public boolean isOpenRounds() {

		boolean result = true;

		if (currRound == maxRounds) {

			result = false;
		}

		return result;
	}

	/**
	 * Sets the maximal number of rounds to be played
	 * 
	 * @param maxRounds
	 *            Maximal number of rounds to be played
	 */
	public void setMaxRounds(long maxRounds) {

		this.maxRounds = maxRounds;
	}

	/**
	 * Gets the maximal number of rounds to be played
	 * 
	 * @return Maximal number of rounds to be played
	 */
	public long getMaxRounds() {

		return maxRounds;
	}

	/**
	 * Sets whether unlimited number of rounds should be played
	 * 
	 * @param unlimitedNumberOfRounds
	 *            TRUE if unlimited number of rounds should be played
	 */
	public void setUnlimitedNumberOfRounds(boolean unlimitedNumberOfRounds) {

		this.unlimitedNumberOfRounds = unlimitedNumberOfRounds;
	}

	/**
	 * Gets whether unlimited number of rounds should be played
	 * 
	 * @return TRUE when unlimited number of rounds should be played
	 */
	private boolean isUnlimitedNumberOfRounds() {

		return unlimitedNumberOfRounds;
	}

	/**
	 * Gets whether there are games left to be played or not
	 * 
	 * @return TRUE when there are games left to be played
	 */
	public boolean isGameLeft() {

		// TODO unit testing for this method
		boolean result = false;

		if (isUnlimitedNumberOfRounds() ||
				games.size() < maxRounds * playerCount) {

			result = true;
		}

		return result;
	}

	/**
	 * Max number of rounds to be played
	 */
	private long maxRounds;

	/**
	 * Number of current round
	 */
	private long currRound;

	/**
	 * Number of players in the skat series
	 */
	private int playerCount;

	/**
	 * All skat player
	 */
	private JSkatPlayer[] players;

	/**
	 * All games in the skat series
	 */
	private Vector<SkatGame> games;

	/**
	 * Holds the data of the current game
	 */
	private SkatGameData currGameData;

	/**
	 * The ID of the current game
	 */
	private int currGameID;

	/**
	 * The status of the series
	 */
	private int seriesState;

	/**
	 * Holds all skat series states
	 */
	public enum SeriesStates {
		/**
		 * New series started
		 */
		NEW_SERIES, 
		/**
		 * Players are ready to play
		 */
		PLAYERS_READY,
		/**
		 * Games are in progress
		 */
		GAMES_IN_PROGRESS, 
		/**
		 * Series has finished
		 */
		SERIES_FINISHED};

	/**
	 * Number of ramsch games still to be played
	 */
	private int ramschGamesToPlay = 0;

	/**
	 * Number of bock rounds still to be played
	 */
	private int bockRoundsToPlay = 0;

	/**
	 * Number of bock games still to be played
	 */
	private int bockGamesToPlay = 0;

	/**
	 * Flag for playing unlimited number of rounds
	 */
	private boolean unlimitedNumberOfRounds = false;
}
