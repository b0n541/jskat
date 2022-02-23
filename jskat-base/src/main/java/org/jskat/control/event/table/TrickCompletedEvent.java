package org.jskat.control.event.table;

import org.jskat.data.Trick;

/**
 * This event is created when a trick is completed.
 */
public class TrickCompletedEvent {

    public final Trick trick;

    public TrickCompletedEvent(Trick trick) {
        this.trick = trick;
    }
}
