package org.jskat.gui.action.main;

import org.jskat.control.JSkatMaster;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for creating a new table
 */
public class CreateTableAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public CreateTableAction() {

        putValue(Action.NAME, STRINGS.getString("play_on_local_table"));
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("new_table_tooltip"));

        setIcon(Icon.TABLE);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        JSkatMaster.INSTANCE.createTable();
    }
}
