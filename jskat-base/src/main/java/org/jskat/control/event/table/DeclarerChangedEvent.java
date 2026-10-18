package org.jskat.control.event.table;

import org.jskat.util.Player;

/**
 * Defines the declarer changed event type.
 */
public final class DeclarerChangedEvent extends AbstractTableEvent {

    /**
     * Player who declared the game.
     */
    public final Player declarer;

    /**
     * Performs declarer changed event.
     * @param tableName table name value.
     * @param declarer declarer value.
     */
    public DeclarerChangedEvent(String tableName, Player declarer) {
        super(tableName);
        this.declarer = declarer;
    }

    @Override
    public String toString() {
        return "DeclarerChangedEvent: " + declarer;
    }
}
