package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for holding a bid
 */
public class HoldBidAction extends AbstractHumanJSkatAction {

    /**
     * Creates the hold bid action.
     *
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public HoldBidAction() {

        putValue(NAME, "hold bid");
        putValue(SHORT_DESCRIPTION, "Hold this bid");

        setActionCommand(JSkatAction.HOLD_BID);
        setIcon(Icon.OK);
    }
}
