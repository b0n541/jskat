package org.jskat.control.command.table;

/**
 * This command is created when the next game move should be replayed.
 */
public class NextReplayMoveCommand extends AbstractTableCommand {
    /**
     * Performs next replay move command.
     * @param tableName table name value.
     */
    public NextReplayMoveCommand(String tableName) {
        super(tableName);
    }
}
