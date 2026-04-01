package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for handling card panel clicks
 */
public class PlayHandGameAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PlayHandGameAction() {

        putValue(NAME, "Play hand game");
        putValue(SHORT_DESCRIPTION, "Play hand game");

        setActionCommand(JSkatAction.PLAY_HAND_GAME);
    }
}
