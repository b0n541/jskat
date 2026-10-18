package org.jskat.control.command.table;

/**
 * Defines the set active table command type.
 */
public class SetActiveTableCommand extends AbstractTableCommand {
    /**
     * Performs set active table command.
     * @param tableName table name value.
     */
    public SetActiveTableCommand(String tableName) {
        super(tableName);
    }
}
