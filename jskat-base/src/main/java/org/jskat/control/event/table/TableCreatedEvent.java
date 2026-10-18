package org.jskat.control.event.table;

import org.jskat.data.JSkatViewType;

/**
 * This event is created when a skat table is created.
 */
/**
 * Event raised when a table is created in the application.
 *
 * @param tableType kind of table that was created
 * @param tableName name assigned to the new table
 */
public record TableCreatedEvent(JSkatViewType tableType, String tableName) {
}
