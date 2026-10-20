package org.jskat.tournament;

import java.util.Objects;

/** Payload is canonical JSON text, never a mutable game-data or JSON tree reference. */
public record TournamentGameEvent(long sequence, String type, String payloadJson) {
    public TournamentGameEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(payloadJson);
        if (sequence < 0 || type.isBlank()) throw new IllegalArgumentException("Invalid event");
        payloadJson = CanonicalJson.encode(CanonicalJson.parse(payloadJson));
    }
}
