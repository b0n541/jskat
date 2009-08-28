/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.main;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Implements the action for showing about dialog
 */
public class LicenseAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public LicenseAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		super(controller);

		putValue(Action.NAME, strings.getString("license"));
		putValue(Action.SHORT_DESCRIPTION, strings.getString("license_tooltip"));
		putValue(Action.SMALL_ICON, new ImageIcon(bitmaps.getIconImage(
				JSkatGraphicRepository.Icon.LICENSE,
				JSkatGraphicRepository.IconSize.SMALL)));
		putValue(Action.LARGE_ICON_KEY, new ImageIcon(bitmaps.getIconImage(
				JSkatGraphicRepository.Icon.LICENSE,
				JSkatGraphicRepository.IconSize.BIG)));
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.showLicense();
	}
}
