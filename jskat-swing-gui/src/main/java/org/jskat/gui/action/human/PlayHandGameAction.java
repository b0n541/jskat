package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;

import javax.swing.*;

/**
 * Implements the action for handling card panel clicks
 */
public class PlayHandGameAction extends AbstractHumanJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PlayHandGameAction() {

        putValue(Action.NAME, "Play hand game");
        putValue(Action.SHORT_DESCRIPTION, "Play hand game");

        setActionCommand(JSkatAction.PLAY_HAND_GAME);
    }
}
