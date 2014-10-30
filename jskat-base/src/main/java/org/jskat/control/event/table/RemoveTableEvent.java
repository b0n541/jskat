package org.jskat.control.event.table;

import org.jskat.data.JSkatViewType;

/**
 * This event is created when a skat table is removed.
 */
public class RemoveTableEvent {
	
	public final String tableName;
	public final JSkatViewType tableType;

	/**
	 * Constructor.
	 * 
	 * @param tableType Type to distinguish between local and ISS tables
	 * @param tableName Table name
	 */
	public RemoveTableEvent(JSkatViewType tableType, String tableName) {
	
		this.tableType = tableType;
		this.tableName = tableName;
	}
}
