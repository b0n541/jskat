package org.jskat.gui.action.main;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for handling card panel clicks during discarding
 */
public class PutCardIntoSkatAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PutCardIntoSkatAction() {

        setActionCommand(JSkatAction.PUT_CARD_INTO_SKAT);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        JSkatMaster.INSTANCE.putCardIntoSkat(new JSkatActionEvent(e
                .getActionCommand(), e.getSource()));
    }
}
