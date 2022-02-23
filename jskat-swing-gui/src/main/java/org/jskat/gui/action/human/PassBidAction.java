package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for passing a bid
 */
public class PassBidAction extends AbstractHumanJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PassBidAction() {

        putValue(Action.NAME, STRINGS.getString("pass")); //$NON-NLS-1$
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("pass_short_description")); //$NON-NLS-1$

        setActionCommand(JSkatAction.PASS_BID);
        setIcon(Icon.STOP);
    }
}
