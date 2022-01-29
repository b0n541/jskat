package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssResignCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for resigning a game on ISS
 */
public class ResignAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ResignAction() {

        putValue(Action.NAME, STRINGS.getString("resign")); //$NON-NLS-1$

        setIcon(Icon.WHITE_FLAG);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        EVENTBUS.post(new IssResignCommand());
    }
}
