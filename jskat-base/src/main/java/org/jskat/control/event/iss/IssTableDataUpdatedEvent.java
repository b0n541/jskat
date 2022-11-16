package org.jskat.control.event.iss;

public record IssTableDataUpdatedEvent(String tableName,
                                       int maxPlayers,
                                       long gamesPlayed,
                                       String player1,
                                       String player2,
                                       String player3) {
}
