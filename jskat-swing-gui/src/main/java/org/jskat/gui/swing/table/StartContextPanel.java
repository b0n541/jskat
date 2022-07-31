package org.jskat.gui.swing.table;

import org.jskat.gui.action.main.StartSkatSeriesAction;
import org.jskat.gui.swing.LayoutFactory;

import javax.swing.*;

class StartContextPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final StartSkatSeriesAction action;

    public StartContextPanel(StartSkatSeriesAction newAction) {

        this.action = newAction;
        initPanel();
    }

    public void initPanel() {

        this.setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill"));

        JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill"));
        panel.add(new JButton(this.action), "center");
        panel.setOpaque(false);
        this.add(panel, "center");

        setOpaque(false);
    }
}
