/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;

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
	 * @param newBitmaps
	 *            JSkat graphics
	 */
	public AbstractJSkatAction(JSkatMaster controller,
			JSkatGraphicRepository newBitmaps) {

		this.jskat = controller;
		this.bitmaps = newBitmaps;
		setIcon(Icon.BLANK);
	}

	protected void setIcon(JSkatGraphicRepository.Icon icon) {
		putValue(
				SMALL_ICON,
				new ImageIcon(bitmaps.getIconImage(icon,
						JSkatGraphicRepository.IconSize.SMALL)));
		putValue(
				LARGE_ICON_KEY,
				new ImageIcon(bitmaps.getIconImage(icon,
						JSkatGraphicRepository.IconSize.BIG)));
	}

	protected void setActionCommand(JSkatAction action) {
		putValue(ACTION_COMMAND_KEY, action.toString());
	}

	/**
	 * Controller class
	 */
	protected JSkatMaster jskat;
	/**
	 * JSkat graphics repository
	 */
	protected JSkatGraphicRepository bitmaps;
}
