package org.jskat.control.command.table;

import org.jskat.util.Card;

/**
 * Defines the put card into skat command type.
 */
public class PutCardIntoSkatCommand extends AbstractTableCommand {
    /**
     * Card to move into the skat.
     */
    public final Card card;

    /**
     * Performs put card into skat command.
     * @param tableName table name value.
     * @param card card value.
     */
    public PutCardIntoSkatCommand(String tableName, Card card) {
        super(tableName);
        this.card = card;
    }
}
