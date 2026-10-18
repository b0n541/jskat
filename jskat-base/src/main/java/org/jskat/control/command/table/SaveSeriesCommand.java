package org.jskat.control.command.table;

/**
 * Defines the save series command type.
 */
public class SaveSeriesCommand extends AbstractTableCommand {
    /**
     * Performs save series command.
     * @param tableName table name value.
     */
    public SaveSeriesCommand(String tableName) {
        super(tableName);
    }
}
