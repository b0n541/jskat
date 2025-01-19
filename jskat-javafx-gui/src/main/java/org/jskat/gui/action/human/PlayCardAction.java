package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for playing a card
 */
public class PlayCardAction extends AbstractHumanJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PlayCardAction() {

        setActionCommand(JSkatAction.PLAY_CARD);
    }
}
