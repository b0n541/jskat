package org.jskat.control.event.table;

import org.jskat.util.Player;

/**
 * Event raised when the active player at a table changes.
 */
public class ActivePlayerChangedEvent extends AbstractTableEvent {

    /**
     * Player who is now active.
     */
    public final Player player;

    /**
     * Creates an active-player change event.
     * @param tableName affected table
     * @param player newly active player
     */
    public ActivePlayerChangedEvent(String tableName, Player player) {
        super(tableName);
        this.player = player;
    }

    @Override
    public String toString() {
        return "ActivePlayerChangedEvent: " + player;
    }
}
