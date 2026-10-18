package org.jskat.control.event.table;

import org.jskat.data.JSkatViewType;

/**
 * This event is created when a skat table is removed.
 */
/**
 * Event raised when a table is removed from the application.
 *
 * @param tableName name of the removed table
 * @param tableType kind of table that was removed
 */
public record TableRemovedEvent(String tableName, JSkatViewType tableType) {
}
