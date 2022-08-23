package org.jskat.control.event.table;

import org.jskat.data.SkatGameData;

public final class SkatGameStateChangedEvent extends AbstractTableEvent {
    public final SkatGameData.GameState gameState;

    public SkatGameStateChangedEvent(String tableName, SkatGameData.GameState gameState) {
        super(tableName);
        this.gameState = gameState;
    }

    @Override
    public String toString() {
        return "SkatGameStateChangedEvent: " + gameState;
    }
}
