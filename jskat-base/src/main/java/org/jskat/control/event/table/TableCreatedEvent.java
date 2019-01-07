/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control.event.table;

import org.jskat.data.JSkatViewType;

/**
 * This event is created when a skat table is created.
 */
public class TableCreatedEvent extends AbstractTableEvent {

	public final JSkatViewType tableType;

	/**
	 * Constructor.
	 *
	 * @param tableType
	 *            Type to distinguish between local and ISS tables
	 * @param tableName
	 *            Table name
	 */
	public TableCreatedEvent(JSkatViewType tableType, String tableName) {

		super(tableName);
		this.tableType = tableType;
	}

	@Override
	public String toString() {
		return "TableCreatedEvent: type: " + tableType + " name: " + tableName;
	}
}
