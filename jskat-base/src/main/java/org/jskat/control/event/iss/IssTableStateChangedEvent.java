package org.jskat.control.event.iss;

import org.jskat.control.event.table.AbstractTableEvent;
import org.jskat.data.iss.TablePanelStatus;

public class IssTableStateChangedEvent extends AbstractTableEvent {
    public final TablePanelStatus status;

    public IssTableStateChangedEvent(String tableName, TablePanelStatus status) {
        super(tableName);
        this.status = status;
    }
}
