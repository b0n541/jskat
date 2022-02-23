package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssShowCardsCommand;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Implements the action for resigning a game on ISS
 */
public class ShowCardsAction extends AbstractJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ShowCardsAction() {

        putValue(Action.NAME, STRINGS.getString("show_cards")); //$NON-NLS-1$

        setIcon(Icon.PLAY);
    }

    /**
     * @see AbstractAction#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        EVENTBUS.post(new IssShowCardsCommand());
    }
}
