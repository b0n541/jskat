package org.jskat.gui.table;

import javax.swing.ActionMap;

/**
 * Panel for showing informations about opponents
 */
public class OpponentPanel extends AbstractHandPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractHandPanel#AbstractHandPanel(ActionMap, int, boolean)
	 */
	public OpponentPanel(ActionMap actions, int maxCards, boolean showIssWidgets) {

		super(actions, maxCards, showIssWidgets);
	}
}
