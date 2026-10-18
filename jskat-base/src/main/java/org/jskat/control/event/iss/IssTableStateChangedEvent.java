package org.jskat.control.event.iss;

import org.jskat.control.event.table.AbstractTableEvent;
import org.jskat.data.iss.TablePanelStatus;

/**
 * Defines the iss table state changed event type.
 */
public class IssTableStateChangedEvent extends AbstractTableEvent {
    /**
     * Current status of the table panel.
     */
    public final TablePanelStatus status;

    /**
     * Performs iss table state changed event.
     * @param tableName table name value.
     * @param status status value.
     */
    public IssTableStateChangedEvent(String tableName, TablePanelStatus status) {
        super(tableName);
        this.status = status;
    }
}
