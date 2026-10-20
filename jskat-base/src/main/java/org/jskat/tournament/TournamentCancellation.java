package org.jskat.tournament;

import java.util.concurrent.atomic.AtomicBoolean;

/** Cooperative request; admission ownership remains with the runner. */
public final class TournamentCancellation implements CancellationToken {
    private final AtomicBoolean requested = new AtomicBoolean();

    public void requestCancellation() {
        requested.set(true);
    }

    public boolean isCancellationRequested() {
        return requested.get();
    }
}
