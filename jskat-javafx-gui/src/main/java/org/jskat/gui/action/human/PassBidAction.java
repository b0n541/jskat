package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for passing a bid
 */
public class PassBidAction extends AbstractHumanJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PassBidAction() {

        putValue(Action.NAME, STRINGS.getString("pass"));
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("pass_short_description"));

        setActionCommand(JSkatAction.PASS_BID);
        setIcon(Icon.STOP);
    }
}
