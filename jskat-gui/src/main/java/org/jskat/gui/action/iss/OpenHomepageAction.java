package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for opening the ISS homepage in the default browser
 */
public class OpenHomepageAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public OpenHomepageAction() {

        putValue(NAME, STRINGS.getString("open_iss_homepage"));
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        JSkatMaster.INSTANCE.getView().openWebPage("https://skatgame.net/iss/");
    }
}
