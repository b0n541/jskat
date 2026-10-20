package org.jskat.tournament;

import java.util.Objects;
import java.util.UUID;

public record TournamentInferenceFallback(
        UUID competitorId, String gameId, String phase, String error) {
    public TournamentInferenceFallback {
        Objects.requireNonNull(competitorId);
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(phase);
        Objects.requireNonNull(error);
    }
}
