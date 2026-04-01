package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssShowLoginCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for showing the ISS login panel
 */
public class ShowLoginPanelAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ShowLoginPanelAction() {

        putValue(NAME, STRINGS.getString("iss_login"));
        setIcon(Icon.LOG_IN);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new IssShowLoginCommand());
    }
}
