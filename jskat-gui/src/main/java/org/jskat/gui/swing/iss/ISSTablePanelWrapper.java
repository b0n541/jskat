package org.jskat.gui.swing.iss;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.javafx.iss.IssTablePanel;
import org.jskat.gui.swing.table.SkatTablePanelWrapper;

import java.util.Map;

@Deprecated
public class ISSTablePanelWrapper extends SkatTablePanelWrapper {

    public ISSTablePanelWrapper(String tableName, Map<JSkatAction, AbstractJSkatAction> actions) {
        super(tableName, actions, new IssTablePanel(tableName, actions));
    }

    public IssTablePanel getIssTablePanel() {
        return (IssTablePanel) skatTablePanel;
    }
}
