package org.jskat.control.event.table;

import org.jskat.control.event.skatgame.SkatGameEvent;

/**
 * This event is created when a move in a skat game was done on a table.
 */
public class TableGameMoveEvent extends AbstractTableEvent {

    public final SkatGameEvent gameEvent;

    public TableGameMoveEvent(String tableName, SkatGameEvent gameEvent) {
        super(tableName);
        this.gameEvent = gameEvent;
    }
}
