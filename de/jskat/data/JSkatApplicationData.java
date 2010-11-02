/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import de.jskat.control.SkatTable;
import de.jskat.gui.human.HumanPlayer;

/**
 * Holds all application data
 */
public class JSkatApplicationData {

	private JSkatOptions options;
	private Map<String, SkatTable> skatTables;
	private String activeTable;
	private String issLoginName;
	private Set<String> availableIssPlayer;
	private Set<String> joinedIssTables;
	private Map<String, HumanPlayer> humanPlayers;

	/**
	 * Contructor
	 * 
	 * @param jskatOptions
	 *            JSkat options
	 */
	public JSkatApplicationData(JSkatOptions jskatOptions) {

		this.options = jskatOptions;
		this.skatTables = new HashMap<String, SkatTable>();
		this.humanPlayers = new HashMap<String, HumanPlayer>();
		this.availableIssPlayer = new HashSet<String>();
		this.joinedIssTables = new HashSet<String>();
	}

	/**
	 * Adds a new local skat table
	 * 
	 * @param newSkatTable
	 *            New local table
	 */
	synchronized public void addSkatTable(SkatTable newSkatTable) {

		this.skatTables.put(newSkatTable.getName(), newSkatTable);
		this.humanPlayers.put(newSkatTable.getName(), new HumanPlayer());
	}

	/**
	 * Returns a reference to a skat table
	 * 
	 * @param tableName
	 *            Table name
	 * @return Skat table
	 */
	public SkatTable getSkatTable(String tableName) {

		return this.skatTables.get(tableName);
	}

	/**
	 * Gets all options for JSkat
	 * 
	 * @return All options for JSkat
	 */
	public JSkatOptions getOptions() {

		return this.options;
	}

	/**
	 * Gets current options for a new table
	 * 
	 * @return Current options for a new table
	 */
	public SkatTableOptions getTableOptions() {

		return this.options.getSkatTableOptions();
	}

	/**
	 * Sets the active table
	 * 
	 * @param newActiveTable
	 *            New active table
	 */
	public void setActiveTable(String newActiveTable) {

		if (!this.skatTables.containsKey(newActiveTable)) {
			// table is not known yet --> comes from ISS
			this.joinedIssTables.add(newActiveTable);
		}

		this.activeTable = newActiveTable;
	}

	/**
	 * Gets the active table
	 * 
	 * @return Active table
	 */
	public String getActiveTable() {

		return this.activeTable;
	}

	/**
	 * Gets the resource bundle
	 * 
	 * @return Resource bundle
	 */
	public ResourceBundle getResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets the login name on ISS
	 * 
	 * @param newISSLoginName
	 *            Login name
	 */
	public void setIssLoginName(String newISSLoginName) {
		this.issLoginName = newISSLoginName;
	}

	/**
	 * Gets the login name on ISS
	 * 
	 * @return Login name
	 */
	public String getIssLoginName() {
		return this.issLoginName;
	}

	/**
	 * Gets available player on ISS
	 * 
	 * @return Available player
	 */
	public Set<String> getAvailableISSPlayer() {
		return this.availableIssPlayer;
	}

	/**
	 * Adds an available player on ISS
	 * 
	 * @param newPlayer
	 *            New player
	 */
	public void addAvailableISSPlayer(String newPlayer) {
		this.availableIssPlayer.add(newPlayer);
	}

	/**
	 * Adds a joined skat table on ISS
	 * 
	 * @param newSkatTable
	 *            Skat table
	 */
	public void addJoinedIssSkatTable(String newSkatTable) {
		this.joinedIssTables.add(newSkatTable);
	}

	/**
	 * Removes a player from the available player on ISS
	 * 
	 * @param player
	 *            Player to be removed
	 */
	public void removeAvailableISSPlayer(String player) {
		this.availableIssPlayer.remove(player);
	}

	/**
	 * Removes a joined skat table on ISS
	 * 
	 * @param skatTable
	 *            Skat table
	 */
	public void removeJoinedIssSkatTable(String skatTable) {
		this.joinedIssTables.remove(skatTable);
	}

	/**
	 * Checks whether a table is in the set of joined ISS tables
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE, if the table was joined on ISS
	 */
	public boolean isTableJoined(String tableName) {
		return joinedIssTables.contains(tableName);
	}

	/**
	 * Gets the human player for a table
	 * 
	 * @param tableName
	 *            Table name
	 * @return Human player
	 */
	public HumanPlayer getHumanPlayer(String tableName) {
		return this.humanPlayers.get(tableName);
	}
}
