package org.jskat.data;

import com.google.common.eventbus.Subscribe;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.SkatTable;
import org.jskat.control.event.table.TableCreatedEvent;
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.data.iss.PlayerData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all application data
 */
public class JSkatApplicationData {

    public final static JSkatApplicationData INSTANCE = new JSkatApplicationData();

    private final JSkatOptions options;
    private final Map<String, SkatTable> localSkatTables;
    private final Map<String, SkatTable> joinedIssTables;
    private String tableName;
    private String issUserName;
    private final Map<String, PlayerData> availableIssPlayers;
    private final Map<String, AbstractHumanJSkatPlayer> humanPlayers;

    /**
     * Constructor
     */
    private JSkatApplicationData() {

        options = JSkatOptions.instance();
        localSkatTables = new HashMap<>();
        humanPlayers = new HashMap<>();
        availableIssPlayers = new HashMap<>();
        joinedIssTables = new HashMap<>();

        JSkatEventBus.INSTANCE.register(this);
    }

    @Subscribe
    synchronized public void adjustTableDataOn(final TableCreatedEvent event) {
        final SkatTable table = new SkatTable(event.tableName(), JSkatOptions.instance().getSkatTableOptions());
        if (JSkatViewType.LOCAL_TABLE.equals(event.tableType())) {
            addLocalSkatTable(table);
            registerHumanPlayerObject(table, JSkatMaster.INSTANCE.getView().getHumanPlayerForGUI());
        } else if (JSkatViewType.ISS_TABLE.equals(event.tableType())) {
            addJoinedIssSkatTable(table);
        }

        if (JSkatViewType.LOCAL_TABLE.equals(event.tableType()) || JSkatViewType.ISS_TABLE.equals(event.tableType())) {
            setActiveTable(event.tableType(), event.tableName());
        }
    }

    /**
     * Removes a local skat table
     *
     * @param tableName Table name
     */
    synchronized public void removeLocalSkatTable(final String tableName) {
        localSkatTables.remove(tableName);
        humanPlayers.remove(tableName);
    }

    /**
     * Adds a new local skat table
     *
     * @param newSkatTable New local table
     */
    synchronized public void addLocalSkatTable(final SkatTable newSkatTable) {
        localSkatTables.put(newSkatTable.getName(), newSkatTable);
    }

    /**
     * Registers a human player object with a skat table
     *
     * @param skatTable   Skat table
     * @param humanPlayer Human player
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
     * @param tableName Table name
     * @return Skat table
     */
    public SkatTable getLocalSkatTable(final String tableName) {

        final SkatTable result = localSkatTables.get(tableName);

        if (result == null) {
            throw new IllegalArgumentException(
                    "Unknown table name: " + tableName);
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
     * @param type      View type
     * @param tableName Table name
     */
    public void setActiveTable(final JSkatViewType type, final String tableName) {
        if (JSkatViewType.LOCAL_TABLE.equals(type) || JSkatViewType.ISS_TABLE.equals(type)) {
            this.tableName = tableName;
        }
    }

    /**
     * Gets the active table
     *
     * @return Active table name
     */
    public String getActiveTable() {

        return tableName;
    }

    /**
     * Sets the user name on ISS
     *
     * @param newISSUserName User name
     */
    public void setIssUserName(final String newISSUserName) {
        issUserName = newISSUserName;
    }

    /**
     * Gets the user name on ISS
     *
     * @return User name
     */
    public String getIssUserName() {
        return issUserName;
    }

    /**
     * Gets available player on ISS
     *
     * @return Available player
     */
    public Collection<PlayerData> getAvailableISSPlayers() {
        return availableIssPlayers.values();
    }

    /**
     * Adds an available player on ISS
     *
     * @param newPlayer New player
     */
    public void updateAvailableISSPlayer(
            final String playerName,
            final String language,
            final long gamesPlayed,
            final double strength) {
        final PlayerData player = availableIssPlayers.computeIfAbsent(playerName, ignored -> new PlayerData());
        player.setLogin(playerName);
        player.setLanguages(language);
        player.setGamesPlayed(gamesPlayed);
        player.setStrength(strength);
    }

    /**
     * Adds a joined skat table on ISS
     *
     * @param newSkatTable Skat table
     */
    public void addJoinedIssSkatTable(final SkatTable newSkatTable) {
        joinedIssTables.put(newSkatTable.getName(), newSkatTable);
    }

    /**
     * Removes a player from the available player on ISS
     *
     * @param player Player to be removed
     */
    public void removeAvailableISSPlayer(final String player) {
        availableIssPlayers.remove(player);
    }

    /**
     * Removes a joined skat table on ISS
     *
     * @param tableName Skat table
     */
    public void removeJoinedIssSkatTable(final String tableName) {
        joinedIssTables.remove(tableName);
    }

    /**
     * Checks whether a table is in the set of joined ISS tables
     *
     * @param tableName Table name
     * @return TRUE, if the table was joined on ISS
     */
    public boolean isTableJoined(final String tableName) {
        return joinedIssTables.containsKey(tableName);
    }

    /**
     * Gets the human player for a table
     *
     * @param tableName Table name
     * @return Human player
     */
    public AbstractHumanJSkatPlayer getHumanPlayer(final String tableName) {
        return humanPlayers.get(tableName);
    }

    /**
     * Checks whether a table name is already in use or not
     *
     * @param tableName Table name
     * @return TRUE, if the table name is not used yet
     */
    public boolean isFreeTableName(final String tableName) {

        return !isExistingLocalSkatTable(tableName);
    }

    /**
     * Checks whether the table is a local table or not
     *
     * @param tableName Table name
     * @return TRUE, if the table is a local table
     */
    public boolean isExistingLocalSkatTable(final String tableName) {
        return localSkatTables.containsKey(tableName);
    }
}
