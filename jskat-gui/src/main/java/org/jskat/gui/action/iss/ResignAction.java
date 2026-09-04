package org.jskat.gui.action.iss;

import org.jskat.control.command.iss.IssResignCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for resigning a game on ISS
 */
public class ResignAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ResignAction() {

        putValue(NAME, STRINGS.getString("resign"));
        setIcon(Icon.WHITE_FLAG);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        if (e.getSource() instanceof String) {

            EVENTBUS.post(new IssResignCommand((String) e.getSource()));
        }
    }
}
