package org.jskat.control.event.general;

/**
 * This event is created when a user puts in a table name that already exists.
 */
public class DuplicateTableNameInputEvent {

	public final String tableName;

	public DuplicateTableNameInputEvent(String tableName) {
		this.tableName = tableName;
	}
}
