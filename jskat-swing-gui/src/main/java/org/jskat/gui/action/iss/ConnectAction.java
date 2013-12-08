/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.action.iss;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jskat.data.iss.LoginCredentials;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;

/**
 * Implements the action for connecting to ISS
 */
public class ConnectAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public ConnectAction() {

		putValue(Action.NAME, strings.getString("connect_to_iss")); //$NON-NLS-1$

		setActionCommand(JSkatAction.CONNECT_TO_ISS);
		setIcon(Icon.CONNECT_ISS);
	}

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof LoginCredentials) {
			jskat.getIssController().connectToISS(
					(LoginCredentials) e.getSource());
		}
	}
}
