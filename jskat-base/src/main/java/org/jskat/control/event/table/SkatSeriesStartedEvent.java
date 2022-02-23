package org.jskat.control.event.table;

public class SkatSeriesStartedEvent extends AbstractTableEvent {

    public SkatSeriesStartedEvent(final String tableName) {
        super(tableName);
    }

    @Override
    public String toString() {
        return "SkatSeriesStartedEvent: tableName: " + tableName;
    }
}
