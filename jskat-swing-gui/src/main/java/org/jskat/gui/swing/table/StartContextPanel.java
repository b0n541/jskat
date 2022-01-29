package org.jskat.gui.swing.table;

import org.jskat.gui.action.main.StartSkatSeriesAction;
import org.jskat.gui.swing.LayoutFactory;

import javax.swing.*;

class StartContextPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private StartSkatSeriesAction action;

    public StartContextPanel(StartSkatSeriesAction newAction) {

        this.action = newAction;
        initPanel();
    }

    public void initPanel() {

        this.setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

        JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
        panel.add(new JButton(this.action), "center"); //$NON-NLS-1$
        panel.setOpaque(false);
        this.add(panel, "center"); //$NON-NLS-1$

        setOpaque(false);
    }
}
