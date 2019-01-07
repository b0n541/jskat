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
 * This event is created when a skat table is removed.
 */
public class TableRemovedEvent extends AbstractTableEvent {
	
	public final JSkatViewType tableType;

	/**
	 * Constructor.
	 * 
	 * @param tableType Type to distinguish between different table types
	 * @param tableName Table name
	 */
	public TableRemovedEvent(String tableName, JSkatViewType tableType) {
	
		super(tableName);
		this.tableType = tableType;
	}
}
