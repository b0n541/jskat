package org.jskat.control.command.table;

/**
 * This command is created when the next game move should be replayed.
 */
public class NextReplayMoveCommand extends AbstractTableCommand {
    public NextReplayMoveCommand(String tableName) {
        super(tableName);
    }
}
