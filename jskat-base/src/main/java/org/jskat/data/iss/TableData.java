package org.jskat.data.iss;

/**
 * Holds all information about a skat table on ISS
 */
public class TableData {

    private String tableName;
    private int maxPlayers;
    private long gamesPlayed;
    private String firstPlayer;
    private String secondPlayer;
    private String thirdPlayer;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public String getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public String getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(String secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public String getThirdPlayer() {
        return thirdPlayer;
    }

    public void setThirdPlayer(String thirdPlayer) {
        this.thirdPlayer = thirdPlayer;
    }
}
