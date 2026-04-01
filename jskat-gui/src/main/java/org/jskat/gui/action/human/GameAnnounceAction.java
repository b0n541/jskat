package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for announcing a game
 */
public class GameAnnounceAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public GameAnnounceAction() {

        putValue(NAME, STRINGS.getString("announce"));

        setActionCommand(JSkatAction.ANNOUNCE_GAME);
        setIcon(Icon.PLAY);
    }
}
