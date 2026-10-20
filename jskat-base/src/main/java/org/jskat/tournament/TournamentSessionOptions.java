package org.jskat.tournament;

import java.time.Duration;
import java.util.Objects;

public record TournamentSessionOptions(int workers, boolean verbose, Duration stallWarning) {
    public TournamentSessionOptions {
        Objects.requireNonNull(stallWarning);
        if (workers < 1 || stallWarning.isNegative() || stallWarning.isZero())
            throw new IllegalArgumentException("Positive workers and stall warning required");
    }

    public static TournamentSessionOptions defaults() {
        return new TournamentSessionOptions(1, false, Duration.ofSeconds(60));
    }
}
