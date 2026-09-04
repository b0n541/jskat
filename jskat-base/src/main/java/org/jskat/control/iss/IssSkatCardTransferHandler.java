package org.jskat.control.iss;

import com.google.common.eventbus.Subscribe;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.PutCardIntoSkatCommand;
import org.jskat.control.command.table.TakeCardFromSkatCommand;
import org.jskat.control.event.table.SkatCardPutEvent;
import org.jskat.control.event.table.SkatCardTakenEvent;

/**
 * Applies the local card-selection part of discarding on an ISS table.
 *
 * <p>ISS receives the final discarded cards with the game announcement, but the player must still be able to
 * move cards between their hand and the visible skat before that announcement.</p>
 */
final class IssSkatCardTransferHandler {

    private final String tableName;

    IssSkatCardTransferHandler(final String tableName) {
        this.tableName = tableName;
    }

    @Subscribe
    public void takeCardFromSkatOn(final TakeCardFromSkatCommand command) {
        JSkatEventBus.INSTANCE.post(new SkatCardTakenEvent(tableName, command.card));
    }

    @Subscribe
    public void putCardIntoSkatOn(final PutCardIntoSkatCommand command) {
        JSkatEventBus.INSTANCE.post(new SkatCardPutEvent(tableName, command.card));
    }
}
