package org.jskat.control.event.table;

import org.jskat.data.JSkatViewType;

/**
 * This event is created when a skat table is removed.
 */
public record TableRemovedEvent(String tableName, JSkatViewType tableType) {
}
