package org.jskat.gui.javafx.main;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.swing.AbstractTabPanel;

import java.awt.*;
import java.util.Map;

@Deprecated
public class WelcomePanelFX extends AbstractTabPanel {

    public WelcomePanelFX(String tableName, Map<JSkatAction, AbstractJSkatAction> actions) {
        super(tableName, actions);
    }

    @Override
    protected void initPanel() {
        setLayout(new BorderLayout());
        final JFXPanel fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WelcomePanel welcomePanel = new WelcomePanel(getActions());
            Scene scene = new Scene(welcomePanel);
            fxPanel.setScene(scene);
        });
    }

    @Override
    protected void setFocus() {
        // no focus needed
    }
}
