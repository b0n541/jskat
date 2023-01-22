package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for holding a bid
 */
public class HoldBidAction extends AbstractHumanJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public HoldBidAction() {

        putValue(Action.NAME, "hold bid");
        putValue(Action.SHORT_DESCRIPTION, "Hold this bid");

        setActionCommand(JSkatAction.HOLD_BID);
        setIcon(Icon.OK);
    }
}
