/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
