package org.jskat.gui.swing.iss;

import org.jskat.gui.javafx.table.ISSTablePanel;
import org.jskat.gui.swing.table.SkatTablePanelWrapper;

import javax.swing.*;

public class ISSTablePanelWrapper extends SkatTablePanelWrapper {

    public ISSTablePanelWrapper(String tableName, ActionMap actions) {
        super(tableName, actions, new ISSTablePanel(tableName, actions));
    }

    public ISSTablePanel getIssTablePanel() {
        return (ISSTablePanel) skatTablePanel;
    }
}
