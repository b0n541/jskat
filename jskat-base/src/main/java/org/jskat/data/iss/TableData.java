package org.jskat.data.iss;

/**
 * Holds all information about a skat table on ISS
 */
public class TableData {
    /**
     * Creates an instance of this type.
     */
    public TableData() {
    }

    private String tableName;
    private int maxPlayers;
    private long gamesPlayed;
    private String firstPlayer;
    private String secondPlayer;
    private String thirdPlayer;

    /**
     * Returns or indicates table name.
     * @return get table name result.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets table name.
     * @param tableName table name value.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Returns or indicates max players.
     * @return get max players result.
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Sets max players.
     * @param maxPlayers max players value.
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Returns or indicates games played.
     * @return get games played result.
     */
    public long getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Sets games played.
     * @param gamesPlayed games played value.
     */
    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    /**
     * Returns or indicates first player.
     * @return get first player result.
     */
    public String getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Sets first player.
     * @param firstPlayer first player value.
     */
    public void setFirstPlayer(String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    /**
     * Returns or indicates second player.
     * @return get second player result.
     */
    public String getSecondPlayer() {
        return secondPlayer;
    }

    /**
     * Sets second player.
     * @param secondPlayer second player value.
     */
    public void setSecondPlayer(String secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    /**
     * Returns or indicates third player.
     * @return get third player result.
     */
    public String getThirdPlayer() {
        return thirdPlayer;
    }

    /**
     * Sets third player.
     * @param thirdPlayer third player value.
     */
    public void setThirdPlayer(String thirdPlayer) {
        this.thirdPlayer = thirdPlayer;
    }
}
