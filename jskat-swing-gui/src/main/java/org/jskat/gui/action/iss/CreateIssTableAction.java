package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for creating a skat table on ISS
 */
public class CreateIssTableAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public CreateIssTableAction() {

        // FIXME (jan 23.11.2010) use CreateTableAction

        putValue(Action.NAME, STRINGS.getString("new_table"));
        setIcon(Icon.TABLE);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JSkatMaster.INSTANCE.getIssController().requestTableCreation();
    }
}
