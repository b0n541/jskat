/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.img.JSkatGraphicRepository;

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
	 * @param bitmaps
	 *            JSkat graphics
	 */
	public AbstractJSkatAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps) {

		this.jskat = controller;
		this.bitmaps = bitmaps;
		setIcons(JSkatGraphicRepository.Icon.BLANK);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.takeCardFromSkat(e);
	}

	protected void setIcons(JSkatGraphicRepository.Icon icon) {
		putValue(
				SMALL_ICON,
				new ImageIcon(bitmaps.getIconImage(icon,
						JSkatGraphicRepository.IconSize.SMALL)));
		putValue(
				LARGE_ICON_KEY,
				new ImageIcon(bitmaps.getIconImage(icon,
						JSkatGraphicRepository.IconSize.BIG)));

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
