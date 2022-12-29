package org.jskat.data.iss;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all data for a ISS table
 */
public class TablePanelStatus {

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

    public int getNumberOfPlayers() {
        return playerInfos.size();
    }

    public Map<String, PlayerStatus> getPlayerInformation() {
        return playerInfos;
    }

    public PlayerStatus getPlayerInformation(String playerName) {
        return playerInfos.get(playerName);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setLoginName(String newLoginName) {
        loginName = newLoginName;
    }

    public String getLoginName() {
        return loginName;
    }
}
