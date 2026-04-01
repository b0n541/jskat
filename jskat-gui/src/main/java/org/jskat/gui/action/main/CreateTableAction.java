package org.jskat.gui.action.main;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for creating a new table
 */
public class CreateTableAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public CreateTableAction() {

        putValue(NAME, STRINGS.getString("play_on_local_table"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("new_table_tooltip"));

        setIcon(Icon.TABLE);
    }

    @Override
    public void actionPerformed(final JSkatActionEvent e) {

        JSkatMaster.INSTANCE.createTable();
    }
}
