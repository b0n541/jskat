package org.jskat.control.command.table;

import org.jskat.util.Card;

public class TakeCardFromSkatCommand extends AbstractTableCommand {
    public final Card card;

    public TakeCardFromSkatCommand(String tableName, Card card) {
        super(tableName);
        this.card = card;
    }
}
