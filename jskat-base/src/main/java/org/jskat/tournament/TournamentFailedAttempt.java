package org.jskat.tournament;

import java.util.*;

/** Failed attempts never carry a score or successful outcome. */
public record TournamentFailedAttempt(
        String gameId,
        UUID attemptId,
        UUID sessionId,
        String phase,
        String error,
        List<TournamentGameEvent> events) {
    public TournamentFailedAttempt {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(attemptId);
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(phase);
        Objects.requireNonNull(error);
        events = List.copyOf(events);
    }
}
