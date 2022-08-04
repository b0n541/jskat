package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for playing a grand hand game in a ramsch game
 */
public class PlayGrandHandAction extends AbstractHumanJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PlayGrandHandAction() {

        putValue(Action.NAME, "Play grand hand game");
        putValue(Action.SHORT_DESCRIPTION, "Play grand hand game");

        setActionCommand(JSkatAction.PLAY_GRAND_HAND);
        setIcon(Icon.PLAY);
    }
}
