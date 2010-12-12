/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.util.ResourceBundle;

import javax.swing.ActionMap;

import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Panel for showing informations about opponents
 */
class OpponentPanel extends HandPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HandPanel#HandPanel(ActionMap, JSkatGraphicRepository,
	 *      ResourceBundle, int)
	 */
	OpponentPanel(ActionMap actions, JSkatGraphicRepository jskatBitmaps,
			ResourceBundle jskatStrings, int maxCards) {

		super(actions, jskatBitmaps, jskatStrings, maxCards);
	}
}
