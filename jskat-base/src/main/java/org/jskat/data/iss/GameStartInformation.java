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

import org.jskat.util.Player;


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
