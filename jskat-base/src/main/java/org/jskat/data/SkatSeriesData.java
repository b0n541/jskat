package org.jskat.data;

import org.jskat.control.SkatGame;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.util.Player;

import java.util.ArrayList;
import java.util.List;

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
     * @param newState New state
     */
    public void setState(SeriesState newState) {

        this.state = newState;
    }

    /**
     * Gets the game state of the current game
     *
     * @return Game state
     */
    public GameState getGameState() {
        return games.get(games.size() - 1).getGameState();
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

    /**
     * Sets the player that is shown at the bottom of the playground panel
     *
     * @param bottomPlayer Player that is shown at the bottom of the playground panel
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
