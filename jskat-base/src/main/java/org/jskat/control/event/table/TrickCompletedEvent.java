package org.jskat.control.event.table;

import org.jskat.data.Trick;

/**
 * This event is created when a trick is completed.
 */
public class TrickCompletedEvent {

    /**
     * Trick that has just completed.
     */
    public final Trick trick;

    /**
     * Performs trick completed event.
     * @param trick trick value.
     */
    public TrickCompletedEvent(Trick trick) {
        this.trick = trick;
    }
}
