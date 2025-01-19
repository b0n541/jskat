package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for handling the "schieben"-Button in a ramsch game
 */
public class SchiebenAction extends AbstractHumanJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public SchiebenAction() {

        putValue(Action.NAME, "Schieben");
        putValue(Action.SHORT_DESCRIPTION, "Schieben");

        setActionCommand(JSkatAction.SCHIEBEN);
        setIcon(Icon.PLAY);
    }
}
