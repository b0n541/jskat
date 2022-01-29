package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for calling Re.
 */
public class CallReAction extends AbstractHumanJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public CallReAction() {

        putValue(Action.NAME, STRINGS.getString("call_re"));
        putValue(Action.SHORT_DESCRIPTION, STRINGS.getString("call_re"));

        setActionCommand(JSkatAction.CALL_RE);
        setIcon(Icon.PLAY);
    }
}
