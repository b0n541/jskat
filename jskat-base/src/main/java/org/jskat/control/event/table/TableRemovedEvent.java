package org.jskat.control.event.table;

import org.jskat.data.JSkatViewType;

/**
 * This event is created when a skat table is removed.
 */
public class TableRemovedEvent extends AbstractTableEvent {

    public final JSkatViewType tableType;

    /**
     * Constructor.
     *
     * @param tableType Type to distinguish between different table types
     * @param tableName Table name
     */
    public TableRemovedEvent(String tableName, JSkatViewType tableType) {

        super(tableName);
        this.tableType = tableType;
    }
}
