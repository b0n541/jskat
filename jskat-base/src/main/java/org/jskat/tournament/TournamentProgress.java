package org.jskat.tournament;

import java.util.Objects;

/** Durable commits and admissions are deliberately distinct counters. */
public record TournamentProgress(
        State state,
        long totalGames,
        long admittedGames,
        long committedGames,
        long failedAttempts,
        long completeEpochs,
        boolean cancellationRequested,
        boolean reproducible,
        String message) {
    public enum State {
        PREPARING,
        RUNNING,
        CANCELLING,
        FINALIZING,
        COMPLETED,
        CANCELLED,
        FAILED
    }

    public TournamentProgress {
        Objects.requireNonNull(state);
        Objects.requireNonNull(message);
        if (totalGames < 0
                || admittedGames < 0
                || committedGames < 0
                || failedAttempts < 0
                || completeEpochs < 0
                || committedGames > totalGames)
            throw new IllegalArgumentException("Invalid progress counters");
    }

    @FunctionalInterface
    public interface Observer {
        void onProgress(TournamentProgress progress);
    }
}
