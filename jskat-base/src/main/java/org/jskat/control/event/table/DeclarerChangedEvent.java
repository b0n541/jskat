package org.jskat.control.event.table;

import org.jskat.util.Player;

public final class DeclarerChangedEvent extends AbstractTableEvent {

    public final Player declarer;

    public DeclarerChangedEvent(String tableName, Player declarer) {
        super(tableName);
        this.declarer = declarer;
    }

    @Override
    public String toString() {
        return "DeclarerChangedEvent: " + declarer;
    }
}
