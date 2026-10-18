package org.jskat.control.event.table;

import org.jskat.control.event.skatgame.SkatGameEvent;

/**
 * This event is created when a move in a skat game was done on a table.
 */
public class TableGameMoveEvent extends AbstractTableEvent {

    /**
     * Game move associated with this table event.
     */
    public final SkatGameEvent gameEvent;

    /**
     * Performs table game move event.
     * @param tableName table name value.
     * @param gameEvent game event value.
     */
    public TableGameMoveEvent(String tableName, SkatGameEvent gameEvent) {
        super(tableName);
        this.gameEvent = gameEvent;
    }
}
