package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;

/**
 * Interface for events during a Skat game
 */
public interface SkatGameEvent {
    /**
     * Processes the event forward.
     *
     * @param data Game data
     */
    public void processForward(SkatGameData data);

    /**
     * Processes the event backward.
     *
     * @param data Game data
     */
    public void processBackward(SkatGameData data);
}
