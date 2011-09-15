/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all data for a ISS table
 */
public class TablePanelStatus {

	private int maxPlayers;

	Map<String, PlayerStatus> playerInfos = new HashMap<String, PlayerStatus>();

	private String loginName;

	/**
	 * Adds a player to the status<br>
	 * If there is a player with the same name, already in the map it's status
	 * is updated instead
	 * 
	 * @param playerName
	 *            Player name
	 * @param status
	 *            Player status
	 */
	public void addPlayer(String playerName, PlayerStatus status) {

		this.playerInfos.put(playerName, status);
	}

	public int getNumberOfPlayers() {

		return this.playerInfos.size();
	}

	public Map<String, PlayerStatus> getPlayerInformations() {

		return playerInfos;
	}

	public PlayerStatus getPlayerInformation(String playerName) {

		return playerInfos.get(playerName);
	}

	public int getMaxPlayers() {

		return this.maxPlayers;
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
