package org.jskat.gui.swing.iss;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.swing.LayoutFactory;

import javax.swing.*;
import java.util.List;

/**
 * Context panel for starting a skat series on ISS
 */
class StartContextPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public StartContextPanel(final ActionMap actions, final List<JSkatAction> activeActions) {

        initPanel(actions, activeActions);
    }

    public void initPanel(final ActionMap actions, final List<JSkatAction> activeActions) {

        this.setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

        JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
        for (JSkatAction action : activeActions) {
            panel.add(new JButton(actions.get(action)), "center"); //$NON-NLS-1$
        }
        panel.setOpaque(false);
        this.add(panel, "center"); //$NON-NLS-1$

        setOpaque(false);
    }
}
