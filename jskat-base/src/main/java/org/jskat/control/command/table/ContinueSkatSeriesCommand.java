package org.jskat.control.command.table;

/**
 * Defines the continue skat series command type.
 */
public class ContinueSkatSeriesCommand extends AbstractTableCommand {
    /**
     * Performs continue skat series command.
     * @param tableName table name value.
     */
    public ContinueSkatSeriesCommand(String tableName) {
        super(tableName);
    }
}
