package org.jskat.control.event.table;

import org.jskat.util.CardList;

/**
 * Defines the skat cards changed event type.
 */
public final class SkatCardsChangedEvent extends AbstractTableEvent {

    /**
     * Current contents of the skat.
     */
    public final CardList cards;

    /**
     * Performs skat cards changed event.
     * @param tableName table name value.
     * @param cards cards value.
     */
    public SkatCardsChangedEvent(String tableName, CardList cards) {
        super(tableName);
        this.cards = new CardList(cards);
    }

    @Override
    public String toString() {
        return "SkatCardsChangedEvent: " + cards;
    }
}
