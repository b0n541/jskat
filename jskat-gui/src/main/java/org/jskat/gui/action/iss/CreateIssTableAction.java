package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for creating a skat table on ISS
 */
public class CreateIssTableAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public CreateIssTableAction() {

        // FIXME (jan 23.11.2010) use CreateTableAction

        putValue(NAME, STRINGS.getString("newTable"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("newTableTooltip"));
        setIcon(Icon.TABLE);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        JSkatMaster.INSTANCE.getIssController().requestTableCreation();
    }
}
