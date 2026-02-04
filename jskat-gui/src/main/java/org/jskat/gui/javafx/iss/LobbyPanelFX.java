package org.jskat.gui.javafx.iss;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.jskat.data.iss.ChatMessage;
import org.jskat.gui.swing.AbstractTabPanel;

import javax.swing.*;
import java.awt.*;

@Deprecated
public class LobbyPanelFX extends AbstractTabPanel {

    private LobbyPanel lobbyPanel;

    public LobbyPanelFX(String tableName, ActionMap actions) {
        super(tableName, actions);
    }

    @Override
    protected void initPanel() {
        setLayout(new BorderLayout());
        final JFXPanel fxPanel = new JFXPanel();
        // Make JFXPanel flexible to allow it to grow within its Swing parent
        fxPanel.setPreferredSize(new Dimension(0, 0)); // This tells the layout manager it has no preferred size
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            lobbyPanel = new LobbyPanel(getActionMap());
            // Bind JavaFX panel's preferred size to JFXPanel's actual size
            lobbyPanel.prefWidthProperty().bind(fxPanel.getScene().heightProperty());
            lobbyPanel.prefHeightProperty().bind(fxPanel.getScene().heightProperty());
            Scene scene = new Scene(lobbyPanel);
            fxPanel.setScene(scene);
        });
    }

    @Override
    protected void setFocus() {
        if (lobbyPanel != null) {
            Platform.runLater(() -> lobbyPanel.setFocus());
        }
    }

    public void updatePlayer(final String playerName, final String language, final long gamesPlayed, final double strength) {
        if (lobbyPanel != null) {
            Platform.runLater(() -> lobbyPanel.updatePlayer(playerName, language, gamesPlayed, strength));
        }
    }

    public void removePlayer(final String playerName) {
        if (lobbyPanel != null) {
            Platform.runLater(() -> lobbyPanel.removePlayer(playerName));
        }
    }

    public void updateTable(final String tableName, final int maxPlayers, final long gamesPlayed, final String player1, final String player2, final String player3) {
        if (lobbyPanel != null) {
            Platform.runLater(() -> lobbyPanel.updateTable(tableName, maxPlayers, gamesPlayed, player1, player2, player3));
        }
    }

    public void removeTable(final String tableName) {
        if (lobbyPanel != null) {
            Platform.runLater(() -> lobbyPanel.removeTable(tableName));
        }
    }

    public void appendChatMessage(final ChatMessage message) {
        if (lobbyPanel != null) {
            Platform.runLater(() -> lobbyPanel.appendChatMessage(message));
        }
    }
}
