package org.jskat.gui.javafx.iss;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.swing.AbstractTabPanel;

import java.awt.*;
import java.util.Map;

@Deprecated
public class LoginPanelFX extends AbstractTabPanel {

    private LoginPanel loginPanel;

    public LoginPanelFX(String tableName, Map<JSkatAction, AbstractJSkatAction> actions) {
        super(tableName, actions);
    }

    @Override
    protected void initPanel() {
        setLayout(new BorderLayout());
        final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            loginPanel = new LoginPanel(actions);
            Scene scene = new Scene(loginPanel);
            fxPanel.setScene(scene);
        });
    }

    @Override
    protected void setFocus() {
        if (loginPanel != null) {
            Platform.runLater(() -> loginPanel.setFocus());
        }
    }
}
