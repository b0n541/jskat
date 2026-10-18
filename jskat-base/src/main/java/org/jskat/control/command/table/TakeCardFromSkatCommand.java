package org.jskat.control.command.table;

import org.jskat.util.Card;

/**
 * Defines the take card from skat command type.
 */
public class TakeCardFromSkatCommand extends AbstractTableCommand {
    /**
     * Card to take from the skat.
     */
    public final Card card;

    /**
     * Performs take card from skat command.
     * @param tableName table name value.
     * @param card card value.
     */
    public TakeCardFromSkatCommand(String tableName, Card card) {
        super(tableName);
        this.card = card;
    }
}
