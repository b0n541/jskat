package org.jskat.control.event.table;

import org.jskat.data.JSkatViewType;

/**
 * This event is created when a skat table is created.
 */
public record TableCreatedEvent(JSkatViewType tableType, String tableName) {
}