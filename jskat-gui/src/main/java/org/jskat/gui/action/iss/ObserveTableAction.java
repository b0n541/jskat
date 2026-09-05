package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for observing a skat table on ISS
 */
public class ObserveTableAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ObserveTableAction() {

        putValue(NAME, STRINGS.getString("issObserveTable"));
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        if (e.getSource() instanceof String) {

            JSkatMaster.INSTANCE.getIssController().observeTable((String) e.getSource());
        }
    }
}
