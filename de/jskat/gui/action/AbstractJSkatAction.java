/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.jskat.control.JSkatMaster;

/**
 * Defines an abstract action for JSkat
 */
public abstract class AbstractJSkatAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param controller
	 *            JSkat master
	 */
	public AbstractJSkatAction(JSkatMaster controller) {

		this.jskat = controller;
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.takeCardFromSkat(e);
	}

	/**
	 * Controller class
	 */
	protected JSkatMaster jskat;
}
