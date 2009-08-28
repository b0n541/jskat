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
 * Implements the action for starting a new skat series
 */
public class ContinueSkatSeriesAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction(JSkatMaster)
	 */
	public ContinueSkatSeriesAction(JSkatMaster controller,
			JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		super(controller);

		putValue(Action.NAME, strings.getString("continue_series"));
		putValue(Action.SHORT_DESCRIPTION, strings
				.getString("continue_series_tooltip"));
		putValue(Action.SMALL_ICON, null);
		putValue(Action.LARGE_ICON_KEY, null);
		setEnabled(false);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		this.jskat.resumeSkatSeries();
	}
}
