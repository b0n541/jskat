package org.jskat.control.command.table;

import org.jskat.data.JSkatViewType;


/**
 * Defines the remove table command type.
 */
public class RemoveTableCommand {

    /**
     * Name of the table to remove.
     */
    public final String tableName;
    /**
     * Type of the table to remove.
     */
    public final JSkatViewType tableType;

    /**
     * Performs remove table command.
     * @param tableType table type value.
     * @param tableName table name value.
     */
    public RemoveTableCommand(JSkatViewType tableType, String tableName) {
        this.tableType = tableType;
        this.tableName = tableName;
    }
}
