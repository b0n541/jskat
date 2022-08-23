package org.jskat.control.event.table;

public final class PlayerNamesChangedEvent extends AbstractTableEvent {

    public final String upperLeftPlayerName;
    public final boolean isUpperLeftPlayerAIPlayer;
    public final String upperRightPlayerName;
    public final boolean isUpperRightPlayerAIPlayer;
    public final String lowerPlayerName;
    public final boolean isLowerPlayerAIPlayer;

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
