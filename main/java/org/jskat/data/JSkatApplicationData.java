/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

	private int localTablesCreated = 0;
	private final JSkatOptions options;
	private final Map<String, SkatTable> skatTables;
	private String activeTable;
	private String issLoginName;
	private final Set<String> availableIssPlayer;
	private final Set<String> joinedIssTables;
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
		skatTables = new HashMap<String, SkatTable>();
		humanPlayers = new HashMap<String, AbstractHumanJSkatPlayer>();
		availableIssPlayer = new HashSet<String>();
		joinedIssTables = new HashSet<String>();
	}

	/**
	 * Removes a local skat table
	 */
	synchronized public void removeSkatTable(final String tableName) {
		skatTables.remove(tableName);
		humanPlayers.remove(tableName);
	}

	/**
	 * Adds a new local skat table
	 * 
	 * @param newSkatTable
	 *            New local table
	 */
	synchronized public void addSkatTable(final SkatTable newSkatTable) {
		skatTables.put(newSkatTable.getName(), newSkatTable);
		localTablesCreated++;
	}

	/**
	 * Registers a human player object with a skat table
	 * 
	 * @param skatTable
	 * @param humanPlayer
	 */
	synchronized public void registerHumanPlayerObject(final SkatTable skatTable,
			final AbstractHumanJSkatPlayer humanPlayer) {
		humanPlayers.put(skatTable.getName(), humanPlayer);
	}

	/**
	 * Gets the number of local tables created so far
	 * 
	 * @return Number of local tables created so far
	 */
	public int getLocalTablesCreated() {

		return localTablesCreated;
	}

	/**
	 * Returns a reference to a skat table
	 * 
	 * @param tableName
	 *            Table name
	 * @return Skat table
	 */
	public SkatTable getSkatTable(final String tableName) {

		SkatTable result = skatTables.get(tableName);

		if (result == null) {
			throw new IllegalArgumentException("Unknown table name: " + tableName); //$NON-NLS-1$
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
	 * Sets the active table
	 * 
	 * @param newActiveTable
	 *            New active table
	 */
	public void setActiveTable(final String newActiveTable) {

		if (!skatTables.containsKey(newActiveTable)) {
			// table is not known yet --> comes from ISS
			joinedIssTables.add(newActiveTable);
		}

		activeTable = newActiveTable;
	}

	/**
	 * Gets the active table
	 * 
	 * @return Active table
	 */
	public String getActiveTable() {

		return activeTable;
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

		return !skatTables.keySet().contains(tableName);
	}
}
