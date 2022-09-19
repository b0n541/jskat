package org.jskat.control.event.table;

import org.jskat.util.Card;

/**
 * This event is created when the user has put a card into the Skat.
 */
public class SkatCardPutEvent extends AbstractTableEvent {

    public final Card card;

    public SkatCardPutEvent(String tableName, Card card) {
        super(tableName);
        this.card = card;
    }

    @Override
    public String toString() {
        return "SkatCardPutEvent: tableName: " + tableName;
    }
}
