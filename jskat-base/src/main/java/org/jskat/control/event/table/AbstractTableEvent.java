package org.jskat.control.event.table;

public abstract class AbstractTableEvent {

    public final String tableName;

    public AbstractTableEvent(String tableName) {
        this.tableName = tableName;
    }
}
