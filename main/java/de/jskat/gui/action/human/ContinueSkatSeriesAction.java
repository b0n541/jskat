/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.human;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for starting a new skat series
 */
public class ContinueSkatSeriesAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public ContinueSkatSeriesAction() {

		putValue(NAME, strings.getString("continue_series")); //$NON-NLS-1$
		putValue(SHORT_DESCRIPTION,
				strings.getString("continue_series_tooltip")); //$NON-NLS-1$

		setIcon(Icon.PLAY);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.resumeSkatSeries();
	}
}
