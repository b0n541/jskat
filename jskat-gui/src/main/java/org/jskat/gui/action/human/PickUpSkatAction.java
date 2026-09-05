package org.jskat.gui.action.human;

import org.jskat.control.gui.action.JSkatAction;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for picking up the skat
 */
public class PickUpSkatAction extends AbstractHumanJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public PickUpSkatAction() {

        putValue(NAME, STRINGS.getString("pickUpSkat"));

        setActionCommand(JSkatAction.PICK_UP_SKAT);
        setIcon(Icon.PLAY);
    }
}
