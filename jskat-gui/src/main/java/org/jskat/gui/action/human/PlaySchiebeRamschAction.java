package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for playing a schieberamsch game in a ramsch game
 */
public class PlaySchiebeRamschAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PlaySchiebeRamschAction() {

        putValue(NAME, "Play schieberamsch game");
        putValue(SHORT_DESCRIPTION, "Play schieberamsch game");

        setActionCommand(JSkatAction.PLAY_SCHIEBERAMSCH);
        setIcon(Icon.PLAY);
    }
}
