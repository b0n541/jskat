package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;

import javax.swing.*;

/**
 * Implements the action for discarding
 */
public class DiscardAction extends AbstractHumanJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public DiscardAction() {

        putValue(Action.NAME, "Discard");
        putValue(Action.SHORT_DESCRIPTION, "Discard cards");

        setActionCommand(JSkatAction.DISCARD_CARDS);
    }
}
