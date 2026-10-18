package org.jskat.data.iss;

/**
 * Holds data about an ISS player
 */
public class PlayerStatus {
    /**
     * Creates an instance of this type.
     */
    public PlayerStatus() {
    }

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

    /**
     * Sets name.
     * @param name name value.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns or indicates name.
     * @return get name result.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns or indicates i p.
     * @return get i p result.
     */
    public String getIP() {
        return IP;
    }

    /**
     * Sets i p.
     * @param ip ip value.
     */
    public void setIP(String ip) {
        IP = ip;
    }

    /**
     * Returns or indicates games played.
     * @return get games played result.
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Sets games played.
     * @param gamesPlayed games played value.
     */
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    /**
     * Returns or indicates games won.
     * @return get games won result.
     */
    public int getGamesWon() {
        return gamesWon;
    }

    /**
     * Sets games won.
     * @param gamesWon games won value.
     */
    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    /**
     * Returns or indicates last game result.
     * @return get last game result result.
     */
    public int getLastGameResult() {
        return lastGameResult;
    }

    /**
     * Sets last game result.
     * @param lastGameResult last game result value.
     */
    public void setLastGameResult(int lastGameResult) {
        this.lastGameResult = lastGameResult;
    }

    /**
     * Returns or indicates total points.
     * @return get total points result.
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * Sets total points.
     * @param totalPoints total points value.
     */
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * Returns or indicates switch34.
     * @return is switch34 result.
     */
    public boolean isSwitch34() {
        return switch34;
    }

    /**
     * Sets switch34.
     * @param switch34 switch34 value.
     */
    public void setSwitch34(boolean switch34) {
        this.switch34 = switch34;
    }

    /**
     * Returns or indicates talk enabled.
     * @return is talk enabled result.
     */
    public boolean isTalkEnabled() {
        return talkEnabled;
    }

    /**
     * Sets talk enabled.
     * @param talkEnabled talk enabled value.
     */
    public void setTalkEnabled(boolean talkEnabled) {
        this.talkEnabled = talkEnabled;
    }

    /**
     * Returns or indicates ready to play.
     * @return is ready to play result.
     */
    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    /**
     * Sets ready to play.
     * @param readyToPlay ready to play value.
     */
    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    /**
     * Sets player left.
     * @param playerLeft player left value.
     */
    public void setPlayerLeft(boolean playerLeft) {
        this.playerLeft = playerLeft;
    }

    /**
     * Returns or indicates player left.
     * @return is player left result.
     */
    public boolean isPlayerLeft() {
        return playerLeft;
    }
}
