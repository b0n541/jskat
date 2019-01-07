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
