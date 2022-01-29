package org.jskat.control.event.table;

import org.jskat.data.JSkatViewType;

/**
 * This event is created when a skat table is created.
 */
public class TableCreatedEvent extends AbstractTableEvent {

    public final JSkatViewType tableType;

    /**
     * Constructor.
     *
     * @param tableType Type to distinguish between local and ISS tables
     * @param tableName Table name
     */
    public TableCreatedEvent(JSkatViewType tableType, String tableName) {

        super(tableName);
        this.tableType = tableType;
    }

    @Override
    public String toString() {
        return "TableCreatedEvent: type: " + tableType + " name: " + tableName;
    }
}
