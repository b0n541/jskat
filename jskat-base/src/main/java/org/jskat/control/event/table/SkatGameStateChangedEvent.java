package org.jskat.control.event.table;

import org.jskat.data.SkatGameData;

/**
 * Defines the skat game state changed event type.
 */
public final class SkatGameStateChangedEvent extends AbstractTableEvent {
    /**
     * New state of the game.
     */
    public final SkatGameData.GameState gameState;

    /**
     * Performs skat game state changed event.
     * @param tableName table name value.
     * @param gameState game state value.
     */
    public SkatGameStateChangedEvent(String tableName, SkatGameData.GameState gameState) {
        super(tableName);
        this.gameState = gameState;
    }

    @Override
    public String toString() {
        return "SkatGameStateChangedEvent: " + gameState;
    }
}
