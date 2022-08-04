package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for calling Contra.
 */
public class CallContraAction extends AbstractHumanJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public CallContraAction() {

        putValue(Action.NAME, STRINGS.getString("call_contra"));
        putValue(Action.SHORT_DESCRIPTION, STRINGS.getString("call_contra"));

        setActionCommand(JSkatAction.CALL_CONTRA);
        setIcon(Icon.PLAY);
    }
}
