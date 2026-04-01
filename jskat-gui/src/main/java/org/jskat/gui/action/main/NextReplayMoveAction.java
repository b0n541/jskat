package org.jskat.gui.action.main;

import org.jskat.control.JSkatMaster;
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

        putValue(NAME, STRINGS.getString("next_replay_step"));
        putValue(SHORT_DESCRIPTION,
                STRINGS.getString("next_replay_step_tooltip"));

        setIcon(Icon.FAST_FORWARD);
    }

    @Override
    public void actionPerformed(JSkatActionEvent e) {

        JSkatMaster.INSTANCE.stepForwardReplay(e.getActionCommand());
    }
}
