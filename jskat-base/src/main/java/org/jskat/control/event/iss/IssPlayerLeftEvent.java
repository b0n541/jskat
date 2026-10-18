package org.jskat.control.event.iss;

/**
 * Defines the iss player left event type.
 */
/**
 * Event raised when a player leaves a server table.
 * @param playerName name of the player who left
 */
public record IssPlayerLeftEvent(String playerName) {
}
