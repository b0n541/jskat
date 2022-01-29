package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

import javax.swing.*;

/**
 * Implements the action for picking up the skat
 */
public class PickUpSkatAction extends AbstractHumanJSkatAction {

    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PickUpSkatAction() {

        putValue(Action.NAME, STRINGS.getString("pick_up_skat")); //$NON-NLS-1$

        setActionCommand(JSkatAction.PICK_UP_SKAT);
        setIcon(Icon.PLAY);
    }
}
