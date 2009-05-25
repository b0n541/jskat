/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import de.jskat.control.JSkatMaster;
import de.jskat.data.iss.ISSLoginCredentials;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.action.JSkatActions;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.iss.ISSLoginPanel;

/**
 * Implements the action for showing about dialog
 */
public class ConnectToISSAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public ConnectToISSAction(JSkatMaster controller, JSkatGraphicRepository bitmaps) {
		
		super(controller);
		
		putValue(Action.NAME, "Connect to ISS");
		putValue(Action.SHORT_DESCRIPTION, "Opens a connection to ISS");
		putValue(Action.SMALL_ICON, new ImageIcon(bitmaps.getIconImage(
				JSkatGraphicRepository.Icon.CONNECT_ISS, 
				JSkatGraphicRepository.IconSize.SMALL)));
		putValue(Action.LARGE_ICON_KEY, new ImageIcon(bitmaps.getIconImage(
				JSkatGraphicRepository.Icon.CONNECT_ISS, 
				JSkatGraphicRepository.IconSize.BIG)));
		putValue(Action.ACTION_COMMAND_KEY, JSkatActions.CONNECT_TO_ISS.toString());
	}
	
	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		System.out.println(e.getSource() + " " + e.getActionCommand());
		
		this.jskat.connectToISS(e);
	}
}
