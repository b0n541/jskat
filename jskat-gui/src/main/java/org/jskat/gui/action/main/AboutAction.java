package org.jskat.gui.action.main;

import org.jskat.control.command.general.ShowAboutInformationCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for showing about dialog.
 */
public class AboutAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public AboutAction() {
        putValue(NAME, STRINGS.getString("about"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("aboutTooltip"));
        setIcon(Icon.ABOUT);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {
        EVENTBUS.post(new ShowAboutInformationCommand());
    }
}
