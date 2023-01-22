package org.jskat.control.event.table;

import org.jskat.util.CardList;

public final class SkatCardsChangedEvent extends AbstractTableEvent {

    public final CardList cards;

    public SkatCardsChangedEvent(String tableName, CardList cards) {
        super(tableName);
        this.cards = new CardList(cards);
    }

    @Override
    public String toString() {
        return "SkatCardsChangedEvent: " + cards;
    }
}
