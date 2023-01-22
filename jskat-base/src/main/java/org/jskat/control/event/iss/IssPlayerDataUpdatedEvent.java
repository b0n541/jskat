package org.jskat.control.event.iss;

public record IssPlayerDataUpdatedEvent(String playerName,
                                        String language,
                                        long gamesPlayed,
                                        double strength) {
}
