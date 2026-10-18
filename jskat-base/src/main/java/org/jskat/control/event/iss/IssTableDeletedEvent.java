package org.jskat.control.event.iss;

/**
 * Defines the iss table deleted event type.
 */
/**
 * Event raised when a server table is deleted.
 * @param tableName name of the deleted table
 */
public record IssTableDeletedEvent(String tableName) {
}
