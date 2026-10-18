package org.jskat.control.command.table;

/**
 * This command is created when a skat game should be replayed.
 */
public class ReplayGameCommand extends AbstractTableCommand {
    /**
     * Performs replay game command.
     * @param tableName table name value.
     */
    public ReplayGameCommand(String tableName) {
        super(tableName);
    }
}
