package org.jskat.control.event.table;

/**
 * Defines the skat series started event type.
 */
public class SkatSeriesStartedEvent extends AbstractTableEvent {

    /**
     * Performs skat series started event.
     * @param tableName table name value.
     */
    public SkatSeriesStartedEvent(final String tableName) {
        super(tableName);
    }

    @Override
    public String toString() {
        return "SkatSeriesStartedEvent";
    }
}
