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
import de.jskat.util.JSkatResourceBundle;

/**
 * Implements the action for leaving a skat table on ISS
 */
public class TalkEnableAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public TalkEnableAction() {

		putValue(Action.NAME, JSkatResourceBundle.instance().getString("talk")); //$NON-NLS-1$

		setIcon(Icon.CHAT);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		jskat.sendTalkEnabledSignal();
	}
}
