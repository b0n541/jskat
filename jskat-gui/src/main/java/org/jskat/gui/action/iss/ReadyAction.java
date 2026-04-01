package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for sending a ready message to ISS
 */
public class ReadyAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ReadyAction() {

        putValue(NAME, STRINGS.getString("ready"));
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        if (e.getSource() instanceof String) {

            JSkatMaster.INSTANCE.getIssController().sendReadySignal((String) e.getSource());
        }
    }
}
