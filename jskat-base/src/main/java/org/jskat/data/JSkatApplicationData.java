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
package org.jskat.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jskat.control.SkatTable;
import org.jskat.gui.human.AbstractHumanJSkatPlayer;

/**
 * Holds all application data
 */
public class JSkatApplicationData {

	private volatile static JSkatApplicationData instance = null;

	private final JSkatOptions options;
	private final Map<String, SkatTable> localSkatTables;
	private final Set<String> joinedIssTables;
	private String activeView;
	private String issLoginName;
	private final Set<String> availableIssPlayer;
	private final Map<String, AbstractHumanJSkatPlayer> humanPlayers;

	/**
	 * Gets the instance of the application data
	 * 
	 * @return Application data
	 */
	public static JSkatApplicationData instance() {

		if (instance == null) {

			instance = new JSkatApplicationData();
		}

		return instance;
	}

	/**
	 * Constructor
	 */
	private JSkatApplicationData() {

		options = JSkatOptions.instance();
		localSkatTables = new HashMap<String, SkatTable>();
		humanPlayers = new HashMap<String, AbstractHumanJSkatPlayer>();
		availableIssPlayer = new HashSet<String>();
		joinedIssTables = new HashSet<String>();
	}

	/**
	 * Removes a local skat table
	 */
	synchronized public void removeLocalSkatTable(final String tableName) {
		localSkatTables.remove(tableName);
		humanPlayers.remove(tableName);
	}

	/**
	 * Adds a new local skat table
	 * 
	 * @param newSkatTable
	 *            New local table
	 */
	synchronized public void addLocalSkatTable(final SkatTable newSkatTable) {
		localSkatTables.put(newSkatTable.getName(), newSkatTable);
	}

	/**
	 * Registers a human player object with a skat table
	 * 
	 * @param skatTable
	 * @param humanPlayer
	 */
	synchronized public void registerHumanPlayerObject(
			final SkatTable skatTable,
			final AbstractHumanJSkatPlayer humanPlayer) {
		humanPlayers.put(skatTable.getName(), humanPlayer);
	}

	/**
	 * Gets the number of local tables created so far
	 * 
	 * @return Number of local tables created so far
	 */
	public int getLocalTablesCreated() {

		return localSkatTables.size();
	}

	/**
	 * Returns a reference to a skat table
	 * 
	 * @param tableName
	 *            Table name
	 * @return Skat table
	 */
	public SkatTable getLocalSkatTable(final String tableName) {

		SkatTable result = localSkatTables.get(tableName);

		if (result == null) {
			throw new IllegalArgumentException(
					"Unknown table name: " + tableName); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * Gets current options for a new table
	 * 
	 * @return Current options for a new table
	 */
	public SkatTableOptions getTableOptions() {

		return options.getSkatTableOptions();
	}

	/**
	 * Sets the active view
	 * 
	 * @param newActiveView
	 *            New active view
	 */
	public void setActiveView(JSkatViewType type, String newActiveView) {

		if (type == JSkatViewType.ISS_TABLE) {
			joinedIssTables.add(newActiveView);
		}

		activeView = newActiveView;
	}

	/**
	 * Gets the active view
	 * 
	 * @return Active view
	 */
	public String getActiveView() {

		return activeView;
	}

	/**
	 * Sets the login name on ISS
	 * 
	 * @param newISSLoginName
	 *            Login name
	 */
	public void setIssLoginName(final String newISSLoginName) {
		issLoginName = newISSLoginName;
	}

	/**
	 * Gets the login name on ISS
	 * 
	 * @return Login name
	 */
	public String getIssLoginName() {
		return issLoginName;
	}

	/**
	 * Gets available player on ISS
	 * 
	 * @return Available player
	 */
	public Set<String> getAvailableISSPlayer() {
		return availableIssPlayer;
	}

	/**
	 * Adds an available player on ISS
	 * 
	 * @param newPlayer
	 *            New player
	 */
	public void addAvailableISSPlayer(final String newPlayer) {
		availableIssPlayer.add(newPlayer);
	}

	/**
	 * Adds a joined skat table on ISS
	 * 
	 * @param newSkatTable
	 *            Skat table
	 */
	public void addJoinedIssSkatTable(final String newSkatTable) {
		joinedIssTables.add(newSkatTable);
	}

	/**
	 * Removes a player from the available player on ISS
	 * 
	 * @param player
	 *            Player to be removed
	 */
	public void removeAvailableISSPlayer(final String player) {
		availableIssPlayer.remove(player);
	}

	/**
	 * Removes a joined skat table on ISS
	 * 
	 * @param skatTable
	 *            Skat table
	 */
	public void removeJoinedIssSkatTable(final String skatTable) {
		joinedIssTables.remove(skatTable);
	}

	/**
	 * Checks whether a table is in the set of joined ISS tables
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE, if the table was joined on ISS
	 */
	public boolean isTableJoined(final String tableName) {
		return joinedIssTables.contains(tableName);
	}

	/**
	 * Gets the human player for a table
	 * 
	 * @param tableName
	 *            Table name
	 * @return Human player
	 */
	public AbstractHumanJSkatPlayer getHumanPlayer(final String tableName) {
		return humanPlayers.get(tableName);
	}

	/**
	 * Checks whether a table name is already in use or not
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE, if the table name is not used yet
	 */
	public boolean isFreeTableName(final String tableName) {

		return !isExistingLocalSkatTable(tableName);
	}

	/**
	 * Checks whether the table is a local table or not
	 * 
	 * @param tableName
	 *            Table name
	 * @return TRUE, if the table is a local table
	 */
	public boolean isExistingLocalSkatTable(String tableName) {
		return localSkatTables.keySet().contains(tableName);
	}
}
