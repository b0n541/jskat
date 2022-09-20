package org.jskat.control.event.table;

public class SkatSeriesFinishedEvent extends AbstractTableEvent {

    public SkatSeriesFinishedEvent(final String tableName) {
        super(tableName);
    }

    @Override
    public String toString() {
        return "SkatSeriesFinishedEvent";
    }
}
