package org.jskat.gui.action.iss;

import org.jskat.control.JSkatMaster;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for showing about dialog
 */
public class ShowLoginPanelAction extends AbstractJSkatAction {


    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ShowLoginPanelAction() {

        putValue(Action.NAME, STRINGS.getString("play_on_iss"));

        setActionCommand(JSkatAction.SHOW_ISS_LOGIN);
        setIcon(Icon.CONNECT_ISS);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        JSkatMaster.INSTANCE.getIssController().showISSLoginPanel();
    }
}
