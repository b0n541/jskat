package org.jskat.gui.action.main;

import org.jskat.control.command.general.ShowLicenseCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for showing the license
 */
public class LicenseAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public LicenseAction() {

        putValue(NAME, STRINGS.getString("license"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("license_tooltip"));
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new ShowLicenseCommand());
    }
}
