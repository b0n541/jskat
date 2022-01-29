
package org.jskat.control.command.table;

import org.jskat.data.JSkatViewType;

public class CreateTableCommand extends AbstractTableCommand {

	public final JSkatViewType tableType;

	public CreateTableCommand(final JSkatViewType tableType, final String tableName) {
		super(tableName);
		this.tableType = tableType;
	}
}
