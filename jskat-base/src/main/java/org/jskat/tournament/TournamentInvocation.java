package org.jskat.tournament;

import java.util.Objects;

public record TournamentInvocation(Action action, boolean recoverStaleLock) {
    public enum Action {
        NEW,
        RESUME,
        VERIFY
    }

    public TournamentInvocation {
        Objects.requireNonNull(action);
    }
}
