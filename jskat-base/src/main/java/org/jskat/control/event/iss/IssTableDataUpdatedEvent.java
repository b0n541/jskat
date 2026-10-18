package org.jskat.control.event.iss;

/**
 * Defines the iss table data updated event type.
 */
/**
 * Event carrying updated metadata for an Internet Skat table.
 * @param tableName table name
 * @param maxPlayers table capacity
 * @param gamesPlayed games played at the table
 * @param player1 name of the first seated player
 * @param player2 name of the second seated player
 * @param player3 name of the third seated player
 */
public record IssTableDataUpdatedEvent(String tableName,
                                       int maxPlayers,
                                       long gamesPlayed,
                                       String player1,
                                       String player2,
                                       String player3) {
}
