package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for calling Re.
 */
public class CallReAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public CallReAction() {

        putValue(NAME, STRINGS.getString("callRe"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("callRe"));

        setActionCommand(JSkatAction.CALL_RE);
        setIcon(Icon.PLAY);
    }
}
