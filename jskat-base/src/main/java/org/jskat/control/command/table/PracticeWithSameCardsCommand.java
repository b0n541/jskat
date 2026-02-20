package org.jskat.control.command.table;

/**
 * This command is created when a skat game should be practiced with the same card deal.
 * The practice game does not count towards the series score.
 */
public class PracticeWithSameCardsCommand extends AbstractTableCommand {

    public PracticeWithSameCardsCommand(String tableName) {
        super(tableName);
    }
}
