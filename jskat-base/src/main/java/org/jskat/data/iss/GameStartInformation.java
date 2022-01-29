package org.jskat.data.iss;

import org.jskat.util.Player;

import java.util.HashMap;
import java.util.Map;


/**
 * Holds all data for a ISS skat game
 */
public class GameStartInformation {

    private String loginName;
    private int gameNo = 0;
    private Map<Player, String> playerNames = new HashMap<Player, String>();
    private Map<Player, Double> playerTimes = new HashMap<Player, Double>();

    public void setGameNo(int newNumber) {

        this.gameNo = newNumber;
    }

    public int getGameNo() {

        return this.gameNo;
    }

    public void clearPlayerNames() {

        this.playerNames.clear();
    }

    public void putPlayerName(Player position, String name) {

        this.playerNames.put(position, name);
    }

    public String getPlayerName(Player position) {

        return this.playerNames.get(position);
    }

    public void clearPlayerTimes() {

        this.playerTimes.clear();
    }

    public void putPlayerTime(Player position, Double time) {

        this.playerTimes.put(position, time);
    }

    public double getPlayerTime(Player position) {

        return this.playerTimes.get(position).doubleValue();
    }

    public void setLoginName(String newLoginName) {

        loginName = newLoginName;
    }

    public String getLoginName() {

        return loginName;
    }
}
