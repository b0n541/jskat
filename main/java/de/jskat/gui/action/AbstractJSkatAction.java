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
import de.jskat.util.JSkatResourceBundle;

/**
 * Defines an abstract action for JSkat
 */
public abstract class AbstractJSkatAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Controller class
	 */
	protected JSkatMaster jskat;
	/**
	 * JSkat graphics repository
	 */
	protected JSkatGraphicRepository bitmaps;
	/**
	 * JSkat i18n strings
	 */
	protected JSkatResourceBundle strings;

	/**
	 * Constructor
	 * 
	 * @param newBitmaps
	 *            JSkat graphics
	 */
	public AbstractJSkatAction() {

		strings = JSkatResourceBundle.instance();
		bitmaps = JSkatGraphicRepository.instance();
		jskat = JSkatMaster.instance();

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
}
