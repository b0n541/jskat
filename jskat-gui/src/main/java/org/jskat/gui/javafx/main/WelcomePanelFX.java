package org.jskat.gui.javafx.main;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.jskat.gui.swing.AbstractTabPanel;

import javax.swing.*;
import java.awt.*;

public class WelcomePanelFX extends AbstractTabPanel {

    public WelcomePanelFX(String tableName, ActionMap actions) {
        super(tableName, actions);
    }

    @Override
    protected void initPanel() {
        setLayout(new BorderLayout());
        final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WelcomePanel welcomePanel = new WelcomePanel(getActionMap());
            Scene scene = new Scene(welcomePanel);
            fxPanel.setScene(scene);
        });
    }

    @Override
    protected void setFocus() {
        // no focus needed
    }
}
