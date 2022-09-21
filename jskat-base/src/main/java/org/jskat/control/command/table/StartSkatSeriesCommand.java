package org.jskat.control.command.table;

/**
 * This command is created when a new skat series should be created.
 */
public class StartSkatSeriesCommand extends AbstractTableCommand {
    public StartSkatSeriesCommand(String tableName) {
        super(tableName);
    }
}
