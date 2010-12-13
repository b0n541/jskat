/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for showing the ISS homepage
 */
public class OpenIssHomepageAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public OpenIssHomepageAction() {

		putValue(Action.NAME, strings.getString("open_iss_homepage")); //$NON-NLS-1$

		setIcon(Icon.WEB);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.openIssHomepage();
	}
}
