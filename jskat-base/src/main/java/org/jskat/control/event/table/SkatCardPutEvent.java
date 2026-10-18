package org.jskat.control.event.table;

import org.jskat.util.Card;

/**
 * This event is created when the user has put a card into the Skat.
 */
public class SkatCardPutEvent extends AbstractTableEvent {

    /**
     * Card moved into the skat.
     */
    public final Card card;

    /**
     * Performs skat card put event.
     * @param tableName table name value.
     * @param card card value.
     */
    public SkatCardPutEvent(String tableName, Card card) {
        super(tableName);
        this.card = card;
    }

    @Override
    public String toString() {
        return "SkatCardPutEvent: tableName: " + tableName;
    }
}
