package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssShowCardsCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for sending a show cards signal to ISS
 */
public class ShowCardsAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ShowCardsAction() {

        putValue(NAME, STRINGS.getString("show_cards"));
        setIcon(Icon.PLAY);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        if (e.getSource() instanceof String) {

            EVENTBUS.post(new IssShowCardsCommand((String) e.getSource()));
        }
    }
}
