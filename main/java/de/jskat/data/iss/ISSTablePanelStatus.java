/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data.iss;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all data for a ISS table
 */
public class ISSTablePanelStatus {

	private int maxPlayers;

	Map<String, ISSPlayerStatus> playerInfos = new HashMap<String, ISSPlayerStatus>();

	private String loginName;

	/**
	 * Adds a player to the status<br>
	 * If there is a player with the same name, already in the map it's status
	 * is updated instead
	 * 
	 * @param player
	 *            Player position
	 * @param status
	 *            Player status
	 */
	public void addPlayer(String playerName, ISSPlayerStatus status) {

		this.playerInfos.put(playerName, status);
	}

	public int getNumberOfPlayers() {

		return this.playerInfos.size();
	}

	public Map<String, ISSPlayerStatus> getPlayerInformations() {

		return playerInfos;
	}

	public ISSPlayerStatus getPlayerInformation(String playerName) {

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
