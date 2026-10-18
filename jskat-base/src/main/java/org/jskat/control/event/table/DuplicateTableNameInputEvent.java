package org.jskat.control.event.table;

/**
 * This event is created when a user puts in a table name that already exists.
 */
public class DuplicateTableNameInputEvent extends AbstractTableEvent {

    /**
     * Performs duplicate table name input event.
     * @param tableName table name value.
     */
    public DuplicateTableNameInputEvent(String tableName) {
        super(tableName);
    }
}
