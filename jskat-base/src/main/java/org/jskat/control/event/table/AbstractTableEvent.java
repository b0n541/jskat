package org.jskat.control.event.table;

/**
 * Base event associated with a named table.
 */
public abstract class AbstractTableEvent {

    /**
     * Name of the table associated with this event.
     */
    public final String tableName;

    /**
     * Creates an event for a table.
     * @param tableName associated table name
     */
    public AbstractTableEvent(String tableName) {
        this.tableName = tableName;
    }
}
