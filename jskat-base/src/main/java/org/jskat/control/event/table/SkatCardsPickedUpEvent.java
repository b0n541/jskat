package org.jskat.control.event.table;

import org.jskat.util.CardList;

public final class SkatCardsPickedUpEvent extends AbstractTableEvent {

    public final CardList cards;

    public SkatCardsPickedUpEvent(final String tableName, final CardList cards) {
        super(tableName);
        this.cards = new CardList(cards);
    }

    @Override
    public String toString() {
        return "SkatCardsPickedUpEvent: " + cards;
    }
}
