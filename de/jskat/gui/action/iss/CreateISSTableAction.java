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
import javax.swing.ImageIcon;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Implements the action for creating a skat table on ISS
 */
public class CreateISSTableAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public CreateISSTableAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		super(controller);

		putValue(Action.NAME, "Create table");
		putValue(Action.SHORT_DESCRIPTION, "Create table on ISS");
		putValue(
				SMALL_ICON,
				new ImageIcon(bitmaps.getIconImage(
						JSkatGraphicRepository.Icon.BLANK,
						JSkatGraphicRepository.IconSize.SMALL)));
		putValue(
				LARGE_ICON_KEY,
				new ImageIcon(bitmaps.getIconImage(
						JSkatGraphicRepository.Icon.BLANK,
						JSkatGraphicRepository.IconSize.BIG)));
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.getISSController().requestTableCreation();
	}
}
