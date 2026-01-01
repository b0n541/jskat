package org.jskat.control.command.table;

/**
 * This command is created when a skat game should be replayed with the same card deal.
 * The replayed game does not count towards the series score.
 */
public class ReplayWithSameCardsCommand extends AbstractTableCommand {

    public ReplayWithSameCardsCommand(String tableName) {
        super(tableName);
    }
}
