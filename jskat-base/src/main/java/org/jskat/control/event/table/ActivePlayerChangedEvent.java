package org.jskat.control.event.table;

import org.jskat.util.Player;

public class ActivePlayerChangedEvent extends AbstractTableEvent {

    public final Player player;

    public ActivePlayerChangedEvent(String tableName, Player player) {
        super(tableName);
        this.player = player;
    }

    @Override
    public String toString() {
        return "ActivePlayerChangedEvent: " + player;
    }
}
