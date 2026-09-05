package org.jskat.gui.action.main;

import org.jskat.control.command.table.ReplayGameCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for replaying a game
 */
public class ReplayGameAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public ReplayGameAction() {

        putValue(NAME, STRINGS.getString("replayGame"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("replayGameTooltip"));

        setIcon(Icon.FIRST);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new ReplayGameCommand(e.getActionCommand()));
    }
}
