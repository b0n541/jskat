package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for calling Contra.
 */
public class CallContraAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public CallContraAction() {

        putValue(NAME, STRINGS.getString("callContra"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("callContra"));

        setActionCommand(JSkatAction.CALL_CONTRA);
        setIcon(Icon.PLAY);
    }
}
