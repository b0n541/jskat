package org.jskat.control.iss;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.PutCardIntoSkatCommand;
import org.jskat.control.command.table.TakeCardFromSkatCommand;
import org.jskat.control.event.table.SkatCardPutEvent;
import org.jskat.control.event.table.SkatCardTakenEvent;
import org.jskat.util.Card;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IssSkatCardTransferHandlerTest {

    private static final String TABLE_NAME = "ISS card transfer test";

    @AfterEach
    void removeTableEventBus() {
        JSkatEventBus.TABLE_EVENT_BUSSES.remove(TABLE_NAME);
    }

    @Test
    void takesACardFromTheSkatLocallyForAnIssTable() {
        final EventBus tableEventBus = new EventBus();
        final CardTransferEvents events = new CardTransferEvents();
        tableEventBus.register(new IssSkatCardTransferHandler(TABLE_NAME));
        tableEventBus.register(events);
        JSkatEventBus.TABLE_EVENT_BUSSES.put(TABLE_NAME, tableEventBus);

        tableEventBus.post(new TakeCardFromSkatCommand(TABLE_NAME, Card.CA));

        assertThat(events.takenCard).isEqualTo(Card.CA);
    }

    @Test
    void putsACardIntoTheSkatLocallyForAnIssTable() {
        final EventBus tableEventBus = new EventBus();
        final CardTransferEvents events = new CardTransferEvents();
        tableEventBus.register(new IssSkatCardTransferHandler(TABLE_NAME));
        tableEventBus.register(events);
        JSkatEventBus.TABLE_EVENT_BUSSES.put(TABLE_NAME, tableEventBus);

        tableEventBus.post(new PutCardIntoSkatCommand(TABLE_NAME, Card.CA));

        assertThat(events.putCard).isEqualTo(Card.CA);
    }

    private static final class CardTransferEvents {
        private Card takenCard;
        private Card putCard;

        @Subscribe
        public void on(final SkatCardTakenEvent event) {
            takenCard = event.card;
        }

        @Subscribe
        public void on(final SkatCardPutEvent event) {
            putCard = event.card;
        }
    }
}
