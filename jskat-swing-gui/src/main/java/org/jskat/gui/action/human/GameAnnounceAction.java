package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for announcing a game
 */
public class GameAnnounceAction extends AbstractHumanJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public GameAnnounceAction() {

        putValue(Action.NAME, STRINGS.getString("announce")); //$NON-NLS-1$

        setActionCommand(JSkatAction.ANNOUNCE_GAME);
        setIcon(Icon.PLAY);
    }
}
