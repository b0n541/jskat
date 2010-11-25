/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.iss;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Implements the action for joining a skat table on ISS
 */
public class JoinISSTableAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public JoinISSTableAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		super(controller, bitmaps);

		putValue(Action.NAME, "Join table");
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() instanceof String) {

			this.jskat.getISSController().joinTable((String) e.getSource());
		}
	}
}
