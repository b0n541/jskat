package org.jskat.control.event.table;

import org.jskat.util.CardList;

/**
 * Defines the skat cards picked up event type.
 */
public final class SkatCardsPickedUpEvent extends AbstractTableEvent {

    /**
     * Cards taken from the skat.
     */
    public final CardList cards;

    /**
     * Performs skat cards picked up event.
     * @param tableName table name value.
     * @param cards cards value.
     */
    public SkatCardsPickedUpEvent(final String tableName, final CardList cards) {
        super(tableName);
        this.cards = new CardList(cards);
    }

    @Override
    public String toString() {
        return "SkatCardsPickedUpEvent: " + cards;
    }
}
