package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for making a bid
 */
public class MakeBidAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public MakeBidAction() {

        putValue(NAME, STRINGS.getString("make_bid"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("make_bid_short_description"));

        setActionCommand(JSkatAction.MAKE_BID);
        setIcon(Icon.OK);
    }
}
