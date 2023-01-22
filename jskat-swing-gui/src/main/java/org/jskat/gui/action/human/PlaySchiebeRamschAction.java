package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for playing a schieberamsch game in a ramsch game
 */
public class PlaySchiebeRamschAction extends AbstractHumanJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PlaySchiebeRamschAction() {

        putValue(Action.NAME, "Play schieberamsch game");
        putValue(Action.SHORT_DESCRIPTION, "Play schieberamsch game");

        setActionCommand(JSkatAction.PLAY_SCHIEBERAMSCH);
        setIcon(Icon.PLAY);
    }
}
