package org.jskat.data.iss;

/**
 * Holds data about an ISS player
 */
public class PlayerStatus {

    private String name;
    private String IP;
    private int gamesPlayed;
    private int gamesWon;
    private int lastGameResult;
    private int totalPoints;
    private boolean switch34;
    private boolean talkEnabled;
    private boolean readyToPlay;
    private boolean playerLeft;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String ip) {
        IP = ip;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getLastGameResult() {
        return lastGameResult;
    }

    public void setLastGameResult(int lastGameResult) {
        this.lastGameResult = lastGameResult;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public boolean isSwitch34() {
        return switch34;
    }

    public void setSwitch34(boolean switch34) {
        this.switch34 = switch34;
    }

    public boolean isTalkEnabled() {
        return talkEnabled;
    }

    public void setTalkEnabled(boolean talkEnabled) {
        this.talkEnabled = talkEnabled;
    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public void setPlayerLeft(boolean playerLeft) {
        this.playerLeft = playerLeft;
    }

    public boolean isPlayerLeft() {
        return playerLeft;
    }
}
