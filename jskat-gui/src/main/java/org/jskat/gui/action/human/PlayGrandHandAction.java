package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for playing a grand hand game in a ramsch game
 */
public class PlayGrandHandAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PlayGrandHandAction() {

        putValue(NAME, "Play grand hand game");
        putValue(SHORT_DESCRIPTION, "Play grand hand game");

        setActionCommand(JSkatAction.PLAY_GRAND_HAND);
        setIcon(Icon.PLAY);
    }
}
