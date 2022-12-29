package org.jskat.control.event.iss;

import org.jskat.control.event.table.AbstractTableEvent;
import org.jskat.data.iss.GameStartInformation;

public class IssTableGameStartedEvent extends AbstractTableEvent {
    public final GameStartInformation gameStart;

    public IssTableGameStartedEvent(String tableName, GameStartInformation gameStart) {
        super(tableName);
        this.gameStart = gameStart;
    }
}
