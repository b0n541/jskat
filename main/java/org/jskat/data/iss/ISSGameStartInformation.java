/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0
 * Build date: 2011-05-23 18:13:47
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

import org.jskat.util.Player;


/**
 * Holds all data for a ISS skat game
 */
public class ISSGameStartInformation {

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
