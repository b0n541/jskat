package org.jskat.tournament;

import java.util.concurrent.CancellationException;

/** Cooperative tournament cancellation, distinct from exceptions thrown by player callbacks. */
public final class TournamentCancellationException extends CancellationException {
    public TournamentCancellationException(String message) {
        super(message);
    }
}
