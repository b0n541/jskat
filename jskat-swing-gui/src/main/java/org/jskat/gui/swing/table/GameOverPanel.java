package org.jskat.gui.swing.table;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.data.GameSummary;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.CardList;
import org.jskat.util.Player;

import javax.swing.*;
import java.util.List;

class GameOverPanel extends JPanel {


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

        JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout("insets 0, fill", "[280!][grow]", "[fill]"));
        skatPanel = new SkatPanel();
        buttonPanel.add(skatPanel, "w 280!, hmax 150");

        JPanel buttonsGroup = new JPanel(LayoutFactory.getMigLayout("insets 0, gap 15", "", ""));
        for (JSkatAction action : activeActions) {
            buttonsGroup.add(new JButton(actions.get(action)));
        }
        buttonsGroup.setOpaque(false);
        buttonPanel.add(buttonsGroup, "center");
        buttonPanel.setOpaque(false);
        panel.add(buttonPanel, "growx");

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
