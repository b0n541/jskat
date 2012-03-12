package org.jskat.gui.action.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for starting a new skat series
 */
public class PauseSkatSeriesAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public PauseSkatSeriesAction() {

		putValue(Action.NAME, strings.getString("pause_series")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION,
				strings.getString("pause_series_tooltip")); //$NON-NLS-1$

		setIcon(Icon.PAUSE);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.startSeries();
	}
}
