package org.jskat.tournament;

import java.time.Duration;
import java.util.*;

/** All elapsed times use a monotonic clock. Ratings are the latest coherent published summary. */
public record TournamentMetrics(
        long sequence,
        Duration elapsed,
        double gamesPerSecond,
        Optional<Duration> estimatedRemaining,
        List<RunningAttempt> runningAttempts,
        Optional<UUID> reportGeneration,
        Optional<String> summaryJson) {
    public TournamentMetrics {
        runningAttempts = List.copyOf(runningAttempts);
    }

    public record RunningAttempt(
            String gameId,
            UUID attemptId,
            List<UUID> competitors,
            Duration elapsed,
            boolean stallWarned) {
        public RunningAttempt {
            competitors = List.copyOf(competitors);
        }
    }

    public static TournamentMetrics empty() {
        return new TournamentMetrics(
                0,
                Duration.ZERO,
                0,
                Optional.empty(),
                List.of(),
                Optional.empty(),
                Optional.empty());
    }
}
