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

import jskat.share.SkatRules;
import jskat.control.SkatGame;
import jskat.player.JSkatPlayer;

/**
 * A skat round
 * 
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net
 */
public class SkatSeriesData extends Observable {

	static Logger log = Logger.getLogger(jskat.data.SkatSeriesData.class);

	/** Creates a new instance of a skat round */
	public SkatSeriesData(JSkatPlayer[] players, long roundsToPlay) {

		this.players = players;
		playerCount = this.players.length;

		if (roundsToPlay > 0) {

			maxRounds = roundsToPlay;

		} else {

			maxRounds = -1;
			unlimitedNumberOfGames = true;
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
	 * Adds a new skat game
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
	 * Get the data of the current skat game
	 */
	public SkatGameData getCurrGameData() {

		return currGameData;
	}

	public SkatGame getCurrGame() {

		return (SkatGame) games.get(currGameID);
	}

	/**
	 * Get the player as ArrayList
	 * 
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
	 * Calculates all wins and losses of the players
	 * 
	 * @return A Vector with the wins and losses of the players
	 */
	public Vector calculateWinsAndLosses() {

		Vector<Integer> winsAndLosses = new Vector<Integer>();
		int playerResult;

		for (int i = 0; i < players.length; i++) {

			playerResult = 0;

			for (int j = 0; j < games.size(); j++) {

				SkatGameData currGameData = ((SkatGame) games.get(j))
						.getSkatGameData();

				if (currGameData.getSinglePlayer() == i) {

					playerResult = playerResult
							+ SkatRules.getResult(currGameData);
				}
			}

			winsAndLosses.add(new Integer(playerResult));
		}

		return winsAndLosses;
	}

	/**
	 * Returns the number of ramsch games to be played
	 * 
	 * @return The number of ramsch games to be played
	 */
	public int getRamschGamesToPlay() {

		return ramschGamesToPlay;
	}

	/**
	 * Sets the number of ramsch games to be played
	 * 
	 * @param ramschGameCount
	 *            The number of ramsch games to be played
	 */
	public void setRamschGamesToPlay(int ramschGameCount) {

		ramschGamesToPlay = ramschGameCount;
	}

	/**
	 * 
	 * 
	 */
	public void ramschGameFinished() {

		if (ramschGamesToPlay > 0) {

			ramschGamesToPlay--;
		}
	}

	/**
	 * Adds a new round of ramsch games
	 * 
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
	 * Adds an additional Bock round to be played
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
	 * @return Returns the bockGamesToPlay.
	 */
	public int getBockGamesToPlay() {

		return bockGamesToPlay;
	}

	/**
	 * @return Returns the bockRoundsToPlay.
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
	 * Sets whether unlimited number of games should be played
	 * 
	 * @param unlimitedNumberOfGames
	 *            TRUE if unlimited number of games should be played
	 */
	public void setUnlimitedNumberOfGames(boolean unlimitedNumberOfGames) {

		this.unlimitedNumberOfGames = unlimitedNumberOfGames;
	}

	/**
	 * Gets whether unlimited number of games should be played
	 * 
	 * @return TRUE when ulimited number of games should be played
	 */
	public boolean isUnlimitedNumberOfGames() {

		return unlimitedNumberOfGames;
	}

	/**
	 * Gets whether there are games left to be played or not
	 * 
	 * @return TRUE when there are games left to be played
	 */
	public boolean isGameLeft() {

		// TODO unit testing for this method
		boolean result = false;

		if (games.size() < maxRounds * playerCount) {

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

	private int seriesState;

	public final int NEW_SERIES = 0;

	public final int PLAYERS_READY = 1;

	public final int GAMES_IN_PROGRESS = 2;

	public final int SERIES_FINISHED = 3;

	private int ramschGamesToPlay = 0;

	private int bockRoundsToPlay = 0;

	private int bockGamesToPlay = 0;

	private boolean unlimitedNumberOfGames = false;

	/*
	 * private int roundState;
	 */
}
