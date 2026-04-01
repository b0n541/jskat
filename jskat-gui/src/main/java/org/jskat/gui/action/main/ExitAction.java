package org.jskat.gui.action.main;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for exiting JSkat
 */
public class ExitAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ExitAction() {

        putValue(NAME, STRINGS.getString("exit_jskat"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("exit_jskat_tooltip"));

        setIcon(Icon.EXIT);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        JSkatMaster.INSTANCE.exitJSkat();
    }
}
