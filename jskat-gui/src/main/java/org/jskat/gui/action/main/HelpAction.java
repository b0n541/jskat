package org.jskat.gui.action.main;

import org.jskat.control.command.general.ShowHelpCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for showing about dialog
 */
public class HelpAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public HelpAction() {

        putValue(NAME, STRINGS.getString("help"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("help_tooltip"));

        setIcon(Icon.HELP);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new ShowHelpCommand());
    }
}
