package org.jskat.tournament;

import java.io.IOException;

/** A free OS lock has unreleased owner metadata; resuming requires explicit authorization. */
public final class TournamentRecoveryRequiredException extends IOException {
    public TournamentRecoveryRequiredException(String message) {
        super(message);
    }
}
