package org.jskat.data.iss;

/**
 * Holds all information about a player on ISS
 */
public class PlayerData {
    /**
     * Creates an instance of this type.
     */
    public PlayerData() {
    }

    private String login;
    private String languages;
    private boolean isKIPlayer;
    private long gamesPlayed;
    private double strength;

    // TODO next four parameter unknown at the moment

    /**
     * Returns or indicates login.
     * @return get login result.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets login.
     * @param login login value.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Returns or indicates languages.
     * @return get languages result.
     */
    public String getLanguages() {
        return languages;
    }

    /**
     * Sets languages.
     * @param languages languages value.
     */
    public void setLanguages(String languages) {
        this.languages = languages;
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
     * Returns or indicates strength.
     * @return get strength result.
     */
    public double getStrength() {
        return strength;
    }

    /**
     * Sets strength.
     * @param strength strength value.
     */
    public void setStrength(double strength) {
        this.strength = strength;
    }

    /**
     * Sets a i player.
     * @param isKIPlayer is k i player value.
     */
    public void setAIPlayer(boolean isKIPlayer) {
        this.isKIPlayer = isKIPlayer;
    }

    /**
     * Returns or indicates a i player.
     * @return is a i player result.
     */
    public boolean isAIPlayer() {
        return isKIPlayer;
    }

}
