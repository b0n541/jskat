package org.jskat.control.event.table;

/**
 * Defines the skat series finished event type.
 */
public class SkatSeriesFinishedEvent extends AbstractTableEvent {

    /**
     * Performs skat series finished event.
     * @param tableName table name value.
     */
    public SkatSeriesFinishedEvent(final String tableName) {
        super(tableName);
    }

    @Override
    public String toString() {
        return "SkatSeriesFinishedEvent";
    }
}
