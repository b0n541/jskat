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
	
	/**
	 * Adds a player to the status<br>
	 * If there is a player with the same name, already in the map
	 * it's status is updated instead
	 * 
	 * @param playerName Player name
	 * @param status Player status
	 */
	public void addPlayer(String playerName, ISSPlayerStatus status) {
		
		this.playerInfos.put(playerName, status);
	}

	/**
	 * Updates the player information
	 * 
	 * @param playerName Player name
	 * @param status Player status
	 */
	public void updatePlayer(String playerName, ISSPlayerStatus status) {
		
		this.playerInfos.put(playerName, status);
	}
	
	public Map<String, ISSPlayerStatus> getPlayerInformation() {
		
		return this.playerInfos;
	}

	public int getMaxPlayers() {
		
		return this.maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		
		this.maxPlayers = maxPlayers;
	}
}
