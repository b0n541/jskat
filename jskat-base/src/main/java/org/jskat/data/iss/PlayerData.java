package org.jskat.data.iss;

/**
 * Holds all information about a player on ISS
 */
public class PlayerData {

    private String login;
    private String languages;
    private boolean isKIPlayer;
    private long gamesPlayed;
    private double strength;

    // TODO next four parameter unknown at the moment

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public void setKIPlayer(boolean isKIPlayer) {
        this.isKIPlayer = isKIPlayer;
    }

    public boolean isKIPlayer() {
        return isKIPlayer;
    }

}
