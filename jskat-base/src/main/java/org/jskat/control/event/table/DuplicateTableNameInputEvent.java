package org.jskat.control.event.table;

/**
 * This event is created when a user puts in a table name that already exists.
 */
public class DuplicateTableNameInputEvent extends AbstractTableEvent {

    public DuplicateTableNameInputEvent(String tableName) {
        super(tableName);
    }
}
