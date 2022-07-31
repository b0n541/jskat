package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for making a bid
 */
public class MakeBidAction extends AbstractHumanJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public MakeBidAction() {

        putValue(Action.NAME, STRINGS.getString("make_bid"));
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("make_bid_short_description"));

        setActionCommand(JSkatAction.MAKE_BID);
        setIcon(Icon.OK);
    }
}
