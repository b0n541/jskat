package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for passing a bid
 */
public class PassBidAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PassBidAction() {

        putValue(NAME, STRINGS.getString("pass"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("pass_short_description"));

        setActionCommand(JSkatAction.PASS_BID);
        setIcon(Icon.STOP);
    }
}
