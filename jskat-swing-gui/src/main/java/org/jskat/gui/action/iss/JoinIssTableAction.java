package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.gui.action.AbstractJSkatAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for joining a skat table on ISS
 */
public class JoinIssTableAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public JoinIssTableAction() {

        putValue(Action.NAME, "Join table");
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof String) {

            JSkatMaster.INSTANCE.getIssController().joinTable(
                    (String) e.getSource());
        }
    }
}
