package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;


/**
 * Implements the action for leaving a skat table on ISS
 */
public class LeaveIssTableAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public LeaveIssTableAction() {
        putValue(NAME, STRINGS.getString("leave_table"));
        setIcon(JSkatGraphicRepository.Icon.LOG_OUT);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {
        if (e.getSource() instanceof String) {
            JSkatMaster.INSTANCE.getIssController().leaveTable((String) e.getSource());
        }
    }
}
