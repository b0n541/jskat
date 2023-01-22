package org.jskat.gui.action.main;

import org.jskat.control.command.general.ShowHelpCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for showing about dialog
 */
public class HelpAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public HelpAction() {

        putValue(Action.NAME, STRINGS.getString("help"));
        putValue(Action.SHORT_DESCRIPTION, STRINGS.getString("help_tooltip"));

        setIcon(Icon.HELP);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        EVENTBUS.post(new ShowHelpCommand());
    }
}
