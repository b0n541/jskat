package org.jskat.gui.swing.table;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.GameSummary;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import javax.swing.*;
import java.util.List;

class GameOverPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private GameResultPanel gameResultPanel;
    private SkatPanel skatPanel;

    public GameOverPanel(ActionMap actions, List<JSkatAction> activeActions) {

        initPanel(actions, activeActions);
    }

    private void initPanel(ActionMap actions, List<JSkatAction> activeActions) {

        setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill"));

        JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "[grow][shrink]"));
        gameResultPanel = new GameResultPanel();
        panel.add(gameResultPanel, "grow, wrap");

        JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout("fill"));
        skatPanel = new SkatPanel();
        buttonPanel.add(skatPanel, "grow");
        for (JSkatAction action : activeActions) {
            buttonPanel.add(new JButton(actions.get(action)), "center, shrink");
        }
        buttonPanel.setOpaque(false);
        panel.add(buttonPanel, "center");

        panel.setOpaque(false);
        add(panel, "center");

        setOpaque(false);
    }

    void setUserPosition(Player player) {

        gameResultPanel.setUserPosition(player);
    }

    void setGameSummary(GameSummary summary) {

        gameResultPanel.setGameSummary(summary);
    }

    void setDealtSkat(CardList skat) {

        skatPanel.setSkatCards(skat);
    }

    public void resetPanel() {

        gameResultPanel.resetPanel();
        skatPanel.resetPanel();
    }
}
