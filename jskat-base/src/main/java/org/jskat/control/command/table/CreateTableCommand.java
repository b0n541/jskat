
package org.jskat.control.command.table;

import org.jskat.data.JSkatViewType;

/**
 * Defines the create table command type.
 */
public class CreateTableCommand extends AbstractTableCommand {

	/**
	 * Type of table to create.
	 */
	public final JSkatViewType tableType;

	/**
	 * Performs create table command.
	 * @param tableType table type value.
	 * @param tableName table name value.
	 */
	public CreateTableCommand(final JSkatViewType tableType, final String tableName) {
		super(tableName);
		this.tableType = tableType;
	}
}
