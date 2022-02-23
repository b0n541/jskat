package org.jskat.control.command.table;

public abstract class AbstractTableCommand {

    public final String tableName;

    public AbstractTableCommand(String tableName) {
        this.tableName = tableName;
    }
}
