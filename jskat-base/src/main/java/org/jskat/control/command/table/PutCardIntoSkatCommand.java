package org.jskat.control.command.table;

import org.jskat.util.Card;

public class PutCardIntoSkatCommand extends AbstractTableCommand {
    public final Card card;

    public PutCardIntoSkatCommand(String tableName, Card card) {
        super(tableName);
        this.card = card;
    }
}
