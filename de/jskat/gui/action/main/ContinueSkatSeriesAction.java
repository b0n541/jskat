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
public class ContinueSkatSeriesAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public ContinueSkatSeriesAction(JSkatMaster controller) {
		
		super(controller);
		
		putValue(Action.NAME, "Play next game");
		putValue(Action.SHORT_DESCRIPTION, "Starts the next game");
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		this.jskat.resumeSkatSeries();
	}
}
