package org.jskat.tournament;

import org.jskat.util.Player;

import java.util.Objects;
import java.util.UUID;

public record TournamentSeat(Player seat, UUID competitorId, long seed) {
    public TournamentSeat {
        Objects.requireNonNull(seat);
        Objects.requireNonNull(competitorId);
    }
}
