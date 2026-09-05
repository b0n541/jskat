package org.jskat.gui.action.main;

import org.jskat.control.command.table.NextReplayMoveCommand;
import org.jskat.control.gui.action.JSkatActionEvent;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for doing the next move in a replay
 */
public class NextReplayMoveAction extends AbstractJSkatAction {

    /**
     * @see AbstractJSkatAction#AbstractJSkatAction()
     */
    public NextReplayMoveAction() {

        putValue(NAME, STRINGS.getString("nextReplayMove"));
        putValue(SHORT_DESCRIPTION, STRINGS.getString("nextReplayMoveTooltip"));

        setIcon(Icon.NEXT);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        EVENTBUS.post(new NextReplayMoveCommand(e.getActionCommand()));
    }
}
