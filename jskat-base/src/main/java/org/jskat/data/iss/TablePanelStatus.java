package org.jskat.data.iss;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all data for a ISS table
 */
public class TablePanelStatus {
    /**
     * Creates an instance of this type.
     */
    public TablePanelStatus() {
    }

    private int maxPlayers;

    private Map<String, PlayerStatus> playerInfos = new HashMap<>();

    private String loginName;

    /**
     * Adds a player to the status<br>
     * If there is a player with the same name, already in the map it's status
     * is updated instead
     *
     * @param playerName Player name
     * @param status     Player status
     */
    public void addPlayer(String playerName, PlayerStatus status) {
        playerInfos.put(playerName, status);
    }

    /**
     * Returns or indicates number of players.
     * @return get number of players result.
     */
    public int getNumberOfPlayers() {
        return playerInfos.size();
    }

    /**
     * Returns or indicates player information.
     * @return get player information result.
     */
    public Map<String, PlayerStatus> getPlayerInformation() {
        return playerInfos;
    }

    /**
     * Returns or indicates player information.
     * @param playerName player name value.
     * @return get player information result.
     */
    public PlayerStatus getPlayerInformation(String playerName) {
        return playerInfos.get(playerName);
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
     * Sets login name.
     * @param newLoginName new login name value.
     */
    public void setLoginName(String newLoginName) {
        loginName = newLoginName;
    }

    /**
     * Returns or indicates login name.
     * @return get login name result.
     */
    public String getLoginName() {
        return loginName;
    }
}
