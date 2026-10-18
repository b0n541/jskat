package org.jskat.control.command.table;

/**
 * Base command targeting a named table.
 */
public abstract class AbstractTableCommand {

    /**
     * Name of the table targeted by this command.
     */
    public final String tableName;

    /**
     * Creates a command for a table.
     * @param tableName target table name
     */
    public AbstractTableCommand(String tableName) {
        this.tableName = tableName;
    }
}
