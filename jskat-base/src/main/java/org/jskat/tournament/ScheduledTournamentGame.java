package org.jskat.tournament;

import org.jskat.util.Card;

import java.util.*;

/** Authoritative immutable inputs. Epoch indexes start at zero; deck order is dealing order. */
public record ScheduledTournamentGame(
        String gameId,
        long epoch,
        String blockId,
        List<TournamentSeat> seats,
        List<Card> deck,
        long deckSeed) {
    public ScheduledTournamentGame {
        Objects.requireNonNull(gameId);
        Objects.requireNonNull(blockId);
        seats = List.copyOf(seats);
        deck = List.copyOf(deck);
        if (gameId.isBlank()
                || blockId.isBlank()
                || epoch < 0
                || seats.size() != 3
                || new HashSet<>(seats.stream().map(TournamentSeat::seat).toList()).size() != 3
                || deck.size() != 32
                || new HashSet<>(deck).size() != 32)
            throw new IllegalArgumentException("Invalid retained game inputs");
        seats = seats.stream().sorted(Comparator.comparingInt(s -> s.seat().getOrder())).toList();
    }
}
