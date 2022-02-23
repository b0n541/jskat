package org.jskat.control.command.table;

import org.jskat.data.JSkatViewType;


public class RemoveTableCommand {

    public final String tableName;
    public final JSkatViewType tableType;

    public RemoveTableCommand(JSkatViewType tableType, String tableName) {
        this.tableType = tableType;
        this.tableName = tableName;
    }
}
