/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.action.main;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jskat.gui.action.AbstractJSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for showing about dialog
 */
public class HelpAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public HelpAction() {

		putValue(Action.NAME, strings.getString("help")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, strings.getString("help_tooltip")); //$NON-NLS-1$

		setIcon(Icon.HELP);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.showHelp();
	}
}
