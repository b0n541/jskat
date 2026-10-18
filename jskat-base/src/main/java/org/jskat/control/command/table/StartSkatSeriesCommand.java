package org.jskat.control.command.table;

/**
 * This command is created when a new skat series should be created.
 */
public class StartSkatSeriesCommand extends AbstractTableCommand {
    /**
     * Performs start skat series command.
     * @param tableName table name value.
     */
    public StartSkatSeriesCommand(String tableName) {
        super(tableName);
    }
}
