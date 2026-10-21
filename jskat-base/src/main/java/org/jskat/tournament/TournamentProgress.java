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
        String message,
        TournamentMetrics metrics,
        Kind kind) {
    /** Presentation policy is independent of diagnostic wording. */
    public enum Kind {
        ROUTINE, NOTICE, STALL_WARNING, FAILURE;

        public boolean urgent() {
            return this == STALL_WARNING || this == FAILURE;
        }
    }

    public TournamentProgress(
            State state, long totalGames, long admittedGames, long committedGames,
            long failedAttempts, long completeEpochs, boolean cancellationRequested,
            boolean reproducible, String message, TournamentMetrics metrics) {
        this(state, totalGames, admittedGames, committedGames, failedAttempts, completeEpochs,
                cancellationRequested, reproducible, message, metrics, Kind.ROUTINE);
    }
    public TournamentProgress(
            State state,
            long totalGames,
            long admittedGames,
            long committedGames,
            long failedAttempts,
            long completeEpochs,
            boolean cancellationRequested,
            boolean reproducible,
            String message) {
        this(
                state,
                totalGames,
                admittedGames,
                committedGames,
                failedAttempts,
                completeEpochs,
                cancellationRequested,
                reproducible,
                message,
                TournamentMetrics.empty());
    }

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
        Objects.requireNonNull(metrics);
        Objects.requireNonNull(kind);
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
