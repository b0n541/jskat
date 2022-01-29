package org.jskat.gui.action.main;

import org.jskat.control.JSkatMaster;
import org.jskat.gui.action.AbstractJSkatAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for creating a new table
 */
public class ChangeActiveTableAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ChangeActiveTableAction() {

        putValue(Action.NAME, "Change active table"); //$NON-NLS-1$
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JSkatMaster.INSTANCE.setActiveTable(e.getActionCommand());
    }
}
