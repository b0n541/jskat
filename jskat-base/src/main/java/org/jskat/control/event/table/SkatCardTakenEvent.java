package org.jskat.control.event.table;

import org.jskat.util.Card;

/**
 * This event is created when the user has taken a card from the Skat.
 */
public class SkatCardTakenEvent extends AbstractTableEvent {

    /**
     * Card taken from the skat.
     */
    public final Card card;

    /**
     * Performs skat card taken event.
     * @param tableName table name value.
     * @param card card value.
     */
    public SkatCardTakenEvent(String tableName, Card card) {
        super(tableName);
        this.card = card;
    }

    @Override
    public String toString() {
        return "SkatCardTakenEvent: tableName: " + tableName;
    }
}
