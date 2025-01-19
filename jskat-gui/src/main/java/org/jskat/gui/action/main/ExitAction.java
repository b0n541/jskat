package org.jskat.gui.action.main;

import org.jskat.control.JSkatMaster;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for exiting JSkat
 */
public class ExitAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ExitAction() {

        putValue(Action.NAME, STRINGS.getString("exit_jskat"));
        putValue(Action.SHORT_DESCRIPTION,
                STRINGS.getString("exit_jskat_tooltip"));

        setIcon(Icon.EXIT);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JSkatMaster.INSTANCE.exitJSkat();
    }
}
