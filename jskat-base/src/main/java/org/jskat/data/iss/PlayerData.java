/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
