package org.jskat.control.command.table;

/**
 * Defines the save series as command type.
 */
public class SaveSeriesAsCommand extends AbstractTableCommand {
    /**
     * Performs save series as command.
     * @param tableName table name value.
     */
    public SaveSeriesAsCommand(String tableName) {
        super(tableName);
    }
}
