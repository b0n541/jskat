package org.jskat.control.event.iss;

/**
 * Defines the iss player data updated event type.
 */
/**
 * Event carrying updated profile data for an Internet Skat player.
 * @param playerName player's display name
 * @param language player's preferred language code
 * @param gamesPlayed number of games played
 * @param strength player's server rating
 */
public record IssPlayerDataUpdatedEvent(String playerName,
                                        String language,
                                        long gamesPlayed,
                                        double strength) {
}
