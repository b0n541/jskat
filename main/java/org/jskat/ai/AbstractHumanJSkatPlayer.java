package org.jskat.ai;

import java.awt.event.ActionEvent;

/**
 * Abstract implementation of a human player for JSkat
 */
public abstract class AbstractHumanJSkatPlayer extends AbstractJSkatPlayer {
	@Override
	public final boolean isAIPlayer() {
		return false;
	}

	/**
	 * Informs the human player about an action that was performed
	 * 
	 * @param e
	 *            Action
	 */
	public abstract void actionPerformed(final ActionEvent e);
}
