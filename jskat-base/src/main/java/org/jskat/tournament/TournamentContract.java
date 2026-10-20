package org.jskat.tournament;

import org.jskat.util.GameType;

import java.util.Objects;

/** Immutable declared contract; all-pass has no contract. */
public record TournamentContract(
        GameType gameType,
        boolean hand,
        boolean ouvert,
        boolean schneiderAnnounced,
        boolean schwarzAnnounced) {
    public TournamentContract {
        Objects.requireNonNull(gameType);
        if (gameType == GameType.RAMSCH)
            throw new IllegalArgumentException("Ramsch is outside standard tournament rules");
    }
}
