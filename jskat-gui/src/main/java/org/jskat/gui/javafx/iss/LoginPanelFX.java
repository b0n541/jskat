package org.jskat.gui.javafx.iss;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.jskat.gui.swing.AbstractTabPanel;

import javax.swing.*;
import java.awt.*;

@Deprecated
public class LoginPanelFX extends AbstractTabPanel {

    private LoginPanel loginPanel;

    public LoginPanelFX(String tableName, ActionMap actions) {
        super(tableName, actions);
    }

    @Override
    protected void initPanel() {
        setLayout(new BorderLayout());
        final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            loginPanel = new LoginPanel(getActionMap());
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
