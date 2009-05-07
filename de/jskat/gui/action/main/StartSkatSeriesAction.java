/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;

/**
 * Implements the action for starting a new skat series
 */
public class StartSkatSeriesAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public StartSkatSeriesAction(JSkatMaster controller) {
		
		super(controller);
		
		putValue(Action.NAME, "Start skat series");
		putValue(Action.SHORT_DESCRIPTION, "Starts a new skat series");
		setEnabled(false);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		this.jskat.startSeries();
	}
}
