package org.jskat.tournament;

import org.jskat.util.Player;

import java.util.*;

/** Frozen after every callback. inputDigest links the exact retained resolved configuration. */
public record TournamentGameResult(
        String gameId,
        UUID attemptId,
        UUID sessionId,
        String inputDigest,
        Outcome outcome,
        Map<Player, Long> seatScores,
        List<TournamentGameEvent> events,
        List<TournamentInferenceFallback> fallbacks) {
    public record Outcome(
            boolean allPassed,
            Player declarer,
            boolean declarerWon,
            long signedGameValue,
            TournamentContract contract,
            Map<Player, Integer> cardPoints,
            Map<Player, Integer> tricks) {
        public Outcome {
            cardPoints = Map.copyOf(cardPoints);
            tricks = Map.copyOf(tricks);
            if (allPassed
                    ? (declarer != null || declarerWon || signedGameValue != 0 || contract != null)
                    : (declarer == null || contract == null))
                throw new IllegalArgumentException("Invalid legal outcome");
            if (!allPassed
                    && (cardPoints.size() != 3
                            || tricks.size() != 3
                            || cardPoints.values().stream().anyMatch(v -> v < 0 || v > 120)
                            || tricks.values().stream().anyMatch(v -> v < 0 || v > 10)))
                throw new IllegalArgumentException("Invalid role observations");
        }
    }

    public TournamentGameResult {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(attemptId);
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(outcome);
        if (inputDigest == null || !inputDigest.matches("[0-9a-f]{64}"))
            throw new IllegalArgumentException("Resolved input digest required");
        seatScores = Map.copyOf(seatScores);
        events = List.copyOf(events);
        fallbacks = List.copyOf(fallbacks);
        if (seatScores.size() != 3)
            throw new IllegalArgumentException("Three seat scores required");
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).sequence() != i)
                throw new IllegalArgumentException("Event sequence must be contiguous from zero");
    }
}
