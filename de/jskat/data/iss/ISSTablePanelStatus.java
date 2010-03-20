/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data.iss;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all data for a ISS table
 */
public class ISSTablePanelStatus {

	private int maxPlayers;

	List<ISSPlayerStatus> playerInfos = new ArrayList<ISSPlayerStatus>();

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
	public void addPlayer(ISSPlayerStatus status) {

		this.playerInfos.add(status);
	}

	public List<ISSPlayerStatus> getPlayerInformation() {

		return this.playerInfos;
	}

	public int getMaxPlayers() {

		return this.maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {

		this.maxPlayers = maxPlayers;
	}
}
