package org.jskat.gui.swing.table;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.javafx.table.SkatTableNode;
import org.jskat.gui.javafx.table.SkatTablePanel;
import org.jskat.gui.swing.AbstractTabPanel;

import java.awt.*;
import java.util.Map;

public class SkatTablePanelWrapper extends AbstractTabPanel {

    protected final SkatTablePanel skatTablePanel;

    public SkatTablePanelWrapper(String tableName, Map<JSkatAction, AbstractJSkatAction> actions) {
        super(tableName, actions);
        skatTablePanel = new SkatTablePanel(tableName, actions);
    }

    protected SkatTablePanelWrapper(String tableName, Map<JSkatAction, AbstractJSkatAction> actions, SkatTablePanel panel) {
        super(tableName, actions);
        this.skatTablePanel = panel;
    }

    @Override
    protected void initPanel() {
        setLayout(new BorderLayout());
        final JFXPanel jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            SkatTableNode skatTableNode = new SkatTableNode(skatTablePanel);
            Scene scene = new Scene(skatTableNode);
            jfxPanel.setScene(scene);
        });
    }

    @Override
    protected void setFocus() {
        // Focus handling can be tricky between Swing and JavaFX.
        // For now, we can try to request focus on the JFXPanel.
        Platform.runLater(() -> skatTablePanel.requestFocus());
    }

    public SkatTablePanel getSkatTablePanel() {
        return skatTablePanel;
    }
}
