/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.human;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for handling click on look into skat button
 */
public class LookIntoSkatAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public LookIntoSkatAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		super(controller, bitmaps);

		putValue(Action.NAME, "Look into skat");
		putValue(Action.SHORT_DESCRIPTION, "Look into skat");

		setActionCommand(JSkatAction.LOOK_INTO_SKAT);
		setIcon(Icon.PLAY);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.triggerHuman(e);
	}
}
