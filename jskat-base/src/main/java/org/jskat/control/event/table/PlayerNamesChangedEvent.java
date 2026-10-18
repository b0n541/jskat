package org.jskat.control.event.table;

/**
 * Defines the player names changed event type.
 */
public final class PlayerNamesChangedEvent extends AbstractTableEvent {

    /**
     * Name of the upper-left player.
     */
    public final String upperLeftPlayerName;
    /**
     * Whether the upper-left player is computer controlled.
     */
    public final boolean isUpperLeftPlayerAIPlayer;
    /**
     * Name of the upper-right player.
     */
    public final String upperRightPlayerName;
    /**
     * Whether the upper-right player is computer controlled.
     */
    public final boolean isUpperRightPlayerAIPlayer;
    /**
     * Name of the lower player.
     */
    public final String lowerPlayerName;
    /**
     * Whether the lower player is computer controlled.
     */
    public final boolean isLowerPlayerAIPlayer;

    /**
     * Performs player names changed event.
     * @param tableName table name value.
     * @param upperLeftPlayerName upper left player name value.
     * @param isUpperLeftPlayerAIPlayer is upper left player a i player value.
     * @param upperRightPlayerName upper right player name value.
     * @param isUpperRightPlayerAIPlayer is upper right player a i player value.
     * @param lowerPlayerName lower player name value.
     * @param isLowerPlayerAIPlayer is lower player a i player value.
     */
    public PlayerNamesChangedEvent(String tableName,
                                   String upperLeftPlayerName, boolean isUpperLeftPlayerAIPlayer,
                                   String upperRightPlayerName, boolean isUpperRightPlayerAIPlayer,
                                   String lowerPlayerName, boolean isLowerPlayerAIPlayer) {
        super(tableName);
        this.upperLeftPlayerName = upperLeftPlayerName;
        this.isUpperLeftPlayerAIPlayer = isUpperLeftPlayerAIPlayer;
        this.upperRightPlayerName = upperRightPlayerName;
        this.isUpperRightPlayerAIPlayer = isUpperRightPlayerAIPlayer;
        this.lowerPlayerName = lowerPlayerName;
        this.isLowerPlayerAIPlayer = isLowerPlayerAIPlayer;
    }

    @Override
    public String toString() {
        return "PlayerNamesChangedEvent: " + upperLeftPlayerName + ", " + upperRightPlayerName + ", " + lowerPlayerName;
    }
}
