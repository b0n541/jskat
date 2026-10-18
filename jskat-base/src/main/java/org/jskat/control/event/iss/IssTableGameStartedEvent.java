package org.jskat.control.event.iss;

import org.jskat.control.event.table.AbstractTableEvent;
import org.jskat.data.iss.GameStartInformation;

/**
 * Defines the iss table game started event type.
 */
public class IssTableGameStartedEvent extends AbstractTableEvent {
    /**
     * Initial details for the new game.
     */
    public final GameStartInformation gameStart;

    /**
     * Performs iss table game started event.
     * @param tableName table name value.
     * @param gameStart game start value.
     */
    public IssTableGameStartedEvent(String tableName, GameStartInformation gameStart) {
        super(tableName);
        this.gameStart = gameStart;
    }
}
