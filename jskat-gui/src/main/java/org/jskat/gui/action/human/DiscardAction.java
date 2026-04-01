package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for discarding
 */
public class DiscardAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public DiscardAction() {

        putValue(NAME, "Discard");
        putValue(SHORT_DESCRIPTION, "Discard cards");

        setActionCommand(JSkatAction.DISCARD_CARDS);
    }
}
